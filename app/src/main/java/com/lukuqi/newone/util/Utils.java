package com.lukuqi.newone.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

/**
 * 工具类
 * <p/>
 * Created by right on 2015/12/30.
 */
public class Utils {


    /**
     * 获取网络状态
     *
     * @param cm ConnectivityManager
     * @return
     */
    public static boolean InternetConn(ConnectivityManager cm) {
        boolean connFlag = false;
        ConnectivityManager conn = cm;
        NetworkInfo network = conn.getActiveNetworkInfo();
        if (network != null) {
            connFlag = conn.getActiveNetworkInfo().isAvailable();
        }
        return connFlag;
    }

//    public static void progressDialog(Context context, CharSequence title, CharSequence message, Boolean indeterminate, Boolean cancelable) {
//        ProgressDialog progressDialog = ProgressDialog.show(context, title, message, indeterminate, cancelable);
//    }

    /**
     * 显示 AlertDialog
     *
     * @param context 上下文
     * @param Message 显示的消息
     */
    public static void alertDialog(Context context, CharSequence Message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //    设置Title的图标
//             builder.setIcon(R.mipmap.ic_launcher);
        //    设置Title的内容
//                 builder.setTitle(base.getMessage());
        //    设置Content来显示一个信息
        builder.setMessage(Message);
        builder.setNegativeButton("确定", null);
//                                        //    设置一个PositiveButton
//                                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                Toast.makeText(LoginActivity.this, "positive: " + which, Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                                        //    设置一个NegativeButton
//                                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                Toast.makeText(LoginActivity.this, "negative: " + which, Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                                        //    设置一个NeutralButton
//                                        builder.setNeutralButton("忽略", new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                Toast.makeText(LoginActivity.this, "neutral: " + which, Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
        builder.show();
    }


    /**
     * 验证手机格式
     *
     * @param mobiles 手机号码
     * @return boolean值
     */
    public static boolean isMobileNO(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
//        if (TextUtils.isEmpty(mobiles)) return false;
        return mobiles.matches(telRegex);
    }
}
