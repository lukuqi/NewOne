package com.lukuqi.newone.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lukuqi.newone.R;

public class SignatureEditActivity extends AppCompatActivity {

    private EditText et_signature;
    private Button btn_signature_save;
    private SQLiteDatabase db;

    private String tel;
    private String signature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initView();
    }

    private void initView() {

        tel = getIntent().getExtras().getString("tel");
        signature = getIntent().getExtras().getString("signature");

        et_signature = (EditText) findViewById(R.id.et_signature);
        et_signature.setText(signature);
        //编辑文本获取焦点
        et_signature.requestFocus();
        et_signature.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btn_signature_save.setBackgroundResource(R.color.blue);
                btn_signature_save.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_signature_save = (Button) findViewById(R.id.btn_signature_save);
        btn_signature_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //资料保存到本地数据库
                localSave();
                //资料上传数据库
//                netSave();
                //回传数据
                returnResult();
                finish();
            }
        });

    }


    /**
     * 资料更新到本地SQLite数据库
     */
    private void localSave() {
        db = openOrCreateDatabase("newone.db", Context.MODE_PRIVATE, null);
        //获取游标
        Cursor cur = db.rawQuery("SELECT tel FROM parent where tel =" + tel, null);
        //ContentValues以键值对的形式存放数据
        ContentValues cv = new ContentValues();
        if (cur != null) {
            // System.out.println("cur:" + cur.moveToFirst());
            //游标至于第一个位置,如果游标为空，返回false
            if (cur.moveToFirst()) {
                cv.put("signature", et_signature.getText().toString());
                db.update("parent", cv, "tel=?", new String[]{tel});
            } else {
                cv.put("tel", tel);
                cv.put("et_signature", et_signature.getText().toString());
                //插入ContentValues中的数据
                db.insert("parent", null, cv);
            }
        }
        db.close();
    }

    /**
     * 返回数据到上个Activity
     */
    private void returnResult() {
        int resultCode = ProfileSettingActivity.SIGNATURE;
        Intent mIntent = new Intent();
        mIntent.putExtra("et_signature", et_signature.getText().toString());
        mIntent.putExtra("change_flag", "true");
        // 设置结果，并进行传送
        setResult(resultCode, mIntent);
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
