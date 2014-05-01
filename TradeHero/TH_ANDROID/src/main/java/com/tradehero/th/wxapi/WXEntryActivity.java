package com.tradehero.th.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.wechat.TrackShareFormDTO;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.graphics.ForSecurityItemForeground;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.WeChatServiceWrapper;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.io.IOException;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler //created by alex
{
    public static final int WECHAT_MESSAGE_TYPE_NONE = -1;
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
    public static final String WECHAT_MESSAGE_ID_KEY = "wechat_message_id_key";
    public static final String WECHAT_MESSAGE_TYPE_KEY = "wechat_message_type_key";
    public static final String WECHAT_MESSAGE_IMAGE_URL_KEY = "wechat_message_image_url_key";
    public static final String WECHAT_MESSAGE_TITLE_KEY = "wechat_message_title_key";
    private static final String WECHAT_SHARE_NEWS_KEY = "news:";
    private static final String WECHAT_SHARE_TYPE_VALUE = "WeChat";

    private int mMsgNewsId;
    private String mTitle;
    private String mMsgImageURL;
    private Bitmap mBitmap;
    private MiddleCallback<Response> trackShareMiddleCallback;

    @Inject CurrentUserId currentUserId;
    @Inject IWXAPI mWeChatApi;
    @Inject WeChatServiceWrapper weChatServiceWrapper;
    @Inject Lazy<Picasso> picassoLazy;
    @Inject @ForSecurityItemForeground protected Transformation foregroundTransformation;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        int msgType = getIntent().getIntExtra(WECHAT_MESSAGE_TYPE_KEY, WECHAT_MESSAGE_TYPE_NONE);
        mMsgNewsId = getIntent().getIntExtra(WECHAT_MESSAGE_ID_KEY, 0);
        mTitle = getIntent().getStringExtra(WECHAT_MESSAGE_TITLE_KEY);
        mMsgImageURL = getIntent().getStringExtra(WECHAT_MESSAGE_IMAGE_URL_KEY);
        loadImage();

        WeChatMessageType weChatMessageType = WeChatMessageType.fromType(msgType);

        boolean isWXInstalled = mWeChatApi.isWXAppInstalled();
        if (isWXInstalled && weChatMessageType != null)
        {
            if (mWeChatApi.getWXAppSupportAPI()
                    >= TIMELINE_SUPPORTED_VERSION) //wechat 4.2 support timeline
            {
                WXMediaMessage weChatMessage = buildMessage(weChatMessageType);
                SendMessageToWX.Req weChatReq = buildRequest(weChatMessage);
                mWeChatApi.sendReq(weChatReq);
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

    private WXMediaMessage buildMessage(WeChatMessageType weChatMessageType)
    {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = Constants.WECHAT_SHARE_URL;

        WXMediaMessage weChatMsg = new WXMediaMessage(webpage);
        weChatMsg.description = getString(weChatMessageType.getTitleResId());
        if (mTitle != null && !mTitle.isEmpty())
        {
            weChatMsg.title = mTitle;
        }
        else
        {
            weChatMsg.title = weChatMsg.description;
        }
        int i = 0;
        while (mMsgImageURL != null && mBitmap == null && i < 200)
        {
            i++;
        }
        initBitmap();
        weChatMsg.thumbData = Util.bmpToByteArray(mBitmap, true);
        mBitmap.recycle();

        return weChatMsg;
    }

    private void loadImage()
    {
        if (mMsgImageURL != null && !mMsgImageURL.isEmpty())
        {
            Thread thread = new Thread(new Runnable()
            {
                @Override public void run()
                {
                    try
                    {
                        mBitmap = Bitmap.createBitmap(picassoLazy.get().load(mMsgImageURL).get());
                        if (mBitmap != null)
                        {
                            mBitmap = Bitmap.createScaledBitmap(mBitmap, 50, 50, false);
                        }
                    } catch (IOException e)
                    {
                        THToast.show(e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }

    private void initBitmap()
    {
        if (mBitmap == null)
        {
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.notification_logo);
        }
    }

    private SendMessageToWX.Req buildRequest(WXMediaMessage weChatMsg)
    {
        SendMessageToWX.Req weChatReq = new SendMessageToWX.Req();
        weChatReq.transaction = String.valueOf(
                System.currentTimeMillis()); //not sure for transaction, maybe identify id?
        weChatReq.scene = SendMessageToWX.Req.WXSceneTimeline;
        weChatReq.message = weChatMsg;
        return weChatReq;
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        mWeChatApi.handleIntent(intent, this);
    }

    //<editor-fold desc="Wechat Callback">
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
        switch (resp.errCode)
        {
            case BaseResp.ErrCode.ERR_OK:
                THToast.show(getString(R.string.share_success));
                reportWeChatSuccessShareToServer();
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            default:
                finish();
                break;
        }
    }
    //</editor-fold>

    private void reportWeChatSuccessShareToServer()
    {
        TrackShareFormDTO trackShareFormDTO = new TrackShareFormDTO();
        trackShareFormDTO.msg = WECHAT_SHARE_NEWS_KEY + mMsgNewsId;
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
            finish();
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
            finish();
        }
    }
}