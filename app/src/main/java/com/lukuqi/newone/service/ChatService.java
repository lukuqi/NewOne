package com.lukuqi.newone.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;


import com.lukuqi.newone.util.XmppConn;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.IOException;

/**
 * Created by mr.right on 2016/5/13.
 */
public class ChatService extends Service {

    private XMPPTCPConnection con;
    ChatManager chatManager;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        con = XmppConn.getInstance().getXmpptcpConnection();
//        System.out.println("01---connected:" + con.isConnected());
        System.out.println("Chat Service---create");


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("Chat Service---start");
        try {
            if (!con.isConnected()) {
                con.connect();
            }

        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("con--------" + con + "isConnected" + con.isConnected());

        if (null != con) {
            chatManager = ChatManager.getInstanceFor(con);
            chatManager.addChatListener(new ChatManagerListener() {
                @Override
                public void chatCreated(Chat chat, boolean createdLocally) {
                    System.out.println("chat:" + chat + "Locally:" + createdLocally);
                    chat.addMessageListener(new ChatMessageListener() {
                        @Override
                        public void processMessage(Chat chat, Message message) {
                            if (null != message.getBody()) {
                                System.out.println("Chat-------" + chat);
                                System.out.println("Message-------" + message);
                                System.out.println("Message-------" + message.getBody());
                            }
                        }
                    });
                }
            });
        }
//        if (null != con) {
//            System.out.println("Chat Service---goin MessageTypeFilter.CHAT");
////            ChatManager chatManager = ChatManager.getInstanceFor(con);
////            chatManager.addChatListener(new MyChatManagerListener());
//            StanzaFilter filter = new AndFilter(MessageTypeFilter.CHAT);
////            PacketCollector myCollector = con.createPacketCollector(filter);
//            StanzaListener listener = new StanzaListener() {
//                @Override
//                public void processPacket(Stanza packet) throws SmackException.NotConnectedException, InterruptedException {
//                    Log.i("Presence", "PresenceService------" + packet.toXML());
//                    System.out.println("Presence:" + packet.toXML());
//
//                    // 看API可知道 Presence是Packet的子类
//                    if (packet instanceof Presence) {
//                        Presence presence = (Presence) packet;
//                        // Presence还有很多方法，可查看API
////                        String from = presence.getFrom();// 发送方
////                        String to = presence.getTo();// 接收方
//                        // Presence.Type有7中状态
//                        if (presence.getType().equals(Presence.Type.subscribe)) {// 好友申请
//
//                        } else if (presence.getType().equals(
//                                Presence.Type.subscribed)) {// 同意添加好友
//
//                        } else if (presence.getType().equals(
//                                Presence.Type.unsubscribe)) {// 拒绝添加好友 和 删除好友
//
//                        } else if (presence.getType().equals(
//                                Presence.Type.unsubscribed)) {// 这个我没用到
//                        } else if (presence.getType().equals(
//                                Presence.Type.unavailable)) {// 好友下线
//                            // 要更新好友列表，可以在这收到包后，发广播到指定页面
//                            // 更新列表
//
//                        } else if (presence.getType().equals(
//                                Presence.Type.available)) {// 好友上线
//
//                        }
//                    } else if (packet instanceof Message) {
//                        System.out.println("Packet:----");
//                        Message msg = (Message) packet;
////                        Toast.makeText(getApplicationContext(),
////                                msg.getFrom() + " 说：" + msg.getBody(),
////                                Toast.LENGTH_SHORT).show();
//                    }
//                }
//            };
//            System.out.println("listener: "+listener);
//            con.addAsyncStanzaListener(listener, filter);
//        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * message listener
     */
//    class MyChatManagerListener implements ChatManagerListener {
//
//
//        @Override
//        public void chatCreated(Chat chat, boolean createdLocally) {
//            System.out.println("chat" + chat);
//
////            Toast.makeText(getApplicationContext(), chat.toString(), Toast.LENGTH_SHORT).show();
//
//
//        }
//    }

}
