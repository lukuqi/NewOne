package com.lukuqi.newone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lukuqi.newone.R;
import com.lukuqi.newone.application.MyApplication;
import com.lukuqi.newone.util.Options;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by mr.right on 2016/4/25.
 */
public class ChildTimeRecyclerAdapter extends RecyclerView.Adapter<ChildTimeRecyclerAdapter.ChildTimeViewHolder> {

    private Context context;                        //上下文
    private LayoutInflater layoutInflater;         //布局管理器
    private List<HashMap<String, String>> datas;    //网页数据
    private ImageLoader imageLoader;                //图片加载器
    private String url = "http://114.215.144.131/NewOne/Upload/";

    public ChildTimeRecyclerAdapter(Context context, List<HashMap<String, String>> datas) {
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(MyApplication.setConfiguration(context));
        this.datas = datas;

    }

    @Override
    public ChildTimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.child_time_recycler_view, parent, false);
        //设置组件点击波浪
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
        view.setBackgroundResource(typedValue.resourceId);

        ChildTimeViewHolder recyclerViewHolder = new ChildTimeViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(final ChildTimeViewHolder holder, final int position) {
        holder.username.setText(datas.get(position).get("name"));
        holder.content.setText(datas.get(position).get("content"));
        holder.time.setText(datas.get(position).get("time"));

//        url+datas.get(position).get("icon")
        imageLoader.displayImage( url+datas.get(position).get("icon"), holder.circleImageView, Options.getListOptions());

    }

    @Override
    public int getItemCount() {
        if (datas == null) {
            return 0;
        }
        return datas.size();
    }

    /**
     * 添加item
     *
     * @param datas 数据
     */
    public void addItem(List<HashMap<String, String>> datas) {
//        int size = datas.size();
//        this.datas.addAll(0, datas);
        notifyItemInserted(0);
    }

    static class ChildTimeViewHolder extends RecyclerView.ViewHolder {
        TextView username, content, time;
        CircleImageView circleImageView;

        public ChildTimeViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.tv_time_username);
            content = (TextView) itemView.findViewById(R.id.tv_time_content);
            time = (TextView) itemView.findViewById(R.id.tv_time_time);
            circleImageView =(CircleImageView)itemView.findViewById(R.id.cIamge_time_user);
        }
    }
}
