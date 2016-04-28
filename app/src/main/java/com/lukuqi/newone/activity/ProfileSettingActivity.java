package com.lukuqi.newone.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lukuqi.newone.bean.Base;
import com.lukuqi.newone.bean.UserBase;
import com.lukuqi.newone.bean.UserInfo;
import com.lukuqi.newone.R;
import com.lukuqi.newone.http.OkHttpUtils;
import com.lukuqi.newone.util.CheckDir;
import com.lukuqi.newone.util.ConstantVar;
import com.lukuqi.newone.util.IP;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ProfileSettingActivity extends AppCompatActivity {
    private RelativeLayout rl_icon;
    private RelativeLayout rl_name;
    private RelativeLayout rl_sex;
    private RelativeLayout rl_area;
    private RelativeLayout rl_signature;
    private ImageView img_icon;
    private TextView tv_name;
    private TextView tv_sex;
    private TextView tv_area;
    private TextView tv_signature;
    private Context mContext;

    private String tel;
    private OkHttpUtils okHttpUtils;
    private boolean change_flag = false;
    private SQLiteDatabase db;

    //activity返回常量值
    public final static int CAMERA = 0;
    public final static int PICTURE = 1;
    public final static int NAME = 2;
    public final static int AREA = 3;
    public final static int SIGNATURE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initView();
    }

    private void initView() {
        mContext = this;
        //创建sqlite数据库
        db = openOrCreateDatabase("newone.db", Context.MODE_PRIVATE, null);
//        db.execSQL("DROP TABLE IF EXISTS parent");
        //创建数据库表 parent
        String sql = "create table if not exists parent(id integer primary key autoincrement,tel varchar, name varchar, sex integer, area varchar, signature varchar)";
        //执行sql语句
        db.execSQL(sql);
        db.close();
        tel = getIntent().getExtras().getString("tel");

        rl_icon = (RelativeLayout) findViewById(R.id.rl_icon);
        rl_name = (RelativeLayout) findViewById(R.id.rl_name);
        rl_sex = (RelativeLayout) findViewById(R.id.rl_sex);
        rl_area = (RelativeLayout) findViewById(R.id.rl_area);
        rl_signature = (RelativeLayout) findViewById(R.id.rl_signature);
        img_icon = (ImageView) findViewById(R.id.img_icon);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_area = (TextView) findViewById(R.id.tv_area);
        tv_signature = (TextView) findViewById(R.id.tv_signature);
        //查看头像
        img_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ProfileSettingActivity.this, AlbumActivity.class));
            }
        });
        //设置头像
        rl_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] items = {"相机拍照", "本地相册"};
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext).setTitle("图片").setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:

                                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(camera, CAMERA);

                                break;
                            case 1:
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent, PICTURE);
                                break;
                            default:
                                break;
                        }

                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();


            }
        });
        //昵称
        rl_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileSettingActivity.this, NameEditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("tel", tel);
                bundle.putString("name", tv_name.getText().toString());
                intent.putExtras(bundle);
                startActivityForResult(intent, NAME);
            }
        });
        //性别
        rl_sex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileSettingActivity.this);
                builder.setTitle("性别");
                final String[] sex = {"男", "女"};

                builder.setSingleChoiceItems(sex, tv_sex.getText().toString().equals("男") ? 0 : 1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        tv_sex.setText(sex[which]);
                        db = openOrCreateDatabase("newone.db", Context.MODE_PRIVATE, null);
                        //获取游标
                        Cursor cur = db.rawQuery("SELECT tel FROM parent where tel =" + tel, null);
                        //ContentValues以键值对的形式存放数据
                        ContentValues cv = new ContentValues();
                        if (cur != null) {
//                            System.out.println("cur:" + cur.moveToFirst());
//                            //游标至于第一个位置,如果游标为空，返回false
                            if (cur.moveToFirst()) {
                                cv.put("sex", tv_sex.getText().toString().equals("男") ? 1 : 0);
                                db.update("parent", cv, "tel=?", new String[]{tel});
                            } else {
                                cv.put("tel", tel);
                                cv.put("sex", tv_sex.getText().toString().equals("男") ? 1 : 0);
                                //插入ContentValues中的数据
                                db.insert("parent", null, cv);
                            }
                        }
                        db.close();
                        change_flag = true;
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        //区域
        rl_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileSettingActivity.this, AreaEditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("tel", tel);
                bundle.putString("signature", tv_signature.getText().toString());
                intent.putExtras(bundle);
                startActivityForResult(intent, AREA);

            }
        });
        //个性签名
        rl_signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileSettingActivity.this, SignatureEditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("tel", tel);
                bundle.putString("signature", tv_signature.getText().toString());
                intent.putExtras(bundle);
                startActivityForResult(intent, SIGNATURE);
            }
        });
        //本地数据库获取资料
        getLocalInfo();
        //更新个人资料
//        updateInfo();

    }

    /**
     * 从本地SQLite数据库获取个人资料
     */
    private void getLocalInfo() {
        db = openOrCreateDatabase("newone.db", Context.MODE_PRIVATE, null);
        //获取游标
        Cursor cur = db.rawQuery("SELECT * FROM parent where tel =" + tel, null);
        if (cur != null) {
            //游标至于第一个位置
            if (cur.moveToFirst()) {
                do {
                    int nameColumn = cur.getColumnIndex("name");
                    int sexColumn = cur.getColumnIndex("sex");
                    int areaColumn = cur.getColumnIndex("area");
                    int signatureColumn = cur.getColumnIndex("signature");
                    tv_name.setText(cur.getString(nameColumn));
                    tv_sex.setText((cur.getInt(sexColumn) == 1) ? "男" : "女");
                    tv_area.setText(cur.getString(areaColumn));
                    tv_signature.setText(cur.getString(signatureColumn));
//                    System.out.println("numColumn: " + cur.getString(nameColumn) + "" + ((cur.getString(sexColumn).equals("1")) ? "男" : "女") + cur.getString(areaColumn) + cur.getString(signatureColumn));
                } while (cur.moveToNext());
            }
        }
        db.close();
    }

    private void updateInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences(ConstantVar.USER_INFO_CHANGED, MODE_PRIVATE);
        String flag = sharedPreferences.getString("change", "null");

        change_flag = true;

//        System.out.println("tel:" + tel + " flag:" + flag);
        if (flag.equals("null")) {
            String url = IP.IP_PARENT + "/getInfo";
            HashMap<String, String> paramsMap = new HashMap<>();
            paramsMap.put("tel", tel);
            okHttpUtils = OkHttpUtils.getInstance(getApplicationContext());
            okHttpUtils.postAsyn(url, paramsMap, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String res = response.body().string();
                    System.out.println("获取个人信息" + res);
                    Gson gson = new Gson();
                    UserBase<UserInfo> user = gson.fromJson(res, new TypeToken<UserBase<UserInfo>>(){}.getType());
                    final List<UserInfo> userInfos = user.getMessage();
                    if (user.getCode().equals("10000")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_name.setText(userInfos.get(0).getName());
                                tv_sex.setText((userInfos.get(0).getSex() == 1) ? "男" : "女");
                                tv_area.setText(userInfos.get(0).getArea());
                                tv_signature.setText(userInfos.get(0).getSignature());
                            }
                        });

                    }
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("ProgileSetting: onStop");
        if (change_flag) {
            String url = IP.IP_PARENT + "/updateInfo";
            HashMap<String, String> paramsMap = new HashMap<>();
            paramsMap.put("tel", tel);
            paramsMap.put("name", tv_name.getText().toString());
            paramsMap.put("sex", tv_sex.getText().toString().equals("男") ? "1" : "0");
            paramsMap.put("area", tv_area.getText().toString());
            paramsMap.put("signature", tv_signature.getText().toString());
            okHttpUtils = OkHttpUtils.getInstance(getApplicationContext());
            okHttpUtils.postAsyn(url, paramsMap, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String res = response.body().string();
                    System.out.println("个人信息更新：" + res);
                    Gson gson = new Gson();
                    Base base = gson.fromJson(res, Base.class);
                    if (base.getCode().equals("10000")) {
                        db = openOrCreateDatabase("newone.db", Context.MODE_PRIVATE, null);
                        //ContentValues以键值对的形式存放数据
                        ContentValues cv = new ContentValues();
                        cv.put("tel", tel);
                        cv.put("name", tv_name.getText().toString());
                        cv.put("sex", tv_sex.getText().toString().equals("男") ? 1 : 0);
                        cv.put("area", tv_area.getText().toString());
                        cv.put("signature", tv_signature.getText().toString());
                        //插入ContentValues中的数据
                        db.update("parent", cv, "tel=?", new String[]{tel});
//                        db.insert("parent", null, cv);
                        db.close();
                    }
                }
            });
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CAMERA:

//                System.out.println("Data:" + data);
//                System.out.println("Data:" + data.getData());
//                System.out.println("Data:" + data.getExtras());
//                System.out.println("Data:" + data.getParcelableExtra("data"));
                if (null != data.getExtras()) {
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    img_icon.setImageBitmap(bitmap);
//                    Uri image_uri = data.getData();
//                    img_icon.setImageURI(image_uri);
                    FileOutputStream fos = null;
                    try {
                        File fileName = CheckDir.checkImageFile("icon.png");
                        System.out.println("fileName: " + fileName);
                         fos = new FileOutputStream(fileName);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);// 把数据写入文件
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {

                        try {
                            fos.flush();
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }




//                    File file = new File(path);


//                    String url = IP.IP_PARENT + "/updateIcon";
//                    HashMap<String, String> paramsMap = new HashMap<>();
//
////                File icon = selectedImage;
//                    paramsMap.put("tel", tel);
//                    OkHttpUtils okHttpUtils = OkHttpUtils.getInstance(getApplicationContext());
//                    okHttpUtils.postFileAsyn(url, paramsMap, file, new Callback() {
//                        @Override
//                        public void onFailure(Call call, IOException e) {
//                            System.out.println("上传照片：" + e);
//                        }
//
//                        @Override
//                        public void onResponse(Call call, Response response) throws IOException {
//                            String res = response.body().string();
//                            System.out.println("上传照片：" + res);
//                        }
//                    });
                    System.out.println("camera_image:" + data.getExtras());

                }
                break;
            case PICTURE:
                System.out.println("图片Data" + data);
                if (null != data) {
                    Uri image_uri = data.getData();
                    String[] proj = {MediaStore.Images.Media.DATA};

                    //好像是android多媒体数据库的封装接口，具体的看Android文档
                    Cursor cursor = getContentResolver().query(image_uri, proj, null, null, null);
                    //按我个人理解 这个是获得用户选择的图片的索引值
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    //将光标移至开头 ，这个很重要，不小心很容易引起越界
                    cursor.moveToFirst();
                    //最后根据索引值获取图片路径
                    String path = cursor.getString(column_index);
                    cursor.close();

                    System.out.println("Uri_image: " + image_uri.getPath());
                    System.out.println("Uri_image: " + image_uri);
                    File file = new File(path);


                    String url = IP.IP_PARENT + "/updateIcon";
                    HashMap<String, String> paramsMap = new HashMap<>();

//                File icon = selectedImage;
                    paramsMap.put("tel", tel);
                    OkHttpUtils okHttpUtils = OkHttpUtils.getInstance(getApplicationContext());
                    okHttpUtils.postFileAsyn(url, paramsMap, file, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            System.out.println("上传照片：" + e);
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String res = response.body().string();
                            System.out.println("上传照片：" + res);
                        }
                    });
                    img_icon.setImageURI(image_uri);
                }

                break;
            case NAME:
                if (null != data) {
                    String name = data.getStringExtra("et_name");
                    tv_name.setText(name);
                    String flag = data.getStringExtra("change_flag");
                    if (flag.equals("true")) {
                        change_flag = true;
                    }
                }

                break;
            case AREA:
                if (null != data) {
                    String area = data.getStringExtra("spinner_area");
                    tv_area.setText(area);
                    String flag = data.getStringExtra("change_flag");
                    if (flag.equals("true")) {
                        change_flag = true;
                    }
                }
                break;
            case SIGNATURE:
                if (null != data) {
                    String signature = data.getStringExtra("et_signature");
                    tv_signature.setText(signature);
                    String flag = data.getStringExtra("change_flag");
                    if (flag.equals("true")) {
                        change_flag = true;
                    }
                }
                break;
            default:
                break;
        }
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
