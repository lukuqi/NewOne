package com.lukuqi.newone.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lukuqi.newone.bean.ChildTime;
import com.lukuqi.newone.bean.UserBase;
import com.lukuqi.newone.R;
import com.lukuqi.newone.adapter.ChildTimeRecyclerAdapter;
import com.lukuqi.newone.http.OkHttpUtils;
import com.lukuqi.newone.util.ConstantVar;
import com.lukuqi.newone.util.IP;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChildTimeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<HashMap<String, String>> datas = new ArrayList<>();
    private ChildTimeRecyclerAdapter adapter;
    private Context context;
    private Handler handler;

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
        String url = IP.IP_PARENT + "/getTimeline";
        paramsMap.put("tel", tel);
        okHttpUtils.postAsyn(url, paramsMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Gson gson = new Gson();
                UserBase<ChildTime> user = gson.fromJson(res, new TypeToken<UserBase<ChildTime>>() {
                }.getType());

                final List<ChildTime> childTimes = user.getMessage();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm:ss");

                if (user.getCode().equals("10000")) {
                    for (ChildTime childTime : childTimes) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("name", childTime.getTel());
                        hashMap.put("content", childTime.getContent());
                        String date = sdf.format(new Date(Integer.parseInt(childTime.getCreate_time())*1000L));
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
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_child_time);
        adapter = new ChildTimeRecyclerAdapter(context, datas);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //操作界面
                switch (msg.what) {
                    case 0x01:
                        adapter.addItem(datas);
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_time, menu);
//        MenuItem item = menu.add(Menu.NONE, Menu.FIRST, 100, "发表");
//        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_publish:
                startActivity(new Intent(this, PublishTimeActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}
