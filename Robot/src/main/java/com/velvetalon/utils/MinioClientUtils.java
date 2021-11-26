package com.velvetalon.utils;

import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.Bucket;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * minio工具类
 *
 * @author shenhuancheng
 */
@Component
public class MinioClientUtils {

    private static final Logger log = LoggerFactory.getLogger(MinioClientUtils.class);

    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.access-key}")
    private String accessKey;
    @Value("${minio.secret-key}")
    private String secretKey;
    @Value("${minio.def-bucket-name}")
    private String defBucketName;

    public MinioClientUtils mi=null;
    public MinioClient client=null;

//	public MinioClientUtils(){
//        System.out.println("wtf");
//	}

	@PostConstruct
    public void init(){
        try{
            client = new MinioClient(endpoint,
                    accessKey, secretKey);
            mi=this;
            /*创建统一的文档存放更目录（没有才会创建）*/
            mi.createBucket(defBucketName);
            log.info("MinIo初始化成功！");
        }catch (Exception E){
            log.error(E.toString());
        }
    }
	/**
	 * 创建bucket
	 *
	 * @param bucketName bucket名称
	 */
	@SneakyThrows
	public void createBucket(String bucketName) {
		if (!client.bucketExists(bucketName)) {
			client.makeBucket(bucketName);
		}
	}

	/**
	 * 获取全部bucket
	 * <p>
	 * https://docs.minio.io/cn/java-client-api-reference.html#listBuckets
	 */
	@SneakyThrows
	public List<Bucket> getAllBuckets() {
		return client.listBuckets();
	}

	/**
	 * 根据bucketName获取信息
	 *
	 * @param bucketName bucket名称
	 */
	@SneakyThrows
	public Optional<Bucket> getBucket(String bucketName) {
		return client.listBuckets().stream().filter(b -> b.name().equals(bucketName)).findFirst();
	}

	/**
	 * 根据bucketName删除信息
	 *
	 * @param bucketName bucket名称
	 */
	@SneakyThrows
	public void removeBucket(String bucketName) {
		client.removeBucket(bucketName);
	}

	/**
	 * 获取文件外链
	 *
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param expires    过期时间 <=7
	 * @return url
	 */
	@SneakyThrows
	public String getObjectURL(String bucketName, String objectName, Integer expires) {
		if(expires == -1)
			return client.getObjectUrl(bucketName, objectName);
		else
			return client.presignedGetObject(bucketName, objectName, expires);
	}

	/**
	 * 获取文件
	 *
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @return 二进制流
	 */
	@SneakyThrows
	public InputStream getObject(String bucketName, String objectName) {
		return client.getObject(bucketName, objectName);
	}

	/**
	 * 上传文件
	 *
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @param stream     文件流
	 * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#putObject
	 */
	public void putObject(String bucketName, String objectName, InputStream stream) throws Exception {
		client.putObject(bucketName, objectName, stream, stream.available(), "application/octet-stream");
	}

	/**
	 * 上传文件
	 *
	 * @param bucketName  bucket名称
	 * @param objectName  文件名称
	 * @param stream      文件流
	 * @param size        大小
	 * @param contextType 类型
	 * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#putObject
	 */
	public void putObject(String bucketName, String objectName, InputStream stream, long size, String contextType) throws Exception {
		client.putObject(bucketName, objectName, stream, size, contextType);
	}

	/**
	 * 获取文件信息
	 *
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @throws Exception
	 */
	public ObjectStat getObjectInfo(String bucketName, String objectName) throws Exception {
		return client.statObject(bucketName, objectName);
	}
	/**
	 * 验证文件是否存在。0 不存在，1 存在，-1 io错误，-2 其他错误
	 *
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @throws Exception
	 */
	public int is_existence(String bucketName, String objectName){
		int existence=0;
		try {
			ObjectStat os=mi.getObjectInfo(bucketName, objectName);
			existence=1;
			log.info("文件存在！");
		}catch (ErrorResponseException e){
			existence=0;
			log.info("文件或目录不存在！");
		}catch(IOException e1){
			existence=-1;
			log.error(e1.toString());
			log.info("文档服务器连接异常");
		}catch(Exception e2){
			existence=-2;
			log.error(e2.toString());
			log.info("验证文件时出现异常！");
		}
		return existence;
	}
	/**
	 * 删除文件
	 *
	 * @param bucketName bucket名称
	 * @param objectName 文件名称
	 * @throws Exception https://docs.minio.io/cn/java-client-api-reference.html#removeObject
	 */
	@SneakyThrows
	public void removeObject(String bucketName, String objectName) throws Exception {
		client.removeObject(bucketName, objectName);
	}

	/**
	 * 上传文件。
     * 支持上传无文件名的文件。
	 *
	 */
	public String upload( InputStream inputStream, String bucketName) {
		try {
			String newName = bucketName + "/" + DateUtils.str(new Date()) + "/"+ UUIDUtil.next();
			putObject(bucketName, newName, inputStream);
			return newName;
		} catch (Exception e) {
			String str=e.toString();
			log.error(str);
			return null;
		}
	}

	public String upload(InputStream io,String bucketName, String file_path_name) {
		try {
			putObject(bucketName, file_path_name, io);
		} catch (Exception e) {
			String str=e.toString();
			log.error(str);
			return "str";
		}
		return "上传成功";
	}


    /**
    * 下载
	 */
	public InputStream download(String bucketName, String fileName) {
		InputStream inputStream=null;
		try{
			 inputStream = getObject(bucketName,fileName);
		} catch (Exception e) {
			log.error("文件读取异常", e);
		}
		return inputStream;
	}
}
