package com.lukuqi.newone.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.lukuqi.newone.R;
import com.lukuqi.newone.adapter.NewsRecyclerAdapter;
import com.lukuqi.newone.http.ParseHtml;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 热点新闻界面
 */
public class NewsActivity extends AppCompatActivity {

    private Context context;
    private RecyclerView recyclerView;
    private Handler handler;
    private NewsRecyclerAdapter newsRecyclerAdapter;
    private LinearLayoutManager linearLayoutManager;         //RecyclerView布局管理器
    List<HashMap<String, String>> list = new ArrayList<>();  //初始加载网页内容
    List<HashMap<String, String>> newList;                   //加载更多网页
    private SwipeRefreshLayout swipeRefreshLayout;           //下拉刷新组件
    private int lastVisibleItem;                             //记录recyclerview滑到最低端的位置position
    public static int page = 2;                             //记录加载的页数 初始化为2，表示将加载第二页

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        initView(); //初始化界面
        getHtml();  //解析网页标签

    }

    /**
     * 初始化组件
     */
    private void initView() {
        context = this;
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_news);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateHtml(page);
                page++;
            }
        });

        newsRecyclerAdapter = new NewsRecyclerAdapter(context, list);
        newsRecyclerAdapter.setOnItemClickListener(new NewsRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                list.get(position).get("url");
                Intent intent = new Intent(NewsActivity.this, NewsDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", list.get(position).get("url"));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        //RecyclerView添加滑动监听事件
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                                System.out.println("dx: " + dx + "dy: " + dy);
                if (linearLayoutManager != null) {
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                }

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, final int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newsRecyclerAdapter != null && lastVisibleItem != 0 && newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1 == newsRecyclerAdapter.getItemCount() && list != null) {
                    swipeRefreshLayout.setRefreshing(true);
                    System.out.println("加载更多……");
                    getMoreHtml(page);
                    page++;
                }
            }
        });
        recyclerView.setAdapter(newsRecyclerAdapter);
        //为RecyclerView设置线性布局
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        //处理线程发送过来的消息
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                //操作界面
                switch (msg.what) {
                    case 0x01:
                        swipeRefreshLayout.setRefreshing(false);
                        newsRecyclerAdapter.addItem(list);
                        break;
                    case 0x02:
                        swipeRefreshLayout.setRefreshing(false);
                        newsRecyclerAdapter.addItem(newList);
                        break;
                    case 0x03:
                        swipeRefreshLayout.setRefreshing(false);
                        newsRecyclerAdapter.addTopItem(newList);
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    /**
     * 获取网页内容
     */
    private void getHtml() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // /解析网页，返回解析内容
                    list = ParseHtml.getHtml(new URL("http://edu.163.com/special/kids_news/"));
                    Message message = new Message();
                    message.what = 0x01;
                    handler.sendMessage(message);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateHtml(int page) {
        //格式化数字，如1格式化为01
        String format_page = String.format("%02d", page);
        if (!format_page.equals("11")) {
            try {
                final URL url = new URL("http://edu.163.com/special/kids_news_" + format_page);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        newList = ParseHtml.getHtml(url);
                        System.out.println("msg: 2");
                        Message message = new Message();
                        message.what = 0x03;
                        handler.sendMessage(message);
                    }
                }).start();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this, "没有更多内容了…", Toast.LENGTH_SHORT).show();
            this.page--;
        }
    }

    /**
     * 加载更多网页
     *
     * @param page 加载网页的页数
     */
    private void getMoreHtml(int page) {
        //格式化数字，如1格式化为01
        String format_page = String.format("%02d", page);
        if (!format_page.equals("11")) {
            try {
                final URL url = new URL("http://edu.163.com/special/kids_news_" + format_page);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        newList = ParseHtml.getHtml(url);
                        System.out.println("msg: 2");
                        Message message = new Message();
                        message.what = 0x02;
                        handler.sendMessage(message);
                    }
                }).start();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this, "没有更多内容了…", Toast.LENGTH_SHORT).show();
            this.page--;
        }

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
