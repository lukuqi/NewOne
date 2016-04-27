package com.lukuqi.newone.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lukuqi.newone.R;
import com.lukuqi.newone.activity.ChildTimeActivity;
import com.lukuqi.newone.activity.NewsActivity;
import com.lukuqi.newone.adapter.MsgRecyclerAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * 发现界面
 * Created by right on 2015/12/29.
 */
public class DiscyFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private MsgRecyclerAdapter mMsgRecyclerAdapter;
    private List<String> mDatas;
    private Context mCotext;
    private TextView news;
    private TextView child_time;

    public static DiscyFragment newInstance() {
        Bundle args = new Bundle();

        DiscyFragment fragment = new DiscyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("DiscyFragment :onCreate");
        initData();
        initView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("DiscyFragment :onCreateView");
        View view = inflater.inflate(R.layout.discy_fragment, container, false);
        news = (TextView) view.findViewById(R.id.tv_news);
        child_time = (TextView) view.findViewById(R.id.tv_child_time);
        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转activity
                startActivity(new Intent(getActivity(), NewsActivity.class));
            }
        });
        child_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转activity
                startActivity(new Intent(getActivity(), ChildTimeActivity.class));
            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    private void initData() {
        mDatas = new ArrayList<>();
        for (int i = 'A'; i < 'z'; i++) {
            mDatas.add("" + (char) i);
        }
    }

    private void initView() {
        mCotext = getContext();
    }
}
