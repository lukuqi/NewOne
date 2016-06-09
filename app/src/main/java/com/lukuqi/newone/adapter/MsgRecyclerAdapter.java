package com.lukuqi.newone.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lukuqi.newone.R;
import com.lukuqi.newone.application.MyApplication;
import com.lukuqi.newone.bean.UserInfo;
import com.lukuqi.newone.util.IP;
import com.lukuqi.newone.util.Options;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by right on 2015/12/29.
 */
public class MsgRecyclerAdapter extends RecyclerView.Adapter<MsgRecyclerAdapter.RecyclerViewHolder> {
    private Context mContext;
    private List<UserInfo> mDatas;
    //    List<RosterEntry> mDatas;
    private LayoutInflater mLayoutInflater;
    ImageLoader imageLoader;
    private String url = IP.HOST + "/Upload/";
    List<HashMap<String, String>> time = new ArrayList<>();


    //点击事件接口
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }


    public MsgRecyclerAdapter(Context mContext, List<UserInfo> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
        mLayoutInflater = LayoutInflater.from(mContext);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(MyApplication.setConfiguration(mContext));
    }
//    public MsgRecyclerAdapter(Context mContext, List<String> mDatas) {
//        this.mContext = mContext;
//        this.mDatas = mDatas;
//        mLayoutInflater = LayoutInflater.from(mContext);
//    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.msg_recycler_view, parent, false);

        //设置组件点击波浪
        TypedValue typedValue = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
        view.setBackgroundResource(typedValue.resourceId);

        RecyclerViewHolder mRecyclerViewHolder = new RecyclerViewHolder(view);
        return mRecyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        holder.name.setText(mDatas.get(position).getName());
//        System.out.println("path:" + mDatas.get(position).getIcon());
        imageLoader.displayImage(url + mDatas.get(position).getIcon(), holder.circleImage, Options.getListOptions());
//        if (time.size() > 0) {
        holder.lastMessage.setText(time.get(0).get("message"));
        holder.time.setText(time.get(0).get("createtime"));
//        }
//        System.out.println("name: " + mDatas.get(position));
//        System.out.println("name: " + mDatas.get(position));
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
        return mDatas.size();
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        CircleImageView circleImage;
        TextView lastMessage;
        TextView time;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.txt_name);
            circleImage = (CircleImageView) itemView.findViewById(R.id.circleImage);
            lastMessage = (TextView) itemView.findViewById(R.id.txt_last_message);
            time = (TextView) itemView.findViewById(R.id.txt_time);
        }
    }

    //新增条目
    public void addItem() {
//        mDatas.add(0, "新条目");
        notifyItemInserted(0);
    }

    public void add(List<UserInfo> datas) {
        mDatas.addAll(datas);
        notifyItemInserted(0);
    }

    public void addTime(List<HashMap<String, String>> datas) {
        time.addAll(datas);
        notifyDataSetChanged();
    }
}
