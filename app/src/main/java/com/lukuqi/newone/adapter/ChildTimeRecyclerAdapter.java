package com.lukuqi.newone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lukuqi.newone.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mr.right on 2016/4/25.
 */
public class ChildTimeRecyclerAdapter extends RecyclerView.Adapter<ChildTimeRecyclerAdapter.ChildTimeViewHolder> {

    private Context context;                        //上下文
    private LayoutInflater layoutInflater;         //布局管理器
    private List<HashMap<String, String>> datas;    //网页数据
    private ImageLoader imageLoader;                //图片加载器

    public ChildTimeRecyclerAdapter(Context context, List<HashMap<String, String>> datas) {
        this.context = context;
        this.datas = datas;
        layoutInflater = LayoutInflater.from(context);
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
    public void onBindViewHolder(ChildTimeViewHolder holder, int position) {
        holder.username.setText(datas.get(position).get("name"));
        holder.content.setText(datas.get(position).get("content"));
        holder.time.setText(datas.get(position).get("time"));

    }

    @Override
    public int getItemCount() {
        if (datas != null) {
            return datas.size();
        }
        return 0;
    }

    class ChildTimeViewHolder extends RecyclerView.ViewHolder {
        TextView username, content, time;

        public ChildTimeViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.tv_time_username);
            content = (TextView) itemView.findViewById(R.id.tv_time_content);
            time = (TextView) itemView.findViewById(R.id.tv_time_time);
        }
    }
}
