/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.alipay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;

import java.util.Map;

public class Pay {

    Activity activity;
    /**
     * 支付宝支付业务：入参app_id
     */
    public static final String APPID = "2018060760322879";

    /**
     * 支付宝账户登录授权业务：入参pid值
     */
    public static final String PID = "2088821702175780";
    /**
     * 支付宝账户登录授权业务：入参target_id值
     */
    public static final String TARGET_ID = "";

    public static final String RSA2_PRIVATE =
            "MIIEowIBAAKCAQEAzo715/QDlTsTRuA+pqdfh+TwZtY4WrIjdBTK17PxScp3eJdC\n" +
                    "j7TtGfZdeGXlVVm529SXc8nAu6WjT4aUiCy8JZek9sCSjWMl6zFSd+mnB3M/WFfZ\n" +
                    "i+swLEOo7oYdail5d9VaPcALu0HdcTE2wy9OnI8YZvCfGDYElXkuJ+aC6hsihfQ9\n" +
                    "HoGImp1+I+W7IDapVskPyMyJgezDWAf5cwKIF7bcC2BwqREVc94FVe4Bnv+e+CF6\n" +
                    "b7O+EzFtEYyazvoghnOr1cGs+PT7vkQwsegoAVLAeRnIPM+x28GhERVowTVaRSXJ\n" +
                    "FGhzWckVonOlI4bCs9LCod29K85ImdWlZVJwrQIDAQABAoIBAFNXtXqop0VwN5/y\n" +
                    "XPULNW10f1dO+iAGHm348btE3IeEjN34Uh//xWcjU6V/n5/neylqxs0uS3irJZCx\n" +
                    "4X5sZmHYpPP20WsA5UzXcsn1WbKU2qs/jwR4mdBSYHgVXwr1vxAN4sJlmZYtxjak\n" +
                    "v87TaKmcxyDJ17eCHClfYrMwvAPOw2RBApW1Hy8DacKzJPsvwD85qMHvqXhg+vR7\n" +
                    "d+AqMrSR2qgIdMzEouVu4kCuW4mkxOyKTR5EDpuvKd0Apy4ZKnuIX8rY8BTmWyWe\n" +
                    "L0cpnzpOOEU6ur2oGaDynSOIJR7yD+al2FTjdwZ570j9wMoO5GI3nRXeMa0chpKe\n" +
                    "3JcDXUECgYEA7H5TUyn+Fqb07afBDjV6HRUaB9krSOdcbUPJB0bqd8+vq5WrHcPc\n" +
                    "ugjOUpLXwfZ6UIm3OeAhIgmYTRXdsyMrJK9aL0aPsM/5Bb8X1od6r7yDoXsb+0e+\n" +
                    "j+76DacKAxxJ7AEXybc5zvKHzFbY/8hy114Spol1OneuLJR3AalCU10CgYEA35iK\n" +
                    "5oBt0/t5n1gwLggrgnWdVXyEL3ox+GizWWqHAHH2LV9h4hZ6RK+iqXZvV7dPhZUF\n" +
                    "gNQugdLEGqqjCzGg4FHKcjLFfQwot3dVRpFPipsN90GTbIJp+9ukOptvbTRQ2jvA\n" +
                    "K6h3FmPvTGxOi0u5/H+pDk39sU5TXWMUWovDjZECgYB86PAr0dcxsbdWOy+dyKMZ\n" +
                    "70ZTdOIG3KSV1aK9ZDh9VcXsc93wl73iyLnpJUQuX3gVR9fuCrKVXsoBlRYHAwFL\n" +
                    "nA6fP76vXfmijl8R4XbskT5ZMUQDQc6mCUwrn9mK8QseSyl8UnmHE9SkGg79cqst\n" +
                    "k7N4IMw5OHNXeocs6nBsxQKBgQCle8iRgEvIsD37aNF/wBwzmciwFE/gs5d4OkVC\n" +
                    "0EzKT43bVtSi9+/WFgdVVIpjaRA51WkVR/SDsPHiqpexGTHv05j5nAn92pnn7zH1\n" +
                    "4GX2N3vGm4n4rSyzcoPBvHhjYmzUl5RKu4wnzh61eRNIlOs5rWH5bE9ugoHAhCv0\n" +
                    "N6yrgQKBgFCzb8YXZ9QIJctrc6qmfRmpuKV7yuvcHmBOB/P0sNJK8gZctXDD6pIP\n" +
                    "3Jpmc2De5lY/5E9lgjAl9IrvroqozP+wplP4jP/yCfschsPp478vYelngUFrXYPU\n" +
                    "lgVDt04GNl2V6JPcpCyWF2mbPbMlLLpcdnlSI0P2zI52jT+/jaJr";
    public static final String RSA_PRIVATE = "";

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    public Pay(Activity activity) {
        this.activity = activity;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /*
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(activity, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(activity, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {
                    @SuppressWarnings("unchecked")
                    AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
                    String resultStatus = authResult.getResultStatus();

                    // 判断resultStatus 为“9000”且result_code
                    // 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
                    if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
                        // 获取alipay_open_id，调支付时作为参数external_token 的value 传入，则支付账户为该授权账户
                        Toast.makeText(activity,
                                "授权成功\n" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();
                    } else {
                        // 其他状态值则为授权失败
                        Toast.makeText(activity,
                                "授权失败" + String.format("authCode:%s", authResult.getAuthCode()), Toast.LENGTH_SHORT).show();

                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * 支付宝支付业务
     */
    public void payV2() {
        if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
            Toast.makeText(activity, "需要配置APPID | RSA_PRIVATE", Toast.LENGTH_LONG).show();
            return;
        }

        /*
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;

        Runnable payRunnable = () -> {
            PayTask alipay = new PayTask(activity);
            Map<String, String> result = alipay.payV2(orderInfo, true);
            Log.i("msp", result.toString());

            Message msg = new Message();
            msg.what = SDK_PAY_FLAG;
            msg.obj = result;
            mHandler.sendMessage(msg);
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

}
