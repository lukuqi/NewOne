package com.lukuqi.newone.http;


import android.content.Context;
import android.media.MediaTimestamp;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


import java.io.File;
import java.io.IOException;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * OkHttp 请求工具类
 * <p/>
 * <p/>
 * Created by mr.right on 2016/3/31.
 */
public class OkHttpUtils {
    private static OkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;

    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    public OkHttpUtils(final Context context) {
        //创建OkHttpClient对象
        mOkHttpClient = new OkHttpClient.Builder().connectTimeout(120, TimeUnit.SECONDS).cookieJar(new CookieJar() {
            private final PersistentCookieStore cookieStore = new PersistentCookieStore(context);

            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                if (cookies != null && cookies.size() > 0) {
                    for (Cookie item : cookies) {
                        cookieStore.add(url, item);
                    }
                }
//                System.out.println("saveFromResponse!" + url + "----" + cookies);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                List<Cookie> cookies = cookieStore.get(url);
//                System.out.println("loadForRequest!" + cookies);
                return cookies != null ? cookies : new ArrayList<Cookie>();
            }
        }).build();
    }

    /**
     * 单例模式创建类
     *
     * @param context 上下文
     * @return
     */
    public static OkHttpUtils getInstance(Context context) {
        if (mInstance == null) {
            synchronized (OkHttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtils(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 同步GET请求
     *
     * @param url 请求地址
     * @return Response 返回内容
     * @throws IOException
     */
    private Response getAsyn(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Call call = mOkHttpClient.newCall(request);
        Response response = call.execute();//会抛出IO异常
        return response;
    }

    /**
     * 异步GET请求
     *
     * @param url              请求地址
     * @param responseCallback call对象
     */
    public void getAsyn(String url, Callback responseCallback) {
        Request request = new Request.Builder().url(url).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(responseCallback);
    }


    /**
     * 同步POST请求
     *
     * @param url       请求地址
     * @param paramsMap 请求参数map
     * @throws IOException
     */
    private void postAsyn(String url, HashMap<String, String> paramsMap) throws IOException {
        Request request = postRequest(url, paramsMap);
        mOkHttpClient.newCall(request).execute();
    }

    /**
     * 异步POST请求
     *
     * @param url              请求地址
     * @param paramsMap        请求参数map
     * @param responseCallback call对象
     */
    public void postAsyn(String url, HashMap<String, String> paramsMap, Callback responseCallback) {
        Request request = postRequest(url, paramsMap);
        mOkHttpClient.newCall(request).enqueue(responseCallback);
    }


    /**
     * 异步POST请求
     *
     * @param url  请求地址
     * @param file 上传文件
     * @return
     */
    public void postFileAsyn(String url, HashMap<String, String> paramsMap, File file, Callback responseCallback) {

//        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("icon", file.getName(), RequestBody.create(MediaType.parse("media/type"), file)).build();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        Set<Map.Entry<String, String>> paramsSet = paramsMap.entrySet();
        for (Map.Entry<String, String> param : paramsSet) {
            builder.addFormDataPart(param.getKey(), param.getValue());
        }
        builder.addFormDataPart("icon", file.getName(), RequestBody.create(MediaType.parse("media/type"), file));
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        mOkHttpClient.newCall(request).enqueue(responseCallback);
//        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
//        builder.addPart(RequestBody.create(MEDIA_TYPE_MARKDOWN, file));
//
//        Request request = new Request.Builder().url(url).post(builder.build()).build();
//        mOkHttpClient.newCall(request).enqueue(responseCallback);
    }

    /**
     * 异步POST多文件上传
     *
     * @param url              请求地址
     * @param paramsMap        请求参数map
     * @param files            上传文件list
     * @param responseCallback
     */
    public void postMutilFileAsyn(String url, HashMap<String, String> paramsMap, List<File> files, Callback responseCallback) {

//        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("icon", file.getName(), RequestBody.create(MediaType.parse("media/type"), file)).build();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        Set<Map.Entry<String, String>> paramsSet = paramsMap.entrySet();
        for (Map.Entry<String, String> param : paramsSet) {
            builder.addFormDataPart(param.getKey(), param.getValue());
        }
        for (File file : files) {
            builder.addFormDataPart("image[]", file.getName(), RequestBody.create(MediaType.parse("media/type"), file));
        }
//        builder.addFormDataPart("icon", file.getName(), RequestBody.create(MediaType.parse("media/type"), file));
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        mOkHttpClient.newCall(request).enqueue(responseCallback);
//        MultipartBuilder builder = new MultipartBuilder().type(MultipartBuilder.FORM);
//        builder.addPart(RequestBody.create(MEDIA_TYPE_MARKDOWN, file));
//
//        Request request = new Request.Builder().url(url).post(builder.build()).build();
//        mOkHttpClient.newCall(request).enqueue(responseCallback);
    }

    public void postFormAsyn(String url, HashMap<String, String> paramsMap, Callback responseCallback) {
        FormBody.Builder formBody = new FormBody.Builder();

        Set<Map.Entry<String, String>> paramsSet = paramsMap.entrySet();
        for (Map.Entry<String, String> param : paramsSet) {
            formBody.add(param.getKey(), param.getValue());
        }
        RequestBody requestBody = formBody.build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        mOkHttpClient.newCall(request).enqueue(responseCallback);
    }

    /**
     * POST请求参数函数
     *
     * @param url       请求地址
     * @param paramsMap 请求参数map
     * @return
     */
    private Request postRequest(String url, HashMap<String, String> paramsMap) {
        FormBody.Builder builder = new FormBody.Builder();
        Set<Map.Entry<String, String>> paramsSet = paramsMap.entrySet();
        for (Map.Entry<String, String> param : paramsSet) {
            builder.add(param.getKey(), param.getValue());
        }
//        Request request = new Request.Builder().url(url).post(builder.build()).build();
        return new Request.Builder().url(url).post(builder.build()).build();
    }
}
