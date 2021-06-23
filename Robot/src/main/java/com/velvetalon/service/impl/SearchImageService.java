package com.velvetalon.service.impl;

import com.velvetalon.entity.ImageSearchResult;
import com.velvetalon.utils.HttpSendUtil;
import com.velvetalon.utils.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @describe: 文件描述
 * @author: Velvetalon
 * HISTORY:
 * <p>
 * 2021/6/10 9:45 : 创建文件
 */
public class SearchImageService {
    private static Logger logger = LoggerFactory.getLogger(SearchImageService.class);

    private final static String IMAGE_SEARCH_API = "https://saucenao.com/search.php";

    public static List<ImageSearchResult> search( String imageUrl ){
        HashMap<String, String> header = new HashMap<>();
        String boundary = "--" + System.currentTimeMillis();
        header.put("Content-Type", "multipart/form-data; boundary=" + boundary);
        String param = String.format("--%s\nContent-Disposition: form-data; name=\"url\"\n\n%s\n--%s--",
                boundary, imageUrl, boundary);
        String resp = HttpSendUtil.httpPost(IMAGE_SEARCH_API, header, param);

        Document document = Jsoup.parse(resp);
        Elements results = document.getElementsByClass("result");
        List<ImageSearchResult> isrList = new ArrayList<>();
        int i = 0;
        for (Element result : results) {
            if ("result-hidden-notification".equals(result.id())) {
                break;
            }
            if (result.text().contains("Creator:")) {
                i++;
                continue;
            }

            ImageSearchResult isr = new ImageSearchResult();
            try {
                isr.setTitle(result.getElementsByClass("resulttitle").get(0).text());
                isr.setUrl(result.getElementsByClass("resultcontentcolumn").get(0).getElementsByTag("a").get(0).attr("href"));
                isr.setImageUrl(result.getElementById("resImage" + i++).attr("src"));
                isr.setSimilarity(result.getElementsByClass("resultsimilarityinfo").text());
                Elements member = result.getElementsByClass("resultcontentcolumn").get(0).getElementsByTag("a");
                isr.setMember(member.get(member.size() > 2 ? 2 : 1).text());
                isrList.add(isr);
            } catch (Exception e) {
                logger.error("搜索图片出错，图片Url：" + imageUrl + " 检索下标：" + i + " 调用栈信息如下：");
                logger.error(StringUtil.array2String(e.getStackTrace()));
            }
        }
        return isrList;
    }
}
