package com.lukuqi.newone.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.lukuqi.newone.fragment.MsgFragment;


/**
 * 网络状态监听服务
 * <p>
 * Created by right on 2015/12/31.
 */
public class NetStateService extends Service {
    private BroadcastReceiver mReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("Service: --onCreate()--");

        mReceiver = new NetworkConnReceiver();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mReceiver, intentFilter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    class NetworkConnReceiver extends BroadcastReceiver {
        private ConnectivityManager connManager;
        private NetworkInfo networkInfo;

        @Override
        public void onReceive(Context context, Intent intent) {
            //System.out.println("Receiver: --onReceiver()--");
            String action = intent.getAction();
            //System.out.println("Action: " +action);
            // Action: android.net.conn.CONNECTIVITY_CHANGE
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                //System.out.println("网络状态已经改变！");
                connManager = (ConnectivityManager) getSystemService(context.CONNECTIVITY_SERVICE);
                networkInfo = connManager.getActiveNetworkInfo();
                //Handler message
                Message message = new Message();

                if (networkInfo != null && networkInfo.isAvailable()) {
                    message.what = MsgFragment.CONNECTION;
//                    System.out.println("当前wifi名称：" + networkInfo.getTypeName());
                } else {
                    System.out.println("无网络" + networkInfo);
                    message.what = MsgFragment.DISCONNECTION;
                }
                MsgFragment.mHandler.sendMessage(message);
            }
        }
    }

}
