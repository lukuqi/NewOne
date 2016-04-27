package com.lukuqi.newone.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by mr.right on 2016/3/31.
 */
public class SQLiteUtils {
    SQLiteDatabase db;

    public SQLiteUtils(String dbName) {
        dbName = "newone.db";
        db = SQLiteDatabase.openOrCreateDatabase(dbName, null);


    }

    //增删改查
    public void insert(String sql) {
        sql = "create table parent(id integer primary key autoincrement, tel varchar,name varchar, sex integer,area varchar,signature varchar";
        db.execSQL(sql);
    }

    public void delete() {

    }

    public void update() {

    }

    public void query() {

    }
}
