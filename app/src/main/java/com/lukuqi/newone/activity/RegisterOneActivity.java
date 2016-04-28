package com.lukuqi.newone.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.lukuqi.newone.bean.Base;
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
import okhttp3.Response;

/**
 * 手机号码注册账号 第一步：输入手机号码
 */
public class RegisterOneActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    OkHttpUtils okHttpUtils;//OkHttp 请求工具类


    @Bind(R.id.et_register_phone)
    EditText et_register_phone;
    @Bind(R.id.btn_register_next)
    Button btn_register_next;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_one);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initView();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        ButterKnife.bind(this);
        //编辑文本获取焦点
        et_register_phone.setFocusable(true);
//        et_register_phone.setFocusableInTouchMode(true);
        et_register_phone.requestFocus();
        et_register_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_register_phone.getText().length() == 11) {
                    btn_register_next.setEnabled(true);
                    btn_register_next.setBackgroundResource(R.color.blue);
                } else {
                    btn_register_next.setEnabled(false);
                    btn_register_next.setBackgroundResource(R.color.gray);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btn_register_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phone = et_register_phone.getText().toString();

                if (Utils.isMobileNO(phone)) {
                    AlertDialog.Builder alert_builder = new AlertDialog.Builder(RegisterOneActivity.this);
                    alert_builder.setTitle("确认发送");
                    alert_builder.setMessage("我们将发送验证码短信到这个号码：" + et_register_phone.getText().toString());
                    alert_builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //显示ProgressDialog
                            progressDialog = ProgressDialog.show(RegisterOneActivity.this, null, "请稍等...", true, false);
                            //检测手机号码是否已注册
                            isRegistered(phone);

                        }
                    });
                    //    设置一个NegativeButton
                    alert_builder.setNegativeButton("取消", null);
                    alert_builder.show();
                } else {
                    Utils.alertDialog(RegisterOneActivity.this, "请输入正确的手机号码！");
                }
            }
        });
    }

    /**
     * 检测手机号码是否已注册     返回数据-code：10000 表示已注册
     *
     * @param phone 手机号码
     */
    public void isRegistered(final String phone) {
        String url = IP.IP_PARENT + "/isRegister";
        okHttpUtils = OkHttpUtils.getInstance(getApplicationContext());
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("tel", phone);

        okHttpUtils.postAsyn(url, paramsMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        //AlertDialog 对话
                        Utils.alertDialog(RegisterOneActivity.this, "请检查网络连接！");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                progressDialog.dismiss();
                String res = response.body().string();
                System.out.println("用户是否已注册：" + res);
                Gson gson = new Gson();
                final Base base = gson.fromJson(res, Base.class);
                if (base.getCode().equals("00000")) {
                    //手机号码注册
                    register(phone);
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //AlertDialog 对话
                            Utils.alertDialog(RegisterOneActivity.this, base.getMessage());
                        }
                    });
                }
            }
        });
    }

    /**
     * 手机号码注册
     *
     * @param phone 手机号码
     */
    public void register(final String phone) {
        String url = IP.IP_SMS + "/sendRegisterSMS";
        okHttpUtils = OkHttpUtils.getInstance(getApplicationContext());
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
                        Utils.alertDialog(RegisterOneActivity.this, "请检查网络连接！");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                System.out.println("发送注册验证码：" + res);
                Gson gson = new Gson();
                final Base base = gson.fromJson(res, Base.class);
                if (base.getCode().equals("10000")) {
                    Intent intent = new Intent(RegisterOneActivity.this, RegisterTwoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("Number", phone);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //AlertDialog 对话
                            Utils.alertDialog(RegisterOneActivity.this, base.getMessage());
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
