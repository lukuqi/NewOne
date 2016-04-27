package com.lukuqi.newone.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
 * 忘记密码 第二步：重置密码，输入验证码
 */
public class ForgetPwdTwoActivity extends AppCompatActivity {

    String pwd; //密码
    String pwd_again;//再次输入密码
    String sms;//验证码
    String phone;//手机号码
    @Bind(R.id.et_forget_password)
    EditText et_forget_password;
    @Bind(R.id.et_forget_password_again)
    EditText et_forget_password_again;
    @Bind(R.id.et_forget_verify)
    EditText et_forget_verify;

    @Bind(R.id.btn_forget_done)
    Button btn_forget_done;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd_two);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initView();

    }


    public void initView() {
        ButterKnife.bind(this);
        String verify = et_forget_verify.getText().toString();
        //设置文本内容监听
        et_forget_password.addTextChangedListener(new TextListener());
        et_forget_password_again.addTextChangedListener(new TextListener());
        et_forget_verify.addTextChangedListener(new TextListener());

        btn_forget_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwd = et_forget_password.getText().toString();
                pwd_again = et_forget_password_again.getText().toString();
                sms = et_forget_verify.getText().toString();
                if (pwd.equals(pwd_again)) {
                    phone = getIntent().getExtras().getString("Number");
                    //重置密码
                    resetPassword(phone, pwd, sms);
                } else {
                    Utils.alertDialog(ForgetPwdTwoActivity.this, "密码不一致，请重新输入！");
                }
//                startActivity(new Intent(ForgetPwdTwoActivity.this, LoginActivity.class));
            }
        });

    }

    /**
     * 重置用户密码
     *
     * @param phone 手机号码
     * @param pwd   密码
     * @param sms   验证码
     */
    public void resetPassword(final String phone, String pwd, String sms) {
//显示ProgressDialog
        final ProgressDialog progressDialog = ProgressDialog.show(ForgetPwdTwoActivity.this, null, "请稍等...", true, false);
        //验证登录
        OkHttpUtils okHttpUtils = OkHttpUtils.getInstance(getApplicationContext());
        String url = IP.IP_PARENT + "/resetPassword";
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("tel", phone);
        paramsMap.put("pwd", pwd);
        paramsMap.put("sms", sms);
        okHttpUtils.postAsyn(url, paramsMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        //AlertDialog 对话
                        Utils.alertDialog(ForgetPwdTwoActivity.this, "请检查网络连接！");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                progressDialog.dismiss();
                String res = response.body().string();
                System.out.println("这里是返回内容" + res);
                Gson gson = new Gson();
                final Base base = gson.fromJson(res, Base.class);
                progressDialog.dismiss();
                if (base.getCode().equals("10000")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ForgetPwdTwoActivity.this);
                            //    设置Content来显示一个信息
                            builder.setMessage(base.getMessage());
                            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(ForgetPwdTwoActivity.this, LoginActivity.class));
                                }
                            });
                            builder.show();

                        }
                    });

                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //AlertDialog 对话
                            Utils.alertDialog(ForgetPwdTwoActivity.this, base.getMessage());
                        }
                    });
                }
            }
        });
    }

    /**
     * 内部类 监听编辑框
     */
    class TextListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            pwd = et_forget_password.getText().toString();
            pwd_again = et_forget_password_again.getText().toString();
            sms = et_forget_verify.getText().toString();

            if (pwd.length() > 0 && pwd_again.length() > 0 && sms.length() > 0) {
                btn_forget_done.setEnabled(true);
                btn_forget_done.setBackgroundResource(R.color.blue);

            } else {
                btn_forget_done.setEnabled(false);
                btn_forget_done.setBackgroundResource(R.color.gray);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
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
