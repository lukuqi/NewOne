package com.lukuqi.newone.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lukuqi.newone.R;
import com.lukuqi.newone.application.MyApplication;
import com.lukuqi.newone.util.Options;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by mr.right on 2016/4/29.
 */
public class PhotoViewerPagerAdapter extends PagerAdapter {

    List<View> views;
    List<String> datas;
    private ImageLoader imageLoader;

    public PhotoViewerPagerAdapter(List<View> views, List<String> datas, Context context) {
        this.views = views;
        this.datas = datas;
        imageLoader = ImageLoader.getInstance();
//        imageLoader.init(MyApplication.setConfiguration(context));
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = views.get(position);
        ImageView image = (ImageView) view.findViewById(R.id.viewPager_image);
        imageLoader.displayImage(datas.get(position), image, Options.getListOptions());
        container.removeView(views.get(position));
        container.addView(views.get(position));
        return views.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
        container.removeView(views.get(position));
    }
}
