package com.lukuqi.newone.util;

import android.util.Log;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaIdFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.packet.Registration;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.util.Map;

/**
 * Smack Xmpp连接工具类
 * <p/>
 * Created by mr.right on 2016/4/18.
 */
public class XmppConn {
    //    private String HOST = "114.215.144.131";
    private String HOST = "100.72.10.152";
    //    private String SERVICE_NAME = "114.215.144.131";
    private String SERVICE_NAME = "100.72.10.152";

    private Integer PORT = 5222;

    public static XMPPTCPConnection xmpptcpConnection;
    public static XmppConn instance;

    static final int CONNECTED = 1;
//    static final int  connected = 0;
//    static final int  connected = 0;
//    static final int  connected = 0;

    public XmppConn() {
        Jid jid = null;
        try {
            jid = JidCreate.from(SERVICE_NAME);
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }
        XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
//        builder.setServiceName(SERVICE_NAME);
        builder.setXmppDomain((DomainBareJid) jid);
        builder.setHost(HOST);
        builder.setPort(PORT);
        builder.setCompressionEnabled(false);
        builder.setDebuggerEnabled(false);
        builder.setSendPresence(true);
        builder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        xmpptcpConnection = new XMPPTCPConnection(builder.build());


    }

    public static XmppConn getInstance() {
        if (instance == null) {
            instance = new XmppConn();
        }
        return instance;
    }

    /**
     * 用户登录
     *
     * @param name     用户名
     * @param password 用户密码
     */
    public int login(String name, String password) {

        try {

            xmpptcpConnection.connect();


            if (!xmpptcpConnection.isAuthenticated()) {
                xmpptcpConnection.login(name, password);
            }
            Presence presence = new Presence(Presence.Type.available);
            xmpptcpConnection.sendStanza(presence);

            xmpptcpConnection.addConnectionListener(new ConnectionListener() {
                @Override
                public void connected(XMPPConnection xmppConnection) {

                    Log.e("Tag", "connected: 连接成功!");
                }

                @Override
                public void authenticated(XMPPConnection xmppConnection, boolean b) {
                    Log.e("Tag", "connected: 连接验证!");
                }

                @Override
                public void connectionClosed() {
                    Log.e("Tag", "connected: 连接关闭!");
                }

                @Override
                public void connectionClosedOnError(Exception e) {
                    Log.e("Tag", "connected: 连接关闭失败!");
                }

                @Override
                public void reconnectionSuccessful() {
                    Log.e("Tag", "connected: 重新连接成功!");
                }

                @Override
                public void reconnectingIn(int i) {
                    Log.e("Tag", "connected: 重新连接!");
                }

                @Override
                public void reconnectionFailed(Exception e) {
                    Log.e("Tag", "connected: 重新连接失败!");
                }
            });
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return CONNECTED;
    }

    public XMPPTCPConnection getXmpptcpConnection() {
        return xmpptcpConnection;
    }

    public void disconnect() {
        xmpptcpConnection.disconnect();
    }


    /**
     * 注册用户
     *
     * @param attributes 用户属性，如：姓名，密码，邮箱…
     */
    public void createAccount(Map<String, String> attributes) {
            try {
                xmpptcpConnection.connect();
                xmpptcpConnection.addConnectionListener(new ConnectionListener() {
                    @Override
                    public void connected(XMPPConnection xmppConnection) {

                        Log.e("Tag", "connected: 连接成功!");
                    }

                    @Override
                    public void authenticated(XMPPConnection xmppConnection, boolean b) {
                        Log.e("Tag", "connected: 连接验证!");
                    }

                    @Override
                    public void connectionClosed() {
                        Log.e("Tag", "connected: 连接关闭!");
                    }

                    @Override
                    public void connectionClosedOnError(Exception e) {
                        Log.e("Tag", "connected: 连接关闭失败!");
                    }

                    @Override
                    public void reconnectionSuccessful() {
                        Log.e("Tag", "connected: 重新连接成功!");
                    }

                    @Override
                    public void reconnectingIn(int i) {
                        Log.e("Tag", "connected: 重新连接!");
                    }

                    @Override
                    public void reconnectionFailed(Exception e) {
                        Log.e("Tag", "connected: 重新连接失败!");
                    }
                });
            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XMPPException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


//        Map<String, String> attributes = new HashMap<>();
//        attributes.put("username", "didi");
//        attributes.put("password", "123456");
        Registration reg = new Registration(attributes);
        reg.setType(IQ.Type.set);
        reg.setTo(xmpptcpConnection.getServiceName());

        StanzaFilter filter = new AndFilter(new StanzaIdFilter(reg.getStanzaId()));
        PacketCollector col = xmpptcpConnection.createPacketCollector(filter);
        try {
            xmpptcpConnection.sendStanza(reg);
            IQ result = (IQ) col.nextResult(SmackConfiguration.getDefaultPacketReplyTimeout());

        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        col.cancel();

    }
}
