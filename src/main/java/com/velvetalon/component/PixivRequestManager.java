package com.velvetalon.component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.velvetalon.entity.PixivImageEntity;
import com.velvetalon.utils.HttpUtil;
import com.velvetalon.utils.StreamUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @describe: Pixiv访问管理器
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/16 14:07 : 创建文件
 */
@Component
public class PixivRequestManager {

    @Autowired
    private HttpProxyManager httpProxyManager;

    @Autowired
    private CookieManager cookieManager;


    private static final SimpleDateFormat SDF_ENCODE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final SimpleDateFormat SDF_DECODE = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss");


    private static final String PIXIV_SEARCH_API = "https://www.pixiv.net/ajax/search/artworks/%s?word=%s&order=date_d&mode=%s&p=%s&s_mode=s_tag&type=all&lang=zh";
    private static final String IMAGE_HOME = "https://www.pixiv.net/artworks/%s";

    private static final String IMAGE_TEMPLATE = "https://i.pximg.net/img-original/img/%s/%s_p0.jpg";

    @SneakyThrows
    public List<PixivImageEntity> search( String keyword, boolean r18 ) throws UnsupportedEncodingException{
        String url = parseUrl(keyword, r18, 1);
        final List<PixivImageEntity> result = new ArrayList<>();
        final boolean[] done = {false};

        HttpUtil.get(url,
                null,
                cookieManager.getCookieMap(),
                httpProxyManager.getHttpProxy(),
                httpResponse -> {
                    cookieManager.updateByHeaders(httpResponse.getHeaders("set-cookie"));
                    JSONObject json = JSON.parseObject(StreamUtil.readAll(httpResponse.getEntity().getContent()));

                    JSONArray data = json.getJSONObject("body").getJSONObject("illustManga").getJSONArray("data");

                    int size = data.size();
                    if (size == 0) {
                        done[0] = true;
                        return null;
                    }

                    // 随机3-5张图
                    int count = new Random().nextInt(2) + 3;
                    Set<Integer> set = new HashSet<>();
                    while (set.size() < count) {
                        set.add(new Random().nextInt(size));
                    }

                    for (Integer index : set) {
                        PixivImageEntity entity = new PixivImageEntity();
                        JSONObject imageJson = data.getJSONObject(index);
                        entity.setTitle(imageJson.getString("title"));
                        entity.setId(imageJson.getString("id"));
                        entity.setMember(imageJson.getString("userName"));
                        entity.setPreviewUrl(imageJson.getString("url"));
                        entity.setUrl(String.format(IMAGE_HOME, imageJson.getString("id")));
                        entity.setR18(r18);

                        String createDate = imageJson.getString("createDate");
                        createDate = createDate.split("\\+")[0];

                        try {
                            String date = SDF_DECODE.format(SDF_ENCODE.parse(createDate));
                            entity.setSourceUrl(String.format(IMAGE_TEMPLATE, date, entity.getId()));
                        } catch (ParseException ignored) {
                        }

                        result.add(entity);
                    }
                    done[0] = true;
                    return null;
                });
        while (!done[0]) {
            Thread.sleep(200);
        }
        return result;
    }



    private static String parseUrl( String keyword, boolean r18, int p ) throws UnsupportedEncodingException{
        keyword = URLEncoder.encode(keyword, "UTF-8");
        String result = String.format(PixivRequestManager.PIXIV_SEARCH_API, keyword, keyword, r18 ? "r18" : "safe", p);
        return result;
    }
}
