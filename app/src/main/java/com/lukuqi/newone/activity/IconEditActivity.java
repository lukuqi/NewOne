package com.lukuqi.newone.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Xml;
import android.view.MenuItem;

import com.lukuqi.newone.bean.Country;
import com.lukuqi.newone.R;
import com.lukuqi.newone.util.PullXml;

import org.xmlpull.v1.XmlPullParser;

import java.util.List;

public class IconEditActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        try {
            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() throws Exception {


        List<Country> countries;
        final XmlPullParser xmlPullParser = Xml.newPullParser();

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
        System.out.println("counsdffsadf:" + countries);
        for (int i = 0; i < countries.size(); i++) {
            System.out.println("name:" + countries.get(i).getName());
            System.out.println("id:" + countries.get(i).getId());
        }

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
