package com.lukuqi.newone.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lukuqi.newone.R;
import com.lukuqi.newone.http.OkHttpUtils;
import com.lukuqi.newone.util.ConstantVar;
import com.lukuqi.newone.util.IP;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class PublishTimeActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE = 2;
    private EditText publish_content;
    private ImageView add;
    private Button publish;
    private ArrayList<String> mSelectPath;
    private LinearLayout parent;
    private Context context;
    private List<File> files = new ArrayList<>(); //上传文件list

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_publish_time);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initView();

    }

    private void initView() {
        parent = (LinearLayout) findViewById(R.id.ll_publish_image);
        publish_content = (EditText) findViewById(R.id.et_publish_content);
        publish = (Button) findViewById(R.id.btn_publish);
        add = (ImageView) findViewById(R.id.image_add);
        //添加照片
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PublishTimeActivity.this, MultiImageSelectorActivity.class);
                // 是否显示拍摄图片
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                // 最大可选择图片数量
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 4);
                // 选择模式
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
                // 默认选择
                if (mSelectPath != null && mSelectPath.size() > 0) {
                    intent.putExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
                }
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });

        //发布动态
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpUtils okHttpUtils = OkHttpUtils.getInstance(context); //初始化网络访问工具
                String url = IP.IP_PARENT + "/uploadImage"; //接口地址
                //访问参数map tel：手机号码 content：动态内容内容 time：发布时间
                HashMap<String, String> paramsMap = new HashMap<>();
                SharedPreferences sharedPreferences = getSharedPreferences(ConstantVar.USER_TEL, MODE_PRIVATE);//new SharedPreferences对象
                String tel = sharedPreferences.getString("tel", "null"); //获取tel电话号码的值
                paramsMap.put("tel", tel);
                paramsMap.put("content", publish_content.getText().toString());
                paramsMap.put("time", Long.toString(System.currentTimeMillis()).substring(0, 10));
                final ProgressDialog progressDialog = ProgressDialog.show(PublishTimeActivity.this, null, "请稍等...", true, false);//进度条
                TimerTask task = new TimerTask() {
                    public void run() {
                        progressDialog.dismiss();
                        finish();
                    }
                };
                Intent intent = new Intent();
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("data",paramsMap);
//                intent.putExtras(bundle);
                intent.putExtra("data", paramsMap);
                intent.putExtra("local", "local");
                setResult(1, intent);//返回结果数据
                Timer timer = new Timer();
                timer.schedule(task, 500);
                //进行网络操作
                okHttpUtils.postMutilFileAsyn(url, paramsMap, files, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.out.println("res: " + e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        System.out.println("res: " + response.body().string());
//                        String res = response.body().string();
//                        Gson gson = new Gson();
//                        final Base base = gson.fromJson(res, Base.class);
//                        if (base.getCode().equals("10000")) {
////                            progressDialog.dismiss();
////                            finish();
//                        }
                    }
                });

            }
        });
    }

    private View addView(String path) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(20, 20, 10, 20);
        LinearLayout view = new LinearLayout(this);
        view.setLayoutParams(lp);
        view.setOrientation(LinearLayout.HORIZONTAL);
        ViewGroup.LayoutParams vlp = new ViewGroup.LayoutParams(
                (int) (80 * context.getResources().getDisplayMetrics().density + 0.5f),
                (int) (80 * context.getResources().getDisplayMetrics().density + 0.5f));

        ImageView image = new ImageView(this);
        image.setLayoutParams(vlp);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Bitmap bm = BitmapFactory.decodeFile(path);
        image.setImageBitmap(bm);
        view.addView(image);
        return view;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                mSelectPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                StringBuilder sb = new StringBuilder();

                for (String p : mSelectPath) {
                    sb.append(p);
                    sb.append("\n");
                    parent.addView(addView(p));
                    files.add(new File(p));
//                    Bitmap bm = BitmapFactory.decodeFile(p);
//
//                    add.setImageBitmap(bm);
                }
//                publish_content.setText(sb.toString());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_time, menu);
//        MenuItem item = menu.add(Menu.NONE, Menu.FIRST, 100, "发表");
//        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
}
