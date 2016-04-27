package com.lukuqi.newone.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lukuqi.newone.Bean.UserBase;
import com.lukuqi.newone.Bean.UserInfo;
import com.lukuqi.newone.R;
import com.lukuqi.newone.activity.AlbumActivity;
import com.lukuqi.newone.activity.ProfileSettingActivity;
import com.lukuqi.newone.activity.SchoolActivity;
import com.lukuqi.newone.activity.SettingActivity;
import com.lukuqi.newone.adapter.MsgRecyclerAdapter;
import com.lukuqi.newone.http.OkHttpUtils;
import com.lukuqi.newone.util.CheckDir;
import com.lukuqi.newone.util.ConstantVar;
import com.lukuqi.newone.util.IP;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * 个人信息界面
 * Created by right on 2015/12/29.
 */
public class ProfileFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private MsgRecyclerAdapter mMsgRecyclerAdapter;
    private List<String> mDatas;
    private Context mCotext;
    private RelativeLayout rl_info;         //布局：个人信息
    private CircleImageView cImg_avatar;
    private TextView tv_username;           //昵称
    private TextView tv_school;             //学校
    private TextView tv_album;              //相册
    private TextView tv_setting;            //设置

    private String tel;                    //sharedPreference中,保存号码的值

    private OkHttpUtils okHttpUtils; //OkHttp 请求工具类

    public static ProfileFragment newInstance() {
        Bundle args = new Bundle();

        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("ProfileFragment :onCreate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("ProfileFragment :onCreateView");
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        initData();
        initView(view);
        //获取用户信息
        getInfo(tel);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    private void initData() {

        mDatas = new ArrayList<>();
        for (int i = 'A'; i < 'z'; i++) {
            mDatas.add("" + (char) i);
        }
    }

    /**
     * 初始化界面
     *
     * @param view 布局View
     */
    private void initView(View view) {
        mCotext = getContext();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(ConstantVar.USER_TEL, Context.MODE_PRIVATE);

        tel = sharedPreferences.getString("tel", "null");
        rl_info = (RelativeLayout) view.findViewById(R.id.rl_info);
        cImg_avatar = (CircleImageView) view.findViewById(R.id.cImg_avatar);
        tv_school = (TextView) view.findViewById(R.id.tv_school);
        tv_album = (TextView) view.findViewById(R.id.tv_album);
        tv_setting = (TextView) view.findViewById(R.id.tv_setting);
        tv_username = (TextView) view.findViewById(R.id.tv_username);
        rl_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mCotext, ProfileSettingActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("tel", tel);
                intent.putExtras(bundle);
                startActivity(intent);
//                startActivity(new Intent(mCotext, ProfileSettingActivity.class));

            }
        });
        tv_school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mCotext, SchoolActivity.class));

            }
        });
        tv_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mCotext, AlbumActivity.class));

            }
        });
        tv_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mCotext, SettingActivity.class));
            }
        });

    }

    /**
     * 访问网络获取用户信息
     *
     * @param phone 手机号码
     */
    public void getInfo(String phone) {
        okHttpUtils = OkHttpUtils.getInstance(getContext());
        String url = IP.IP_PARENT + "/getInfo";
        final HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("tel", phone);

        okHttpUtils.postAsyn(url, paramsMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Gson gson = new Gson();
                System.out.println("个人信息返回内容" + res);
                UserBase user = gson.fromJson(res, UserBase.class);
                final List<UserInfo> userInfos = user.getMessage();
                if (user.getCode().equals("10000")) {
                    if (userInfos.get(0).getIcon() != null) {
                        //头像icon地址
                        String url = IP.HOST + "/Upload/" + userInfos.get(0).getIcon();
                        okHttpUtils = OkHttpUtils.getInstance(getContext());

                        okHttpUtils.getAsyn(url, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                InputStream is = response.body().byteStream();
//                                CheckDir.checkBasePath(new File(Environment.getExternalStorageDirectory(), "NewOne"));
                                File imagePath = CheckDir.checkImageFile("icon.png");
//                                if (!basePath.exists()) {
//                                    basePath.mkdir();
//                                    System.out.println("文件夹不存在！");
//                                } else {
//                                    if (!imagePath.exists()) {
//                                        imagePath.createNewFile();
//                                        System.out.println("文件不存在！");
//                                    }
//                                }
                                FileOutputStream fileOutputStream = new FileOutputStream(imagePath);
                                System.out.println("头像is返回内容: " + is);
                                final Bitmap bm = BitmapFactory.decodeStream(is);
                                bm.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
//                                fileOutputStream.close();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        cImg_avatar.setImageBitmap(bm);
                                    }
                                });
                            }
                        });
                    }
                    if (userInfos.get(0).getName() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_username.setText(userInfos.get(0).getName());
                                System.out.println(userInfos.get(0).getName());
                            }
                        });
                    }
                }
            }
        });
    }
}
