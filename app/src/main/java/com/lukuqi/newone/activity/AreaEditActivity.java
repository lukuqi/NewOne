package com.lukuqi.newone.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Xml;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.lukuqi.newone.bean.City;
import com.lukuqi.newone.bean.Country;
import com.lukuqi.newone.bean.Province;
import com.lukuqi.newone.R;
import com.lukuqi.newone.util.PullXml;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;


/**
 * 用户修改区域
 */
public class AreaEditActivity extends AppCompatActivity {

    private Spinner spinner_country;
    private Spinner spinner_province;
    private Spinner spinner_city;
    private Context mContext;
    private SQLiteDatabase db;

    final XmlPullParser xmlPullParser = Xml.newPullParser();


    List<Country> countries;
    List<Province> provinces;
    List<City> cities;
    List<String> str_country;
    List<String> str_province;
    List<String> str_city;

    private Button btn_area_save;
    private String area_province;
    private String area_city;
    private String area;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_edit);
        mContext = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //初始化数据
        try {
            initData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //初始化界面
        initView();
    }

    private void initData() throws Exception {

        //读取国家xml
        Country country = new Country();
        countries = new PullXml<Country>(this, country, xmlPullParser) {
            @Override
            public Country saveTag() {
                Country country = new Country();
                country.setId(Integer.valueOf(xmlPullParser.getAttributeValue(1)));// 节点包含属性参数，索引从0开始
                country.setName(xmlPullParser.getAttributeValue(0));

                return country;
            }
        }.pulltoXML("Countries.xml", "Country");

        //获取国家名到数组中
        str_country = new ArrayList<>();
        for (int i = 0; i < countries.size(); i++) {
            str_country.add(countries.get(i).getName());
        }
        //读取省份xml
        Province province = new Province();
        provinces = new PullXml<Province>(this, province, xmlPullParser) {
            @Override
            public Province saveTag() {
                Province province = new Province();
                province.setcId(Integer.valueOf(xmlPullParser.getAttributeValue(0)));
                province.setId(Integer.valueOf(xmlPullParser.getAttributeValue(1)));
                province.setName(xmlPullParser.getAttributeValue(2));

                return province;
            }
        }.pulltoXML("Provinces.xml", "Province");

        //读取地区xml
        City city = new City();
        cities = new PullXml<City>(this, city, xmlPullParser) {
            @Override
            public City saveTag() {
                City city = new City();
                city.setpId(Integer.valueOf(xmlPullParser.getAttributeValue(2)));
                city.setId(Integer.valueOf(xmlPullParser.getAttributeValue(1)));
                city.setName(xmlPullParser.getAttributeValue(0));

                return city;
            }
        }.pulltoXML("Cities.xml", "City");
    }

    private void initView() {
        btn_area_save = (Button) findViewById(R.id.btn_area_save);

        spinner_country = (Spinner) findViewById(R.id.spinner_country);
        spinner_province = (Spinner) findViewById(R.id.spinner_province);
        spinner_city = (Spinner) findViewById(R.id.spinner_city);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, str_country);
        spinner_country.setAdapter(adapter);
        spinner_country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("position:" + str_country.get(position));

                for (int i = 0; i < countries.size(); i++) {
                    if (str_country.get(position).equals(countries.get(i).getName())) {
//                        System.out.println("cName:" + countries.get(i).getName());
//                        System.out.println("cId:" + countries.get(i).getId());
                        str_province = new ArrayList<>();
                        for (int j = 0; j < provinces.size(); j++) {
                            if (countries.get(i).getId() == provinces.get(j).getcId())
//                                System.out.println("provinces cId:" + provinces.get(j).getcId() + "Name" + provinces.get(j).getName());
                                str_province.add(provinces.get(j).getName());
                        }
                        break;
                    }
                    System.out.println("这里执行不了！");
                }
                ArrayAdapter<String> adapter1 = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, str_province);
                spinner_province.setAdapter(adapter1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                area_province = str_province.get(position);

                for (int i = 0; i < provinces.size(); i++) {
                    System.out.println("province---position:" + str_province.get(position));
                    if (str_province.get(position).equals(provinces.get(i).getName())) {
                        System.out.println("cName:" + provinces.get(i).getName());
                        System.out.println("pId:" + provinces.get(i).getId());
                        str_city = new ArrayList<>();
                        for (int j = 0; j < cities.size(); j++) {
                            if (provinces.get(i).getId() == cities.get(j).getpId()) {
                                System.out.println("city pId:" + cities.get(j).getpId() + "Name" + cities.get(j).getName());
                                str_city.add(cities.get(j).getName());
                            }
                        }
                        break;
                    }
                }
                ArrayAdapter<String> adapter2 = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_dropdown_item, str_city);
                spinner_city.setAdapter(adapter2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinner_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                area_city = str_city.get(position);
                System.out.println("City Position:" + position);
                btn_area_save.setEnabled(true);
                btn_area_save.setBackgroundResource(R.color.blue);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_area_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(area_province + "---" + area_city);
                if (area_province.equals(area_city)) {
                    area = area_province;
                } else {
                    area = area_province + " " + area_city;
                }
                localSave();
                returnResult();
                finish();
            }
        });
    }

    /**
     * 资料更新到本地SQLite数据库
     */
    private void localSave() {
        String tel = getIntent().getExtras().getString("tel");
        db = openOrCreateDatabase("newone.db", Context.MODE_PRIVATE, null);
        //获取游标
        Cursor cur = db.rawQuery("SELECT tel FROM parent where tel =" + tel, null);
        //ContentValues以键值对的形式存放数据
        ContentValues cv = new ContentValues();
        if (cur != null) {
            // System.out.println("cur:" + cur.moveToFirst());
            //游标至于第一个位置,如果游标为空，返回false
            if (cur.moveToFirst()) {
                cv.put("area", area);
                db.update("parent", cv, "tel=?", new String[]{tel});
            } else {
                cv.put("tel", tel);
                cv.put("area", area);
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
        int resultCode = ProfileSettingActivity.AREA;
        Intent mIntent = new Intent();
        mIntent.putExtra("spinner_area", area);
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
