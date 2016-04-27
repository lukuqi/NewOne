package com.lukuqi.newone.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lukuqi.newone.Bean.Base;
import com.lukuqi.newone.MainActivity;
import com.lukuqi.newone.R;
import com.lukuqi.newone.http.OkHttpUtils;
import com.lukuqi.newone.util.ConstantVar;
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
 * 用户登录activity
 */
public class LoginActivity extends AppCompatActivity {

    String tel;
    public final static String EXTRA_PARAM = "param";
    @Bind(R.id.et_phone)
    EditText et_phone;
    @Bind(R.id.et_password)
    EditText et_password;
    @Bind(R.id.btn_login)
    Button btn_login;
    @Bind(R.id.tv_forget)
    TextView tv_forget;
    @Bind(R.id.tv_new)
    TextView tv_new;
    private int flag = 0;//按钮可点击标记，0表示不可点击


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //初始化组件
        initView();
        //进入主界面
        login();


    }

    /**
     * 初始化组件
     */
    private void initView() {
        ButterKnife.bind(this);
        //获取intent传过来的数据
        tel = et_phone.getText().toString();
        String param = getIntent().getStringExtra(EXTRA_PARAM);
        et_phone.setText(param);

        et_phone.addTextChangedListener(new TextListener());
        et_password.addTextChangedListener(new TextListener());


        tv_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterOneActivity.class));
            }
        });
        tv_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgetPwdOneActivity.class));
            }
        });

    }

    /**
     * 用户验证
     */
    private void login() {
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 1) {
                    //显示ProgressDialog
                    final ProgressDialog progressDialog = ProgressDialog.show(LoginActivity.this, null, "请稍等...", true, false);
                    //验证登录
                    String url = IP.IP_PARENT + "/login";
                    String pwd = et_password.getText().toString();
                    String phone = et_phone.getText().toString();
                    HashMap<String, String> paramsMap = new HashMap<>();
                    paramsMap.put("tel", phone);
                    paramsMap.put("pwd", pwd);
                    OkHttpUtils okHttpUtils = OkHttpUtils.getInstance(getApplicationContext());

                    String json = "{'tel':" + phone + ",'pwd': " + pwd + "}";
                    okHttpUtils.postAsyn(url, paramsMap, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    //AlertDialog 对话
                                    Utils.alertDialog(LoginActivity.this, "请检查网络连接！");
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String res = response.body().string();
                            Gson gson = new Gson();
                            final Base base = gson.fromJson(res, Base.class);
                            progressDialog.dismiss();
                            System.out.println("Login返回内容：" + res);
                            if (base.getCode().equals("10000")) {
                                //存储用户号码
                                SharedPreferences sharedPreferences = getSharedPreferences(ConstantVar.USER_TEL, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("tel", tel);
                                editor.apply();

                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //AlertDialog 对话
                                        Utils.alertDialog(LoginActivity.this, base.getMessage());
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 内部类
     * 监听手机号码和密码是否有值，是，登录按钮可点击；否，不可点击置灰
     */
    class TextListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String phone = et_phone.getText().toString();
            String password = et_password.getText().toString();
//            System.out.println(phone + "--" + password);
            if (phone.length() > 0 && password.length() > 0) {
                btn_login.setEnabled(true);
                flag = 1;
                btn_login.setBackgroundResource(R.color.blue);

            } else {
                btn_login.setEnabled(false);
                flag = 0;
                btn_login.setBackgroundResource(R.color.gray);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}

