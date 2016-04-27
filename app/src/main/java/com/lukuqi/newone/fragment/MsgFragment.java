package com.lukuqi.newone.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lukuqi.newone.R;
import com.lukuqi.newone.activity.MessageActivity;
import com.lukuqi.newone.activity.NewActivity;
import com.lukuqi.newone.adapter.MsgRecyclerAdapter;
import com.lukuqi.newone.receiver.NetworkConnReceiver;
import com.lukuqi.newone.service.NetStateService;
import com.lukuqi.newone.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息界面
 * <p/>
 * Created by right on 2015/12/29.
 */
public class MsgFragment extends Fragment {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private MsgRecyclerAdapter mMsgRecyclerAdapter;
    private List<String> mDatas;
    private Context mCotext;
    private TextView mInternet;
    private Boolean flag = false; //无网络flag
    public static final int CONNECTION = 0x01;
    public static final int DISCONNECTION = 0x00;
    public static Handler mHandler;

    public static MsgFragment newInstance() {
        Bundle args = new Bundle();

        MsgFragment fragment = new MsgFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("MsgFragment ----onCreate");
        initData();
        initView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("MsgFragment --- onCreateView");
        View view = inflater.inflate(R.layout.msg_fragment, container, false);
        //检查网络状况
//        checkNet(view);
        mInternet = (TextView) view.findViewById(R.id.internet_tip);
        if (flag) {
            mInternet.setVisibility(View.VISIBLE);
        } else {
            mInternet.setVisibility(View.GONE);
        }

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case DISCONNECTION:
                        mInternet.setVisibility(View.VISIBLE);
                        flag = true;
                        break;
                    case CONNECTION:
                        mInternet.setVisibility(View.GONE);
                        flag = false;
                        break;
                }
            }
        };


        //初始化下拉刷新组件
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        //下拉新增条目
                        mMsgRecyclerAdapter.addItem();
                    }
                }, 1000);
            }
        });
        //初始化RecyclerView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        //初始化适配器
        mMsgRecyclerAdapter = new MsgRecyclerAdapter(mCotext, mDatas);
        //设置点击监听事件
        mMsgRecyclerAdapter.setOnItemClickListener(new MsgRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Snackbar.make(getView(), position + " is onClicked", Snackbar.LENGTH_SHORT).show();
                //跳转
                Intent intent = new Intent(mCotext,
                        MessageActivity.class);
                startActivity(intent);

//                Toast.makeText(getActivity(),position + " is onClicked",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Snackbar.make(getView(), position + " is onLongClicked", Snackbar.LENGTH_SHORT).show();


            }
        });
        //为RecyclerView绑定适配器
        mRecyclerView.setAdapter(mMsgRecyclerAdapter);
        //为RecyclerView设置线性布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //设置Item新增、移除动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("onPause()---");
    }

    private void initData() {
        mDatas = new ArrayList<>();
        for (int i = 'A'; i < 'z'; i++) {
            mDatas.add("" + (char) i);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //停止服务
        getActivity().stopService(new Intent(mCotext, NetStateService.class));
        System.out.println("onDestroy()---");
    }

    private void initView() {
        mCotext = getContext();
        //开启服务
        getActivity().startService(new Intent(mCotext, NetStateService.class));

    }

    //检查网络
    private void checkNet(View view) {
        if (!Utils.InternetConn((ConnectivityManager) mCotext.getSystemService(mCotext.CONNECTIVITY_SERVICE))) {
            TextView tip = (TextView) view.findViewById(R.id.internet_tip);
            tip.setVisibility(View.VISIBLE);
        }
    }


}
