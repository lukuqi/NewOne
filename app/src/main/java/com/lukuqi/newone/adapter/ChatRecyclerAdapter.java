package com.lukuqi.newone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.lukuqi.newone.R;
import com.lukuqi.newone.activity.ChatActivity;

import java.util.HashMap;
import java.util.List;

/**
 * Created by mr.right on 2016/5/2.
 */
public class ChatRecyclerAdapter extends RecyclerView.Adapter<ChatRecyclerAdapter.ChatViewHolder> {

    //    private List<String> datas;
    private List<HashMap<String, String>> datas;
    private Context context;
    private LayoutInflater layoutInflater;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;

    }

    public ChatRecyclerAdapter(Context context, List<HashMap<String, String>> datas) {
        this.context = context;
        this.datas = datas;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.chat_recycler_view, parent, false);
        ChatViewHolder viewHolder = new ChatViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ChatViewHolder holder, final int position) {
        if (datas.get(position).get("who").equals(ChatActivity.ME)) {
            holder.local_message.setVisibility(View.VISIBLE);
            holder.remote_message.setVisibility(View.INVISIBLE);
            holder.local_message.setText(datas.get(position).get("message"));
        } else {
            holder.local_message.setVisibility(View.INVISIBLE);
            holder.remote_message.setVisibility(View.VISIBLE);
            holder.remote_message.setText(datas.get(position).get("message"));
        }
//
//        if (onItemClickListener != null) {
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    onItemClickListener.onItemClick(holder.itemView, position);
//                }
//            });
//        }

    }

    @Override
    public int getItemCount() {
        if (datas != null) {
            return datas.size();
        }
        return 0;
    }

    public void addMessage(List<HashMap<String, String>> datas) {
        int size = this.datas.size();
        this.datas.addAll(datas);
//        for (HashMap<String, String> hashMap : this.datas) {
//            System.out.println("hashMap: " + hashMap);
//        }
        notifyItemInserted(size);
    }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView local_message;
        TextView remote_message;

        public ChatViewHolder(View itemView) {
            super(itemView);
            local_message = (TextView) itemView.findViewById(R.id.tv_message_local);
            remote_message = (TextView) itemView.findViewById(R.id.tv_message_remote);
        }
    }
}
