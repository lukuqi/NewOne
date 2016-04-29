package com.lukuqi.newone.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.lukuqi.newone.R;
import com.lukuqi.newone.adapter.PhotoViewerPagerAdapter;
import com.lukuqi.newone.application.MyApplication;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片浏览器
 */
public class PhotoViewerActivity extends AppCompatActivity {

    private ViewPager photoViewer;
    private ImageLoader imageLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //全屏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        initView();
    }

    private void initView() {

        List<String> list_datas = getIntent().getStringArrayListExtra("datas");
//        List<String> list_data = bundle.getStringArrayList("datas");
        List<View> list_views = new ArrayList<>();
        LayoutInflater inflater = LayoutInflater.from(this);
        photoViewer = (ViewPager) findViewById(R.id.viewPager_photoviewer);
        for (int i = 0; i < list_datas.size(); i++) {
            View item = inflater.inflate(R.layout.viewpager_item, null);
//            ((TextView) item.findViewById(R.id.viewPager_text1)).setText(".");
            list_views.add(item);
        }
        photoViewer.setAdapter(new PhotoViewerPagerAdapter(list_views,list_datas,this));
        photoViewer.setCurrentItem(getIntent().getIntExtra("position",0));
    }

}
