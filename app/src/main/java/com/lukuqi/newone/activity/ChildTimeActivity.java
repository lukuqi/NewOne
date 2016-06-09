package com.lukuqi.newone.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lukuqi.newone.application.MyApplication;
import com.lukuqi.newone.bean.ChildTime;
import com.lukuqi.newone.bean.UserBase;
import com.lukuqi.newone.R;
import com.lukuqi.newone.adapter.ChildTimeRecyclerAdapter;
import com.lukuqi.newone.bean.UserInfo;
import com.lukuqi.newone.http.OkHttpUtils;
import com.lukuqi.newone.util.CheckDir;
import com.lukuqi.newone.util.ConstantVar;
import com.lukuqi.newone.util.IP;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 孩子的成长记录
 */
public class ChildTimeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<HashMap<String, String>> datas = new ArrayList<>();
    private ChildTimeRecyclerAdapter adapter;
    private Context context;
    private Handler handler;

    private ImageLoader imageLoader;
    private CircleImageView icon;
    private TextView name;
    private String str_name;
    private final String icon_url = IP.HOST + "/Upload/";
    private String icon_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_time);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initView();
        initData();

    }

    private void initData() {

        HashMap<String, String> paramsMap = new HashMap<>();
        OkHttpUtils okHttpUtils = OkHttpUtils.getInstance(this);

        SharedPreferences sharedPreferences = getSharedPreferences(ConstantVar.USER_TEL, MODE_PRIVATE);
        String tel = sharedPreferences.getString("tel", "null");
        final String url = IP.IP_PARENT + "/getTimeline";
        paramsMap.put("tel", tel);
        okHttpUtils.postAsyn(IP.IP_PARENT + "/getInfo", paramsMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Gson gson = new Gson();
                UserBase<UserInfo> user = gson.fromJson(res, new TypeToken<UserBase<UserInfo>>() {
                }.getType());

                final List<UserInfo> userInfos = user.getMessage();
                if (user.getCode().equals("10000")) {
//                    imageLoader.displayImage(icon_url + userInfos.get(0).getIcon(), icon);
                    icon_path = icon_url + userInfos.get(0).getIcon();
                    str_name = userInfos.get(0).getName();
                    System.out.println("str_name:" + str_name + "icon_url: " + userInfos.get(0).getIcon());
                    Message message = new Message();
                    message.what = 0x02;
                    handler.sendMessage(message);
                }
            }
        });
        okHttpUtils.postAsyn(url, paramsMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                System.out.println("联合表： " + res);
                Gson gson = new Gson();
                UserBase<ChildTime> user = gson.fromJson(res, new TypeToken<UserBase<ChildTime>>() {
                }.getType());
                final List<ChildTime> childTimes = user.getMessage();
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm:ss");

                if (user.getCode().equals("10000")) {
//                    for (int i = childTimes.size() - 1; i > -1; i--) {
//                        HashMap<String, String> hashMap = new HashMap<>();
//                        hashMap.put("name", childTimes.get(i).getName());
//                        hashMap.put("icon", childTimes.get(i).getIcon());
//                        hashMap.put("content", childTimes.get(i).getContent());
//                        hashMap.put("picture", childTimes.get(i).getPicture());
//                        hashMap.put("comment", childTimes.get(i).getComment());
//                        hashMap.put("favorite", childTimes.get(i).getFavorite());
//                        String date = sdf.format(new Date(Integer.parseInt(childTimes.get(i).getCreate_time()) * 1000L));
//                        hashMap.put("time", date);
//                        datas.add(hashMap);
//                    }
                    for (ChildTime childTime : childTimes) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("name", childTime.getName());
                        hashMap.put("icon", childTime.getIcon());
                        hashMap.put("content", childTime.getContent());
                        hashMap.put("picture", childTime.getPicture());
                        hashMap.put("comment", childTime.getComment());
                        hashMap.put("favorite", childTime.getFavorite());
                        String date = sdf.format(new Date(Integer.parseInt(childTime.getCreate_time()) * 1000L));
                        hashMap.put("time", date);
                        datas.add(hashMap);
                    }
//                    for (int i = 0; i < 3; i++) {
//                        HashMap<String, String> hashMap = new HashMap<>();
//                        hashMap.put("name", "werewr");
//                        hashMap.put("content", "sdfdsf");
//                        hashMap.put("time", "sdfsdfdsf");
//                        System.out.println("datas---i: " + i);
//                        datas.add(hashMap);
//                    }
                    Message message = new Message();
                    message.what = 0x01;
                    handler.sendMessage(message);
                }
            }
        });
    }

    private void initView() {

        context = this;

        name = (TextView) findViewById(R.id.tv_time_name);
        icon = (CircleImageView) findViewById(R.id.cIamge_icon);

        imageLoader = ImageLoader.getInstance();
        imageLoader.init(MyApplication.setConfiguration(context));

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_child_time);

        adapter = new ChildTimeRecyclerAdapter(context, datas);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        handler = new Handler() {
            int count = 0;

            @Override
            public void handleMessage(Message msg) {
                //操作界面
                switch (msg.what) {
                    case 0x01:
                        System.out.println("datas: " + datas.size());
//                        adapter.addItem(datas);
                        System.out.println(count + "-------------------------------");
                        count++;
                        break;
                    case 0x02:
                        name.setText(str_name);
                        imageLoader.displayImage(icon_path, icon);
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_time, menu);
//        MenuItem item = menu.add(Menu.NONE, Menu.FIRST, 100, "发表");
//        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            adapter.datas.clear();
            adapter.notifyDataSetChanged();
            if (requestCode == 1) {
                HashMap<String, String> paramsMap = new HashMap<>();
                OkHttpUtils okHttpUtils = OkHttpUtils.getInstance(this);

                SharedPreferences sharedPreferences = getSharedPreferences(ConstantVar.USER_TEL, MODE_PRIVATE);
                String tel = sharedPreferences.getString("tel", "null");
                final String url = IP.IP_PARENT + "/getTimeline";
                paramsMap.put("tel", tel);
//                HashMap<String, String> str = (HashMap<String, String>) data.getSerializableExtra("data");
//                System.out.println("sdfdfsd-------" + str);
//                String local = data.getStringExtra("local");
//                System.out.println("local:" + local);
////                Bundle bundle = getIntent().getExtras();
////                Serializable map = (Serializable) bundle.get("data");
//                HashMap<String, String> hashMap = new HashMap<>();
//
//
////                hashMap.put("icon", );
//                hashMap.put("content", str.get("content"));
////                hashMap.put("picture", childTime.getPicture());
////                hashMap.put("comment", childTime.getComment());
////                hashMap.put("favorite", childTime.getFavorite());
//                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm:ss");
//                String date = sdf.format(new Date(Integer.parseInt(str.get("time")) * 1000L));
//                hashMap.put("time", date);
//                adapter.addItem(hashMap);
                okHttpUtils.postAsyn(url, paramsMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String res = response.body().string();
                        System.out.println("联合表： " + res);
                        Gson gson = new Gson();
                        UserBase<ChildTime> user = gson.fromJson(res, new TypeToken<UserBase<ChildTime>>() {
                        }.getType());
                        final List<ChildTime> childTimes = user.getMessage();
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm:ss");

//                        if (user.getCode().equals("10000")) {
////                            HashMap<String, String> hashMap = new HashMap<>();
////                            hashMap.put("name", childTimes.get(childTimes.size() - 1).getName());
////                            hashMap.put("icon", childTimes.get(childTimes.size() - 1).getIcon());
////                            hashMap.put("content", childTimes.get(childTimes.size() - 1).getContent());
////                            hashMap.put("picture", childTimes.get(childTimes.size() - 1).getPicture());
////                            hashMap.put("comment", childTimes.get(childTimes.size() - 1).getComment());
////                            hashMap.put("favorite", childTimes.get(childTimes.size() - 1).getFavorite());
////                            String date = sdf.format(new Date(Integer.parseInt(childTimes.get(childTimes.size() - 1).getCreate_time()) * 1000L));
////                            hashMap.put("time", date);
////                            datas.add(hashMap);
//                            HashMap<String, String> hashMap = new HashMap<>();
//                            hashMap.put("name", childTimes.get(0).getName());
//                            hashMap.put("icon", childTimes.get(0).getIcon());
//                            hashMap.put("content", childTimes.get(0).getContent());
//                            hashMap.put("picture", childTimes.get(0).getPicture());
//                            hashMap.put("comment", childTimes.get(0).getComment());
//                            hashMap.put("favorite", childTimes.get(0).getFavorite());
//                            String date = sdf.format(new Date(Integer.parseInt(childTimes.get(childTimes.size() - 1).getCreate_time()) * 1000L));
//                            hashMap.put("time", date);
//                            datas.add(hashMap);
//                        }
                        for (ChildTime childTime : childTimes) {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("name", childTime.getName());
                            hashMap.put("icon", childTime.getIcon());
                            hashMap.put("content", childTime.getContent());
                            hashMap.put("picture", childTime.getPicture());
                            hashMap.put("comment", childTime.getComment());
                            hashMap.put("favorite", childTime.getFavorite());
                            String date = sdf.format(new Date(Integer.parseInt(childTime.getCreate_time()) * 1000L));
                            hashMap.put("time", date);
                            datas.add(hashMap);
                        }
                    }
                });
            }
            adapter.notifyDataSetChanged();
            adapter.addItemNotify(datas);

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_publish:
                startActivityForResult(new Intent(this, PublishTimeActivity.class), 1);
//                startActivity(new Intent(this, PublishTimeActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}
