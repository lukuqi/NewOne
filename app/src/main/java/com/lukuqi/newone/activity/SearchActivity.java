package com.lukuqi.newone.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lukuqi.newone.R;
import com.lukuqi.newone.util.Utils;
import com.lukuqi.newone.util.XmppConn;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

public class SearchActivity extends AppCompatActivity {
    EditText et_add;
    Button btn_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        initView();
    }

    private void initView() {
        et_add = (EditText) findViewById(R.id.et_add);
        btn_add = (Button) findViewById(R.id.btn_add);

//        et_add.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XMPPTCPConnection connection = XmppConn.getInstance().getXmpptcpConnection();
                //订阅好友的Presence
                Stanza stanza = new Presence(Presence.Type.subscribe);
                try {
//                    stanza.setTo(JidCreate.from("18380465202@laptop-c7q32nn2"));
                    stanza.setTo(JidCreate.from(et_add.getText().toString() + "@laptop-c7q32nn2"));
                } catch (XmppStringprepException e) {
                    e.printStackTrace();
                }
//                            String user = "";
//                            stanza.setTo();
//                            //presence.setMode(Presence.Mode.available);
                try {
                    connection.sendStanza(stanza);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                AlertDialog.Builder alert_builder = new AlertDialog.Builder(SearchActivity.this);
//                alert_builder.setTitle("确认发送");
                alert_builder.setMessage("添加好友请求已发送！");
                alert_builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });     //    设置一个NegativeButton
//                alert_builder.setNegativeButton("取消", null);

                alert_builder.show();
            }
        });
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
