package com.lukuqi.newone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lukuqi.newone.R;
import com.lukuqi.newone.adapter.ChildTimeRecyclerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChildTimeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<HashMap<String, String>> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_time);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initData();
        initView();
    }

    private void initData() {
        datas = new ArrayList<>();
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("name", "yidianban");
        hashMap.put("content", "你好一点半先生！");
        hashMap.put("time", "2016/4/25");
        datas.add(hashMap);
        datas.add(hashMap);
        datas.add(hashMap);
        datas.add(hashMap);
        datas.add(hashMap);
        datas.add(hashMap);
        datas.add(hashMap);
        datas.add(hashMap);
        datas.add(hashMap);
        datas.add(hashMap);
        datas.add(hashMap);
        datas.add(hashMap);
        datas.add(hashMap);
        datas.add(hashMap);
        datas.add(hashMap);
        datas.add(hashMap);
        datas.add(hashMap);
        datas.add(hashMap);
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_child_time);
        ChildTimeRecyclerAdapter adapter = new ChildTimeRecyclerAdapter(this, datas);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_time,menu);
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
                startActivity(new Intent(this,PublishTimeActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}
