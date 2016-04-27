package com.lukuqi.newone.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.lukuqi.newone.Bean.Base;
import com.lukuqi.newone.R;
import com.lukuqi.newone.http.OkHttpUtils;
import com.lukuqi.newone.util.IP;
import com.lukuqi.newone.util.Utils;


import java.io.IOException;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 忘记密码 第一步：输入手机号码
 */
public class ForgetPwdOneActivity extends AppCompatActivity {

    @Bind(R.id.et_forget_phone)
    EditText et_forget_phone;
    @Bind(R.id.btn_forget_next)
    Button btn_forget_next;

    private OkHttpUtils okHttpUtils; //Okhttp请求工具类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initView();

    }

    public void initView() {
        ButterKnife.bind(this);
        et_forget_phone.setFocusable(true);
        et_forget_phone.requestFocus();
        et_forget_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String phone = et_forget_phone.getText().toString();
                if (phone.length() == 11) {
                    btn_forget_next.setEnabled(true);
                    btn_forget_next.setBackgroundResource(R.color.blue);
                } else {
                    btn_forget_next.setEnabled(false);
                    btn_forget_next.setBackgroundResource(R.color.gray);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        btn_forget_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //验证手机号码是否已注册
//                isRegister(et_forget_phone.getText().toString());
                final String phone = et_forget_phone.getText().toString();
                if (Utils.isMobileNO(phone)) {
                    //等待画面
                    final ProgressDialog progressDialog = ProgressDialog.show(ForgetPwdOneActivity.this, null, "请稍等……", true, false);
                    //号码检测地址
                    String check_url = IP.IP_PARENT + "/isRegister";
                    okHttpUtils = OkHttpUtils.getInstance(getApplicationContext());
                    HashMap<String, String> paramsMap = new HashMap<>();
                    paramsMap.put("tel", phone);
                    okHttpUtils.postAsyn(check_url, paramsMap, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    //AlertDialog 对话
                                    Utils.alertDialog(ForgetPwdOneActivity.this, "请检查网络连接！");
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            progressDialog.dismiss();
                            String res = response.body().string();
                            System.out.println("号码检测结果：" + res);
                            Gson gson = new Gson();
                            final Base base = gson.fromJson(res, Base.class);
                            if (base.getCode().equals("10000")) {
//                                Intent intent = new Intent(ForgetPwdOneActivity.this, ForgetPwdTwoActivity.class);
//                                Bundle bundle = new Bundle();
//                                bundle.putString("Number", phone);
//                                intent.putExtras(bundle);
//                                startActivity(intent);
//                                finish();
                                //发送验证码
                                sendVerifySMS(phone);
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //AlertDialog 对话
                                        Utils.alertDialog(ForgetPwdOneActivity.this, base.getMessage());
                                    }
                                });
                            }
                        }
                    });
                } else {
                    Utils.alertDialog(ForgetPwdOneActivity.this, "请输入正确的手机号码！");
                }


            }
        });
    }

    /**
     * 向手机发送验证码
     *
     * @param phone 手机号码
     */
    public void sendVerifySMS(final String phone) {
        okHttpUtils = OkHttpUtils.getInstance(getApplicationContext());

        String url = IP.IP_SMS + "/sendRegisterSMS";
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("to", phone);
        paramsMap.put("datas", "null");
        paramsMap.put("tempId", "1");
        okHttpUtils.postAsyn(url, paramsMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //AlertDialog 对话
                        Utils.alertDialog(ForgetPwdOneActivity.this, "请检查网络连接！");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                System.out.println("发送验证码：" + res);
                Gson gson = new Gson();
                final Base base = gson.fromJson(res, Base.class);
                if (base.getCode().equals("10000")) {
//                    Intent intent = new Intent(ForgetPwdOneActivity.this, ForgetPwdTwoActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("Number", phone);
//                    intent.putExtras(bundle);
//                    startActivity(intent);
                    //跳转下一个activity
                    Intent intent = new Intent(ForgetPwdOneActivity.this, ForgetPwdTwoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Number", phone);
                    intent.putExtras(bundle);
                    startActivity(intent);
//                    finish();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //AlertDialog 对话
                            Utils.alertDialog(ForgetPwdOneActivity.this, base.getMessage());
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
