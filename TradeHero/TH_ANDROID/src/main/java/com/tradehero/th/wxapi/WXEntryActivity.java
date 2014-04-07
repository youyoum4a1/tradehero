package com.tradehero.th.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.platformtools.Util;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.wechat.TrackShareFormDTO;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.WeChatServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler //created by alex
{
    public static final int WECHAT_MESSAGE_TYPE_NONE = -1;
    public static final int WECHAT_MESSAGE_TYPE_TRADE = 0;
    public static final int WECHAT_MESSAGE_TYPE_CREATE_DISCUSSION = 1;
    public static final int WECHAT_MESSAGE_TYPE_DISCUSSION = 2;
    public static final int WECHAT_MESSAGE_TYPE_NEWS = 3;
    public static final int WECHAT_MESSAGE_TYPE_TIMELINE = 4;
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
    public static final String WECHAT_MESSAGE_ID_KEY = "wechat_message_id_key";
    public static final String WECHAT_MESSAGE_TYPE_KEY = "wechat_message_type_key";
    private static final String WECHAT_SHARE_NEWS_KEY = "news:";
    private static final String WECHAT_SHARE_TYPE_VALUE = "WeChat";

    private int mMsgId;
    private int mMsgType;
    private MiddleCallback<Response> trackShareMiddleCallback;
    @Inject CurrentUserId currentUserId;
    @Inject IWXAPI mWeChatApi;
    @Inject WeChatServiceWrapper weChatServiceWrapper;
    @Inject WXMediaMessage mWeChatMsg;
    @Inject SendMessageToWX.Req mWeChatReq;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mMsgType = getIntent().getIntExtra(WECHAT_MESSAGE_TYPE_KEY, WECHAT_MESSAGE_TYPE_NONE);
        mMsgId = getIntent().getIntExtra(WECHAT_MESSAGE_ID_KEY, 0);

        boolean isWXInstalled = mWeChatApi.isWXAppInstalled();
        if (isWXInstalled)
        {
            if (mWeChatApi.getWXAppSupportAPI()
                    >= TIMELINE_SUPPORTED_VERSION) //wechat 4.2 support timeline
            {
                mWeChatMsg.title = getString(WXMessageType.News.getTitleResId());
                mWeChatMsg.description = mWeChatMsg.title;
                Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.notification_logo);
                mWeChatMsg.thumbData = Util.bmpToByteArray(thumb, true);
                mWeChatReq.message = mWeChatMsg;
                mWeChatApi.sendReq(mWeChatReq);
            }
            else
            {
                THToast.show(getString(R.string.need_update_wechat));
                finish();
            }
        }
        else
        {
            THToast.show(getString(R.string.need_install_wechat));
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        mWeChatApi.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req)
    {
        switch (req.getType())
        {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                break;
            default:
                break;
        }
    }

    @Override
    public void onResp(BaseResp resp)
    {
        //Timber.d("WX onResp %d %d", resp.errCode, mMsgType);
        //THToast.show("WX onResp=" + resp.errCode + " mMsgType=" + mMsgType);
        int result = 0;
        switch (resp.errCode)
        {
            case BaseResp.ErrCode.ERR_OK:
                THToast.show(getString(R.string.share_success));
                reportWeChatSuccessShareToServer();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                finish();
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                finish();
                break;
            default:
                finish();
                break;
        }
    }

    private void reportWeChatSuccessShareToServer()
    {
        TrackShareFormDTO trackShareFormDTO = new TrackShareFormDTO();
        trackShareFormDTO.msg = WECHAT_SHARE_NEWS_KEY + mMsgId;
        trackShareFormDTO.type = WECHAT_SHARE_TYPE_VALUE;

        detachTrackShareMiddleCallback();
        trackShareMiddleCallback =
                weChatServiceWrapper.trackShare(currentUserId.get(), trackShareFormDTO,
                        new TrackShareCallback());
    }

    private void detachTrackShareMiddleCallback()
    {
        if (trackShareMiddleCallback != null)
        {
            trackShareMiddleCallback.setPrimaryCallback(null);
        }
        trackShareMiddleCallback = null;
    }

    @Override protected void onDestroy()
    {
        detachTrackShareMiddleCallback();
        super.onDestroy();
    }

    private class TrackShareCallback implements Callback<Response>
    {
        @Override public void success(Response response, Response response2)
        {
            // do nothing for now
            Timber.d("lyl success");
            finish();
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            Timber.d("lyl failure");
            THToast.show(new THException(retrofitError));
            finish();
        }
    }
}