package com.lukuqi.newone.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.lukuqi.newone.MainActivity;
import com.lukuqi.newone.R;
import com.lukuqi.newone.util.ConstantVar;

import java.io.File;

public class SplashActivity extends AppCompatActivity {
    private String tel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //全屏
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    checkDir();
                    loginCheck();
                    finish();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    /**
     * 检测用户是否已登录
     * <p>
     * 是：进入主界面
     * 否：进入登录界面
     */
    private void loginCheck() {
        SharedPreferences sharedPreferences = getSharedPreferences(ConstantVar.USER_TEL, MODE_PRIVATE);
        tel = sharedPreferences.getString("tel", "null");
        if (!tel.equals("null")) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        } else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }
    }

    private void checkDir() {
        File basePath = new File(Environment.getExternalStorageDirectory(), "NewOne");
        if (!basePath.exists()) {
            basePath.mkdir();
        }
    }
}
