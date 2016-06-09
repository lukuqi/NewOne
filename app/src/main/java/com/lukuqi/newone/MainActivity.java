package com.lukuqi.newone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.lukuqi.newone.activity.AboutActivity;
import com.lukuqi.newone.activity.SearchActivity;
import com.lukuqi.newone.adapter.ViewPagerAdapter;
import com.lukuqi.newone.fragment.DiscyFragment;
import com.lukuqi.newone.fragment.ExpertFragment;
import com.lukuqi.newone.fragment.MsgFragment;
import com.lukuqi.newone.fragment.ProfileFragment;
import com.lukuqi.newone.util.ConstantVar;

import java.util.ArrayList;
import java.util.List;

/**
 * 主界面
 *
 * @author mr.right
 * @time 2015/11/03 15:59
 * @email it-all-right@hotmail.com
 */

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;                    //Toolbar
    private List<String> mDatas;                //数据集合
    private ViewPager mViewPager;               //ViewPage显示主界面内容
    private ViewPagerAdapter mViewPagerAdapter; //适配器
    private TabLayout mTabLayout;               //TabLayout
    private String[] tabName = {"消息", "我的专家", "发现", "我"}; //显示Tab栏内容

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置actionbar无阴影
        //getSupportActionBar().setElevation(0);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //初始化数据
        initData();
        //初始化界面
        initView();
        //注册BroadcastReceiver
        registerBroadcastReceiver();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mDatas = new ArrayList<>();
        mDatas.add(0, tabName[0]);
        mDatas.add(1, tabName[1]);
        mDatas.add(2, tabName[2]);
        mDatas.add(3, tabName[3]);
//        for (int i = 'A'; i < 'z'; i++) {
//            mData.add("" + (char) i);
//        }
    }

    /**
     * 初始化界面
     */
    private void initView() {
        //初始化ViewPager
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        //初始化ViewPagerAdapter
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        //添加Fragment
        mViewPagerAdapter.addFragment(new MsgFragment(), mDatas.get(0));
        mViewPagerAdapter.addFragment(new ExpertFragment(), mDatas.get(1));
        mViewPagerAdapter.addFragment(new DiscyFragment(), mDatas.get(2));
        mViewPagerAdapter.addFragment(new ProfileFragment(), mDatas.get(3));
        //viewPager绑定适配器
        mViewPager.setAdapter(mViewPagerAdapter);


        //初始化TabLayout
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        //TabLayout添加Tab
        mTabLayout.addTab(mTabLayout.newTab().setText(mDatas.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mDatas.get(1)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mDatas.get(2)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mDatas.get(3)));
        //为TabLayout设置ViewPager
        mTabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This is a Snackbar!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //初始化broadcastReceiver对象
        broadcastReceiver = new ExitReciver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent setting_intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(setting_intent);
                break;
            case R.id.action_search:
                Intent search_intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(search_intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 退出应用BroadcastReceiver
     */
    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(ConstantVar.USER_EXIT);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    /**
     * 内部类 接受退出消息
     */
    class ExitReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.print("ExitReciver:----");
            if (intent.getAction().equals(ConstantVar.USER_EXIT)) {
                System.out.print("ExitReciver:----");
                MainActivity.this.finish();
            }

        }
    }
}
