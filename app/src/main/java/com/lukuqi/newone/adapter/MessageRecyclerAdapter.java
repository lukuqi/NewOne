package com.lukuqi.newone.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.lukuqi.newone.R;

import org.jivesoftware.smack.roster.RosterEntry;

import java.util.Collection;
import java.util.List;

/**
 * Created by mr.right on 2016/5/2.
 */
public class MessageRecyclerAdapter extends RecyclerView.Adapter<MessageRecyclerAdapter.MessageViewHolder> {

    //    private List<String> datas;
    private List<RosterEntry> datas;
    private Context context;
    private LayoutInflater layoutInflater;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;

    }

    public MessageRecyclerAdapter(Context context, List<RosterEntry> datas) {
        this.context = context;
        this.datas = datas;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.message_recycler_view, parent, false);
        MessageViewHolder viewHolder = new MessageViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, final int position) {
        holder.name.setText(datas.get(position).getName());

        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder.itemView, position);
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

    public void addItem(Collection<RosterEntry> datas) {

        this.datas.addAll(datas);
//        for (RosterEntry entry : datas) {
//            this.datas.add(entry);
//            System.out.println(entry);
//        }
        notifyItemInserted(0);
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView name;

        public MessageViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }
}
