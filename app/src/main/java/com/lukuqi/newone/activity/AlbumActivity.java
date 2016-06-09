package com.lukuqi.newone.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lukuqi.newone.R;
import com.lukuqi.newone.adapter.AlbumRecyclerAdapter;
import com.lukuqi.newone.bean.UserBase;
import com.lukuqi.newone.bean.UserInfo;
import com.lukuqi.newone.http.OkHttpUtils;
import com.lukuqi.newone.util.ConstantVar;
import com.lukuqi.newone.util.IP;
import com.lukuqi.newone.util.SpacesItemDecoration;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 我的照片
 */
public class AlbumActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AlbumRecyclerAdapter adapter;
    private Context context;
    private List<String> datas = new ArrayList<>();

    private List<String> lists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initData();
        initView();

    }

    private void initView() {
        context = this;
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_album);
        adapter = new AlbumRecyclerAdapter(context, datas);
        adapter.setOnItemClickListener(new AlbumRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                System.out.println("Item clicked: " + position);
                Intent intent = new Intent(AlbumActivity.this, PhotoViewerActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putParcelableArrayList();
                intent.putStringArrayListExtra("datas", (ArrayList<String>) datas);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        //瀑布流对象
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        //设置item间距
        SpacesItemDecoration decoration = new SpacesItemDecoration(16);
        recyclerView.addItemDecoration(decoration);
    }

    private void initData() {
        OkHttpUtils okHttpUtils = OkHttpUtils.getInstance(context);
        String url = IP.IP_PARENT + "/getImage";
        SharedPreferences sharedPreferences = getSharedPreferences(ConstantVar.USER_TEL, MODE_PRIVATE);
        String tel = sharedPreferences.getString("tel", "null");
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("tel", tel);
        paramsMap.put("host",IP.HOST);
        okHttpUtils.postAsyn(url, paramsMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("IOException: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                System.out.println("res: " + res);
                Gson gson = new Gson();
                UserBase<String> user = gson.fromJson(res, new TypeToken<UserBase<String>>() {
                }.getType());

                lists = user.getMessage();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.addPicture(lists);
                    }
                });

                if (user.getCode().equals("10000")) {
                    for (String list : lists) {
                        System.out.println("list: " + list);
                    }
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

}
