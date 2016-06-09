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
            doc = Jsoup.parse(url, 5000); //设置Jsoup网页地址，访问延迟5000毫秒
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("doc: " + doc);
        if(doc == null )
            return null;
        Elements divs = doc.getElementsByClass("newsList");//获取“newsList”节点元素集
//                    System.out.println("divs: " + divs);
        Document doc_lis = Jsoup.parse(divs.toString());//解析节点元素
//                    System.out.println("doc_lis:" + doc_lis);
//                    List<HashMap<String, String>> list = new ArrayList<>();
        List<HashMap<String, String>> list = new ArrayList<>();
        Elements h3 = doc_lis.getElementsByTag("h3");//获取“h3”标签元素集
//        Elements contents = doc_lis.getElementsByClass("newsDigest");
        Elements source = doc_lis.getElementsByClass("sourceDate");//解析节点元素
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
        /*
        *
        * 添加解析内容 title：文章标题 url:网页链接地址 content:内容 image:图片链接地址 source：文章来源
        * */
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
