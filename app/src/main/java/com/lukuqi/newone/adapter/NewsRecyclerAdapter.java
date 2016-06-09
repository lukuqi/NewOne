package com.lukuqi.newone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lukuqi.newone.R;
import com.lukuqi.newone.application.MyApplication;
import com.lukuqi.newone.util.Options;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;

/**
 * 新闻RecyclerView适配器
 * <p/>
 * Created by mr.right on 2016/4/21.
 */
public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.NewsViewHolder> {

    private Context context;                        //上下文
    private LayoutInflater mLayoutInflater;         //布局管理器
    private List<HashMap<String, String>> datas;    //网页数据
    private ImageLoader imageLoader;                //图片加载器

    public NewsRecyclerAdapter(Context context, List<HashMap<String, String>> datas) {
        this.context = context;
        this.datas = datas;
        mLayoutInflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(MyApplication.setConfiguration(context));
    }


    //点击事件接口
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

//        void onItemLongClick(View view, int position);
    }

    public OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    @Override
    public void onBindViewHolder(final NewsViewHolder holder, final int position) {
        holder.title.setText(datas.get(position).get("title"));
//        System.out.println("title: " + datas.get(position).get("title"));
        holder.content.setText(datas.get(position).get("content"));
        holder.source.setText(datas.get(position).get("source"));
        if (mOnItemClickListener != null) {
//            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//
//                @Override
//                public boolean onLongClick(View v) {
//                    mOnItemClickListener.onItemLongClick(holder.itemView, position);
//                    return false;
//                }
//            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
        if (datas.get(position).get("image").isEmpty()) {
            holder.image.setVisibility(View.GONE);
        } else {
            imageLoader.displayImage(datas.get(position).get("image"), holder.image, Options.getListOptions());
        }
    }

    @Override
    public int getItemCount() {
        if (datas == null) {
            return 0;
        }
        return datas.size();
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.news_recycler_view, parent, false);

        //设置组件点击波浪
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
        view.setBackgroundResource(typedValue.resourceId);

        NewsViewHolder recyclerViewHolder = new NewsViewHolder(view);
        return recyclerViewHolder;
    }


    /**
     * 加载下一页
     *
     * @param datas 网页数据内容
     */
    public void addItem(List<HashMap<String, String>> datas) {
        int size = datas.size();
        this.datas.addAll(datas);
        notifyItemInserted(size);
    }

    public void addTopItem(List<HashMap<String, String>> datas) {
        this.datas.addAll(0, datas);
        notifyItemInserted(0);
        notifyDataSetChanged();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, source;
        ImageView image;
        RelativeLayout item;

        public NewsViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            content = (TextView) itemView.findViewById(R.id.tv_content);
            source = (TextView) itemView.findViewById(R.id.tv_source);
            image = (ImageView) itemView.findViewById(R.id.img_cover);
            item = (RelativeLayout) itemView.findViewById(R.id.rl_item);

        }
    }
}

