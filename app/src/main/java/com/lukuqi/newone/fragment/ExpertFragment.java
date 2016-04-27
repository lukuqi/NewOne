package com.lukuqi.newone.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lukuqi.newone.R;
import com.lukuqi.newone.adapter.MsgRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 专家信息界面
 * Created by right on 2015/12/29.
 */
public class ExpertFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private MsgRecyclerAdapter mMsgRecyclerAdapter;
    private List<String> mDatas;
    private Context mCotext;

    public static ExpertFragment newInstance() {
        Bundle args = new Bundle();

        ExpertFragment fragment = new ExpertFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("ExpertFragment :onCreate");
        initData();
        initView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("ExpertFragment :onCreateView");
        View view = inflater.inflate(R.layout.expert_fragment, container, false);
//        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
//        mMsgRecyclerAdapter = new MsgRecyclerAdapter(mCotext, mDatas);
//        mRecyclerView.setAdapter(mMsgRecyclerAdapter);
//
//        //为RecyclerView设置线性布局
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//        mRecyclerView.setLayoutManager(linearLayoutManager);
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
