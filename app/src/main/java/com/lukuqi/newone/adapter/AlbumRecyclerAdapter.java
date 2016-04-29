package com.lukuqi.newone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lukuqi.newone.R;
import com.lukuqi.newone.application.MyApplication;
import com.lukuqi.newone.util.Options;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mr.right on 2016/4/28.
 */
public class AlbumRecyclerAdapter extends RecyclerView.Adapter<AlbumRecyclerAdapter.AlbumViewHolder> {
    private Context context;
    private List<String> datas;
    private LayoutInflater layoutInflater;
    private ImageLoader imageLoader;            //图片加载器
    public OnItemClickListener mOnItemClickListener;

    //点击事件接口
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

//        void onItemLongClick(View view, int position);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }


    public AlbumRecyclerAdapter(Context context, List<String> datas) {
        this.context = context;
        this.datas = datas;
        layoutInflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(MyApplication.setConfiguration(context));
    }

    @Override
    public AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.album_recycler_view, parent, false);
        //设置组件点击波浪
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
        view.setBackgroundResource(typedValue.resourceId);

        AlbumViewHolder recyclerViewHolder = new AlbumViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(final AlbumViewHolder holder, final int position) {
//        holder.imageView.setImageBitmap();
        imageLoader.displayImage(datas.get(position), holder.imageView, Options.getListOptions());
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
    }

    @Override
    public int getItemCount() {
        if (datas != null) {
            return datas.size();
        }
        return 0;
    }

    public void addPicture(List<String> datas) {
        this.datas.addAll(datas);
        notifyItemInserted(0);
    }

    static class AlbumViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public AlbumViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_album);
        }
    }
}
