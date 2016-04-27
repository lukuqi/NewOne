package com.lukuqi.newone.http;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 解析网页工具类
 * Created by mr.right on 2016/4/20.
 */
public class ParseHtml {

    /**
     * 解析网页
     *
     * @param url 解析网页的地址
     * @return
     */
    public static List<HashMap<String, String>> getHtml(URL url) {
        Document doc = null;
        try {
            doc = Jsoup.parse(url, 5000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements divs = doc.getElementsByClass("newsList");
//                    System.out.println("divs: " + divs);
        Document doc_lis = Jsoup.parse(divs.toString());
//                    System.out.println("doc_lis:" + doc_lis);
//                    List<HashMap<String, String>> list = new ArrayList<>();
        List<HashMap<String, String>> list = new ArrayList<>();
        Elements h3 = doc_lis.getElementsByTag("h3");
//        Elements contents = doc_lis.getElementsByClass("newsDigest");
        Elements source = doc_lis.getElementsByClass("sourceDate");
        Elements contents = doc_lis.select("div[class=clearfix]");
//                    Elements contents = doc_lis.getElementsByClass("newsDigest");
//                    Elements source = doc_lis.getElementsByClass("sourceDate");
//                    for (int i = 0, j = 0, k = 0; i < h3.size() && j < contents.size() && k < source.size(); i++, j++, k++) {
//                        HashMap<String, String> hashMap = new HashMap<>();
//                        hashMap.put("title", h3.get(i).getElementsByTag("a").text());
//                        hashMap.put("url", h3.get(i).getElementsByTag("a").attr("href"));
//                        hashMap.put("content", contents.get(j).getElementsByTag("p").text());
//                        hashMap.put("source", source.get(i).getElementsByTag("span").text());
//                        list.add(i, hashMap);
//                    }
        for (int i = 0; i < h3.size(); i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("title", h3.get(i).getElementsByTag("a").text());
            hashMap.put("url", h3.get(i).getElementsByTag("a").attr("href"));
            hashMap.put("content", contents.get(i).getElementsByClass("newsDigest").text());
            hashMap.put("image", contents.get(i).getElementsByTag("img").attr("src"));
            hashMap.put("source", source.get(i).getElementsByTag("p").text());
            list.add(hashMap);
        }
        return list;
    }
}
