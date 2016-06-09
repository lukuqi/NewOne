package com.lukuqi.newone.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initDb();

        initView(); //初始化界面
        getLocalHtml();//获取本地缓存网页
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
                if (list != null) {
                    list.get(position).get("url");
                    Intent intent = new Intent(NewsActivity.this, NewsDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("url", list.get(position).get("url"));
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "当前无网络访问!", Toast.LENGTH_SHORT).show();
                }
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
                        System.out.println("list:" + list);
                        if (list == null) {
                            getLocalHtml();
                            Toast.makeText(getApplicationContext(), "网页获取失败！请检查网络。", Toast.LENGTH_SHORT).show();
                        } else {
                            saveLoclaHtml(list);
                            newsRecyclerAdapter.addItem(list);
                        }
                        break;
                    case 0x02:
                        swipeRefreshLayout.setRefreshing(false);
                        if (list == null) {
                            Toast.makeText(getApplicationContext(), "网页获取失败！请检查网络。", Toast.LENGTH_SHORT).show();
                        } else {
                            newsRecyclerAdapter.addItem(newList);
                        }
                        break;
                    case 0x03:
                        swipeRefreshLayout.setRefreshing(false);
                        if (list == null) {
                            Toast.makeText(getApplicationContext(), "网页获取失败！请检查网络。", Toast.LENGTH_SHORT).show();
                        } else {
                            newsRecyclerAdapter.addTopItem(newList);
                        }

                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    /**
     * 初始化数据库
     */
    private void initDb() {
        db = openOrCreateDatabase("newone.db", Context.MODE_PRIVATE, null);
        String sql = "create table if not exists news(id integer primary key autoincrement,title varchar, url varchar, content varchar, image varchar, source varchar)";
        db.execSQL(sql);
        db.close();
    }

    /**
     * 保存新闻列表
     *
     * @param lists 新闻列表数据
     */
    private void saveLoclaHtml(List<HashMap<String, String>> lists) {
        db = openOrCreateDatabase("newone.db", Context.MODE_PRIVATE, null);
        //ContentValues以键值对的形式存放数据
        ContentValues cv = new ContentValues();
        for (HashMap<String, String> list : lists) {
            cv.put("title", list.get("title"));
            cv.put("url", list.get("url"));
            cv.put("content", list.get("content"));
            cv.put("image", list.get("image"));
            cv.put("source", list.get("source"));
            db.insert("news", null, cv);
        }

        //插入ContentValues中的数据
//        db.update("parent", cv, "tel=?", new String[]{tel});
//                        db.insert("parent", null, cv);
        db.close();
    }

    /**
     * 获取本地网页缓存
     */
    private void getLocalHtml() {
        db = openOrCreateDatabase("newone.db", Context.MODE_PRIVATE, null);
        Cursor cur = db.rawQuery("SELECT * FROM news ", null);
        List<HashMap<String, String>> datas = new ArrayList<>();
        if (cur != null) {
            //游标至于第一个位置
            if (cur.moveToFirst()) {
                do {
                    HashMap<String, String> hashMap = new HashMap<>();
                    int titleColumn = cur.getColumnIndex("title");
                    int urlColumn = cur.getColumnIndex("url");
                    int contentColumn = cur.getColumnIndex("content");
                    int imageColumn = cur.getColumnIndex("image");
                    int sourceColumn = cur.getColumnIndex("source");
//                    tv_name.setText(cur.getString(nameColumn));
//                    tv_sex.setText((cur.getInt(sexColumn) == 1) ? "男" : "女");
//                    tv_area.setText(cur.getString(areaColumn));
//                    tv_signature.setText(cur.getString(signatureColumn));
                    hashMap.put("title", cur.getString(titleColumn));
                    hashMap.put("url", cur.getString(urlColumn));
                    hashMap.put("content", cur.getString(contentColumn));
                    hashMap.put("image", cur.getString(imageColumn));
                    hashMap.put("source", cur.getString(sourceColumn));
                    datas.add(hashMap);
//                    System.out.println("numColumn: " + cur.getString(nameColumn) + "" + ((cur.getString(sexColumn).equals("1")) ? "男" : "女") + cur.getString(areaColumn) + cur.getString(signatureColumn));
//                    System.out.println("NewsColumn: " + cur.getString(titleColumn) + "" + (cur.getString(urlColumn)) + cur.getString(contentColumn) + cur.getString(imageColumn)+ cur.getString(sourceColumn));
                } while (cur.moveToNext());
            }
        }
        db.close();
        newsRecyclerAdapter.addItem(datas);

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
                    Message message = new Message();    //message对象
                    message.what = 0x01;                //设置message标志
                    handler.sendMessage(message);       //发送消息
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
