package com.tradehero.chinabuild.notification.getui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.utils.Constants;


/**
 * Created by palmer on 15/4/17.
 */
public class GETUIPushReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        THLog.d("GetuiSdk onReceive() action=" + bundle.getInt("action"));
        switch (bundle.getInt(PushConsts.CMD_ACTION)) {

            case PushConsts.GET_MSG_DATA:
                THLog.d("ccc");
                // 获取透传数据
                // String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");

                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");

                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, Constants.NOTIFICATION_ID);

                if (payload != null) {
                    String data = new String(payload);

                    THLog.d("GetuiSdk Got Payload:" + data);
                }
                break;
            case PushConsts.GET_CLIENTID:
                // 获取ClientID(CID)
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String cid = bundle.getString("clientid");

                THLog.d("aaa "+cid);
                break;
            case PushConsts.THIRDPART_FEEDBACK:
                THLog.d("bbb");
                String appid = bundle.getString("appid");
                String taskidA = bundle.getString("taskid");
                String actionid = bundle.getString("actionid");
                String resultA = bundle.getString("result");
                long timestamp = bundle.getLong("timestamp");

                THLog.d("appid = " + appid);
                THLog.d("taskid = " + taskidA);
                THLog.d("actionid = " + actionid);
                THLog.d("result = " + resultA);
                THLog.d("timestamp = " + timestamp);
                break;
            default:
                break;
        }
    }
}