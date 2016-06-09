package com.lukuqi.newone.activity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.lukuqi.newone.R;
import com.lukuqi.newone.adapter.ChatRecyclerAdapter;
import com.lukuqi.newone.util.XmppConn;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.EntityJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private String jid;
    private XmppConn xmppConn;
    private XMPPConnection connection;
    private RecyclerView recyclerView;
    private ChatRecyclerAdapter adapter;
    private Context context;
    private List<HashMap<String, String>> datas;
    public final static String ME = "com.lukuqi.newone.me";
    public final static String OTHER = "com.lukuqi.newone.other";
    ChatManager chatmanager;
    Chat newChat = null;
    List<HashMap<String, String>> chat_list = new ArrayList<>();
    private SQLiteDatabase db;
    private EditText input;
    private Button send;
    SimpleDateFormat df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
        df = new SimpleDateFormat("HH:mm");//设置日期格式
        System.out.println("Time:" + df.format(new Date()));
        initDb();//本地数据库
        getLocalChat();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //保存聊天记录
        saveLoclaChat(chat_list);
//        for (HashMap<String, String> list : chat_list) {
//            System.out.println("who" + chat_list.get(0).get("who"));
//            System.out.println("message" + chat_list.get(0).get("message"));
//            System.out.println("time" + chat_list.get(0).get("time"));
//        }
    }

    /**
     * 初始化数据库
     */
    private void initDb() {
        db = openOrCreateDatabase("newone.db", Context.MODE_PRIVATE, null);
        String sql = "create table if not exists chat(id integer primary key autoincrement,who varchar, message text, createtime int)";
        db.execSQL(sql);
        db.close();
    }

    /**
     * 保存聊天记录
     *
     * @param lists
     */
    private void saveLoclaChat(List<HashMap<String, String>> lists) {
        db = openOrCreateDatabase("newone.db", Context.MODE_PRIVATE, null);
        //ContentValues以键值对的形式存放数据
        ContentValues cv = new ContentValues();
        for (HashMap<String, String> list : lists) {
            cv.put("who", list.get("who"));
            System.out.println("save WHO---------" + list.get("who"));
            cv.put("message", list.get("message"));
            cv.put("createtime", list.get("createtime"));
            db.insert("chat", null, cv);
        }
        //插入ContentValues中的数据
//        db.update("parent", cv, "tel=?", new String[]{tel});
//                        db.insert("parent", null, cv);
        db.close();
    }


    /**
     * 获取本地聊天缓存
     */
    private void getLocalChat() {
        db = openOrCreateDatabase("newone.db", Context.MODE_PRIVATE, null);
        Cursor cur = db.rawQuery("SELECT * FROM chat ", null);
        List<HashMap<String, String>> datas = new ArrayList<>();
        if (cur != null) {
            //游标至于第一个位置
            if (cur.moveToFirst()) {
                do {
                    HashMap<String, String> hashMap = new HashMap<>();
                    int titleColumn = cur.getColumnIndex("who");
                    int urlColumn = cur.getColumnIndex("message");
                    int contentColumn = cur.getColumnIndex("createtime");
//                    tv_name.setText(cur.getString(nameColumn));
//                    tv_sex.setText((cur.getInt(sexColumn) == 1) ? "男" : "女");
//                    tv_area.setText(cur.getString(areaColumn));
//                    tv_signature.setText(cur.getString(signatureColumn));
                    hashMap.put("who", cur.getString(titleColumn));
                    System.out.println("get WHO---------" + cur.getString(titleColumn));
                    hashMap.put("message", cur.getString(urlColumn));
                    hashMap.put("createtime", cur.getString(contentColumn));
                    datas.add(hashMap);
//                    System.out.println("numColumn: " + cur.getString(nameColumn) + "" + ((cur.getString(sexColumn).equals("1")) ? "男" : "女") + cur.getString(areaColumn) + cur.getString(signatureColumn));
//                    System.out.println("NewsColumn: " + cur.getString(titleColumn) + "" + (cur.getString(urlColumn)) + cur.getString(contentColumn) + cur.getString(imageColumn)+ cur.getString(sourceColumn));
                } while (cur.moveToNext());
            }
        }
        db.close();
        adapter.addMessage(datas);

    }

    private void initView() {
        context = this;
        datas = new ArrayList<>();
        jid = getIntent().getStringExtra("Jid");
        xmppConn = XmppConn.getInstance();
        connection = xmppConn.getXmpptcpConnection();
        // Assume we've created an XMPPConnection name "connection"._
        chatmanager = ChatManager.getInstanceFor(connection);
        chatmanager.addChatListener(new ChatManagerListener() {
            @Override
            public void chatCreated(Chat chat, boolean createdLocally) {
                if (!createdLocally)
                    chat.addMessageListener(new MyNewMessageListener());
//                System.out.println("local chat: " + chat);
            }
        });

        try {
            newChat = chatmanager.createChat((EntityJid) JidCreate.from(jid), new ChatMessageListener() {
                @Override
                public void processMessage(Chat chat, Message message) {
                    System.out.println("sfddsffsdf");
                }
            });
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        input = (EditText) findViewById(R.id.et_input);
        send = (Button) findViewById(R.id.btn_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    newChat.sendMessage(input.getText().toString()); //发送消息
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //adapter设置数据 message：消息内容 who：发送对象 （ME：自己，OTHER:别人）
                            final List<HashMap<String, String>> msg = new ArrayList<>();
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("message", input.getText().toString());
                            hashMap.put("who", ME);
                            hashMap.put("createtime", df.format(new Date()));
                            msg.add(hashMap);
                            chat_list.add(hashMap);//加载到本地数据
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.addMessage(msg);
                                }
                            });
                            recyclerView.smoothScrollToPosition(datas.size());//recyclerView自动往上滚动
                        }
                    });
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                input.setText("");

            }
        });
        //设置recyclerView
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_chat);
        recyclerView.setHasFixedSize(true);
        adapter = new ChatRecyclerAdapter(context, datas);
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
//        linearLayoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }


    class MyNewMessageListener implements ChatMessageListener {
        @Override
        public void processMessage(Chat chat, final Message message) {
//            System.out.println(" chat:" + chat.getParticipant());
//            System.out.println(" Jid:" + jid);
//            if (chat.getParticipant().toString().contains(jid)) {
//
//            }
            if (message.getBody() != null) {
                if (chat.getParticipant().toString().contains(jid)) {
                    System.out.println(" message:" + message);
                    final List<HashMap<String, String>> msg = new ArrayList<>();
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("message", message.getBody());
                    hashMap.put("who", OTHER);
                    hashMap.put("createtime", df.format(new Date()));
                    msg.add(hashMap);
                    chat_list.add(hashMap);//加载到本地数据
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.addMessage(msg);
                        }
                    });
                } else {
                    System.out.println("otherChat: " + chat.getParticipant());
                }


            }

        }
    }
}
