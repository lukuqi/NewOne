package com.lukuqi.newone.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
 * 手机号注册 第二步：验证码验证
 */
public class RegisterTwoActivity extends AppCompatActivity {

    @Bind(R.id.tv_verify_phone)
    TextView et_vertify_phone;
    @Bind(R.id.et_password)
    EditText et_password;
    @Bind(R.id.et_verify_code)
    EditText et_verify_code;
    @Bind(R.id.btn_register_done)
    Button btn_register_done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_two);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initView();

    }

    private void initView() {
        ButterKnife.bind(this);
        final String verify_phone = getIntent().getExtras().getString("Number");
        et_vertify_phone.setText(verify_phone);

        btn_register_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    //显示ProgressDialog
                    final ProgressDialog progressDialog = ProgressDialog.show(RegisterTwoActivity.this, null, "请稍等...", true, false);
                    //验证登录
                    String url = IP.IP_PARENT + "/register";
                    OkHttpUtils okHttpUtils = OkHttpUtils.getInstance(getApplicationContext());
                    HashMap<String, String> paramsMap = new HashMap<>();
                    paramsMap.put("tel", verify_phone);
                    paramsMap.put("pwd", et_password.getText().toString());
                    paramsMap.put("sms", et_verify_code.getText().toString());

                    okHttpUtils.postAsyn(url, paramsMap, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    //AlertDialog 对话
                                    Utils.alertDialog(RegisterTwoActivity.this, "请检查网络连接！");
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            progressDialog.dismiss();
                            String res = response.body().string();
                            System.out.println("注册成功返回内容" + res);
                            Gson gson = new Gson();
                            final Base base = gson.fromJson(res, Base.class);
                            progressDialog.dismiss();
                            if (base.getCode().equals("10000")) {
//                                Intent intent = new Intent(RegisterTwoActivity.this,LoginActivity.class);
//                                Bundle bundle = new Bundle();
//                                bundle.putString(LoginActivity.EXTRA_PARAM,verify_phone);
//                                intent.putExtras(bundle);
//                                intent.putExtra(LoginActivity.EXTRA_PARAM,verify_phone);
//                                startActivity(intent);
                                startActivity(new Intent(RegisterTwoActivity.this, LoginActivity.class));
                                finish();
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //AlertDialog 对话
                                        Utils.alertDialog(RegisterTwoActivity.this, base.getMessage());
                                    }
                                });
                            }
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
