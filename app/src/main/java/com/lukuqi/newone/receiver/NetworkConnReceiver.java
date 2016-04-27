package com.lukuqi.newone.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

/**
 * Created by right on 2015/12/31.
 */
public class NetworkConnReceiver extends BroadcastReceiver {
    ConnectivityManager connManager;
    @Override
    public void onReceive(Context context, Intent intent) {
//        System.out.println("Receiver: --onReceiver()--");
        String action = intent.getAction();
        //System.out.println("Action: " +action);
        // Action: android.net.conn.CONNECTIVITY_CHANGE
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

            System.out.print("网络状态已经改变！");
        }
    }
}
