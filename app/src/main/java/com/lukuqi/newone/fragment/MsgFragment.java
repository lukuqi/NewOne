package com.lukuqi.newone.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lukuqi.newone.MainActivity;
import com.lukuqi.newone.R;
import com.lukuqi.newone.activity.ChatActivity;
import com.lukuqi.newone.activity.MessageActivity;
import com.lukuqi.newone.activity.NewActivity;
import com.lukuqi.newone.activity.RegisterTwoActivity;
import com.lukuqi.newone.adapter.MsgRecyclerAdapter;
import com.lukuqi.newone.bean.Base;
import com.lukuqi.newone.bean.UserBase;
import com.lukuqi.newone.bean.UserInfo;
import com.lukuqi.newone.http.OkHttpUtils;
import com.lukuqi.newone.receiver.NetworkConnReceiver;
import com.lukuqi.newone.service.ChatService;
import com.lukuqi.newone.service.NetStateService;
import com.lukuqi.newone.util.ConstantVar;
import com.lukuqi.newone.util.IP;
import com.lukuqi.newone.util.Utils;
import com.lukuqi.newone.util.XmppConn;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntries;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 消息界面
 * <p/>
 * Created by right on 2015/12/29.
 */
public class MsgFragment extends Fragment {
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private MsgRecyclerAdapter mMsgRecyclerAdapter;
    private List<UserInfo> mDatas = new ArrayList<>();
    List<RosterEntry> datas = new ArrayList<>();
    Collection<RosterEntry> entries;
    private Context mCotext;
    private TextView mInternet;
    private Boolean flag = false; //无网络flag
    public static final int CONNECTION = 0x01;
    public static final int DISCONNECTION = 0x00;
    public static Handler mHandler;
    private SQLiteDatabase db;
    List<HashMap<String, String>> time = new ArrayList<>();

    XmppConn xmppConn;
    XMPPTCPConnection connection;

    public static MsgFragment newInstance() {
        Bundle args = new Bundle();

        MsgFragment fragment = new MsgFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println("MsgFragment ----onCreate");
//        System.out.println("jid: "+list.get(0));
//                            "18380465202@laptop-c7q32nn2"
        getFriends();

        initView();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("MsgFragment --- onCreateView");
        initData();
        View view = inflater.inflate(R.layout.msg_fragment, container, false);
        //检查网络状况
//        checkNet(view);
        mInternet = (TextView) view.findViewById(R.id.internet_tip);
        if (flag) {
            mInternet.setVisibility(View.VISIBLE);
        } else {
            mInternet.setVisibility(View.GONE);
        }

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case DISCONNECTION:
                        mInternet.setVisibility(View.VISIBLE);
                        flag = true;
                        break;
                    case CONNECTION:
                        mInternet.setVisibility(View.GONE);
                        flag = false;
                        break;
                }
            }
        };


        //初始化下拉刷新组件
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        //下拉新增条目
                        mMsgRecyclerAdapter.addItem();
                    }
                }, 1000);
            }
        });
        //初始化RecyclerView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        //初始化适配器
        mMsgRecyclerAdapter = new MsgRecyclerAdapter(mCotext, mDatas);
        //设置点击监听事件
        mMsgRecyclerAdapter.setOnItemClickListener(new MsgRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Snackbar.make(getView(), position + " is onClicked", Snackbar.LENGTH_SHORT).show();
                Intent intent = new Intent(mCotext, ChatActivity.class);
                Bundle bundle = new Bundle();
                //18380465202@laptop-c7q32nn2
                bundle.putString("Jid", mDatas.get(position).getTel() + "@laptop-c7q32nn2");
                intent.putExtras(bundle);
                startActivity(intent);

//                //跳转
//                Intent intent = new Intent(mCotext,
//                        MessageActivity.class);
//                startActivity(intent);

//                Toast.makeText(getActivity(),position + " is onClicked",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Snackbar.make(getView(), position + " is onLongClicked", Snackbar.LENGTH_SHORT).show();


            }
        });
        //为RecyclerView绑定适配器
        mRecyclerView.setAdapter(mMsgRecyclerAdapter);
        //为RecyclerView设置线性布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //设置Item新增、移除动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        initDb();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("onResume..");
        initDb();
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("onPause()---");
    }

    private void initData() {
        xmppConn = XmppConn.getInstance();
        connection = xmppConn.getXmpptcpConnection();
        SharedPreferences sp = getActivity().getSharedPreferences(ConstantVar.USER_TEL, getActivity().MODE_PRIVATE);
        final String tel = sp.getString("tel", "null");
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("tel:----------" + tel);
                if (xmppConn.login(tel, tel) == 1) {
//                    getFriends();

                    Roster roster = Roster.getInstanceFor(connection);
//                    //订阅好友的Presence
//                    Stanza stanza = new Presence(Presence.Type.subscribe);
//                    try {
//                        stanza.setTo(JidCreate.from("18380465202@laptop-c7q32nn2"));
//                    } catch (XmppStringprepException e) {
//                        e.printStackTrace();
//                    }
////                            String user = "";
////                            stanza.setTo();
////                            //presence.setMode(Presence.Mode.available);
//                    try {
//                        connection.sendStanza(stanza);
//                    } catch (SmackException.NotConnectedException e) {
//                        e.printStackTrace();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                    roster.addRosterListener(new RosterListener() {
                        @Override
                        public void entriesAdded(Collection<Jid> addresses) {
                            System.out.println("add addresses: " + addresses);
                            ArrayList<Jid> list = (ArrayList<Jid>) addresses;
                            System.out.println("jid: " + list.get(0));
//                            "18380465202@laptop-c7q32nn2"
                            //订阅好友的Presence
                            Stanza stanza = new Presence(Presence.Type.subscribe);
                            stanza.setTo(list.get(0));
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
                        }

                        @Override
                        public void entriesUpdated(Collection<Jid> addresses) {

                        }

                        @Override
                        public void entriesDeleted(Collection<Jid> addresses) {

                        }

                        @Override
                        public void presenceChanged(Presence presence) {

                        }
                    });
                    entries = roster.getEntries();
                    datas.addAll(entries);
//                    ArrayList<RosterEntry> lists = new ArrayList<>();
//                    lists.addAll(entries);
//                    List<String> list = new ArrayList<>();
//                    for (RosterEntry entry : lists) {
//                        String[] tel = entry.getJid().toString().split("@");
//                        list.add(tel[0]);
//                        System.out.println("entry:" + tel[0]);
//                    }

//                  datas.addAll(entries);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("entries:" + entries);
//                            mMsgRecyclerAdapter.add(mDatas);
                        }
                    });
                }
                getActivity().startService(new Intent(mCotext, ChatService.class));
            }
        }).start();
//        mDatas = new ArrayList<>();
//        for (int i = 'A'; i < 'z'; i++) {
//            mDatas.add("" + (char) i);
//        }
    }

    public void getFriends() {
        //验证登录
        String url = IP.IP_PARENT + "/getFriends";
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(ConstantVar.USER_TEL, getActivity().MODE_PRIVATE);
        String tel = sharedPreferences.getString("tel", "null");
        HashMap<String, String> paramsMap = new HashMap<>();
        paramsMap.put("tel", tel);
        OkHttpUtils okHttpUtils = OkHttpUtils.getInstance(getActivity().getApplicationContext());


        okHttpUtils.postAsyn(url, paramsMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        progressDialog.dismiss();
//                        //AlertDialog 对话
//                        Utils.alertDialog(LoginActivity.this, "请检查网络连接！");
//                    }
//                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res = response.body().string();
                Gson gson = new Gson();
                UserBase<UserInfo> user = gson.fromJson(res, new TypeToken<UserBase<UserInfo>>() {
                }.getType());
                final List<UserInfo> userInfos = user.getMessage();
                mDatas.addAll(userInfos);
                for (UserInfo userInfo : userInfos) {
                    System.out.println("userInfo：" + userInfo.getName() + "--" + userInfo.getIcon());
                }
//                final Base base = gson.fromJson(res, Base.class);
//                progressDialog.dismiss();
//                System.out.println("Login返回内容：" + res);
//                if (user.getCode().equals("10000")) {
//
//                } else {
////                    runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            //AlertDialog 对话
////                            Utils.alertDialog(LoginActivity.this, base.getMessage());
////                        }
////                    });
//                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //停止服务
        getActivity().stopService(new Intent(mCotext, NetStateService.class));
        connection.disconnect();
        System.out.println("onDestroy()---");
    }

    private void initView() {
        mCotext = getContext();
        //开启服务
        getActivity().startService(new Intent(mCotext, NetStateService.class));

    }

    //检查网络
    private void checkNet(View view) {
        if (!Utils.InternetConn((ConnectivityManager) mCotext.getSystemService(mCotext.CONNECTIVITY_SERVICE))) {
            TextView tip = (TextView) view.findViewById(R.id.internet_tip);
            tip.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 初始化数据库
     */
    private void initDb() {
        db = getActivity().openOrCreateDatabase("newone.db", Context.MODE_PRIVATE, null);
        String sql = "create table if not exists chat(id integer primary key autoincrement,who varchar, message text, createtime int)";
        db.execSQL(sql);
        Cursor cur = db.rawQuery("SELECT * FROM chat ", null);
        if (cur != null) {
            //游标至于最后一个位置
            cur.moveToLast();
            HashMap<String, String> hashMap = new HashMap<>();
            int urlColumn = cur.getColumnIndex("message");
            int contentColumn = cur.getColumnIndex("createtime");
            hashMap.put("message", cur.getString(urlColumn));
            hashMap.put("createtime", cur.getString(contentColumn));
            System.out.println("4324234324243message:" + cur.getString(urlColumn));
            System.out.println("4324234324243time:" + cur.getString(contentColumn));
            time.add(hashMap);
            mMsgRecyclerAdapter.addTime(time);
        }
        db.close();

    }

}
