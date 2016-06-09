package com.lukuqi.newone.adapter;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lukuqi.newone.R;
import com.lukuqi.newone.application.MyApplication;
import com.lukuqi.newone.util.IP;
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
    public List<HashMap<String, String>> datas;    //网页数据
    private ImageLoader imageLoader;                //图片加载器
    private String url = IP.HOST + "/Upload/";

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
//        System.out.println("position:" + position + "content:" + datas.get(position).get("content") + "--picture" + datas.get(position).get("picture"));
//        url+datas.get(position).get("icon")
        imageLoader.displayImage(url + datas.get(position).get("icon"), holder.circleImageView, Options.getListOptions());
            if (!datas.get(position).get("picture").isEmpty()) {
                ImageView[] pictures = {holder.picture_1, holder.picture_2, holder.picture_3, holder.picture_4};
                holder.linearLayout.setVisibility(View.VISIBLE);
                String[] strings = datas.get(position).get("picture").split(",");
                pictures[0].setVisibility(View.GONE);
                pictures[1].setVisibility(View.GONE);
                pictures[2].setVisibility(View.GONE);
                pictures[3].setVisibility(View.GONE);
                for (int i = 0; i < strings.length; i++) {
                    pictures[i].setVisibility(View.VISIBLE);
                    imageLoader.displayImage(url + strings[i], pictures[i], Options.getListOptions());
                }
            } else {
                holder.linearLayout.setVisibility(View.GONE);
            }



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
//    public void addItem(List<HashMap<String, String>> datas) {
//        //int size = datas.size();
//        System.out.println("new Datas: " + datas.size());
//        System.out.println("new Datas 1: " + this.datas.size());
////        this.datas.addAll(datas);
//        System.out.println("new Datas 1: " + this.datas.size());
////        notifyItemInserted(0);
//        System.out.println("new Datas: " + datas.size());
////        notifyItemRangeInserted(0,datas.size());
//        notifyDataSetChanged();
//    }
    public void addItem(HashMap<String, String> datas) {

        this.datas.add(0, datas);
        notifyItemInserted(0);
        notifyDataSetChanged();

    }
    public void addItemNotify(List<HashMap<String, String>> datas) {
        this.datas.addAll(0, datas);
//        notifyItemInserted(0);
        notifyDataSetChanged();
    }


    static class ChildTimeViewHolder extends RecyclerView.ViewHolder {
        TextView username, content, time;
        CircleImageView circleImageView;
        ImageView picture_1, picture_2, picture_3, picture_4;
        LinearLayout linearLayout;

        public ChildTimeViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.tv_time_username);
            content = (TextView) itemView.findViewById(R.id.tv_time_content);
            time = (TextView) itemView.findViewById(R.id.tv_time_time);
            circleImageView = (CircleImageView) itemView.findViewById(R.id.cIamge_time_user);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.ll_picture);
            picture_1 = (ImageView) itemView.findViewById(R.id.img_pic_1);
            picture_2 = (ImageView) itemView.findViewById(R.id.img_pic_2);
            picture_3 = (ImageView) itemView.findViewById(R.id.img_pic_3);
            picture_4 = (ImageView) itemView.findViewById(R.id.img_pic_4);
        }
    }
}
