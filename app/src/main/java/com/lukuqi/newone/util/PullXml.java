package com.lukuqi.newone.util;

import android.content.Context;


import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Pull解析xml工具类 抽象类
 * <p/>
 * Created by mr.right on 2016/4/3.
 *
 * @param <T> 实体类
 */
public abstract class PullXml<T> {

    private XmlPullParser xmlPullParser;    //XmlPullParser势力
    private Context context;                //上下文
    private T cls;                          //节点实体类 Country Province City
    public List<T> lists;                    //节点实体类集合

    /**
     * 抽象函数 重写节点保存操作
     *
     * @return 节点实体类
     */
    public abstract T saveTag();

    public PullXml(Context context, T cls, XmlPullParser xmlPullParser) {
        this.context = context;
        this.cls = cls;
        this.xmlPullParser = xmlPullParser;
    }


    /**
     * 解析xml文件
     *
     * @param xml_path 解析的xml文件名称 eg：xxx.xml
     * @param tag      xml节点匹配名称
     * @return 节点实体类集合
     * @throws Exception
     */
    public List<T> pulltoXML(String xml_path, String tag) throws Exception {
        InputStream xml = context.getResources().getAssets().open(xml_path);
        xmlPullParser.setInput(xml, "UTF-8");           // xml为参数，将xml文件以输入流的形式作为参数执行本方法，第2个参数是字符编码
        int eventType = xmlPullParser.getEventType();   // getEventType方法返回int结果，对应的结果常量有START_TAG, END_TAG, TEXT等
        lists = null;
        cls = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {// 当结果不为END_DOCUMENT时循环

            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
                    // 当读取到文件头时，创建节点实例
                    lists = new ArrayList<>();
                    //System.out.println("START_DOCUMENT");
                    break;
                case XmlPullParser.START_TAG:
                    // 通过XmlPullParser的getName方法可以得到读取到的节点名称，匹配，则创建当前所需的实例，为把该节点的id属性值赋值到该实例对应的属性值中
                    if (tag.equals(xmlPullParser.getName())) {
                        cls = saveTag(); //保存节点和节点属性操作 函数
                    }
                    //System.out.println("START_TAG");
                    break;
                case XmlPullParser.END_TAG:
                    //System.out.println("END_TAG");
                    // 当读取到结束的节点时，添加到lists中，并设置该实例为null
                    if (tag.equals(xmlPullParser.getName())) {
                        lists.add(cls);
                        cls = null;
                    }
                    break;
                default:
                    break;
            }
            // 继续读取下一个节点
            eventType = xmlPullParser.next();

        }
        System.out.println("lists:" + lists);
        return lists;
    }

}
