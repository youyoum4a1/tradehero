package com.tradehero.th.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.Window;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.*;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.api.share.wechat.WeChatTrackShareFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.graphics.ForSecurityItemForeground;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.WeChatServiceWrapper;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;

import java.io.IOException;
import javax.inject.Inject;

import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler //created by alex
{

    public static final String APP_ID = "";
    public static final int WECHAT_MESSAGE_TYPE_NONE = -1;
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
    private static final String WECHAT_DTO_INTENT_KEY = WXEntryActivity.class.getName() + ".weChatDTOKey";
    private static final String WECHAT_SHARE_NEWS_KEY = "news:";
    private static final String WECHAT_SHARE_TYPE_VALUE = "WeChat";

    private WeChatDTO weChatDTO;
    private Bitmap mBitmap;
    private MiddleCallback<Response> trackShareMiddleCallback;

    @Inject
    CurrentUserId currentUserId;
    @Inject
    IWXAPI mWeChatApi;
    @Inject
    WeChatServiceWrapper weChatServiceWrapper;
    @Inject
    Lazy<Picasso> picassoLazy;
    @Inject
    @ForSecurityItemForeground
    protected Transformation foregroundTransformation;

    private static String WECHAT_CODE;

    public static void putWeChatDTO(@NotNull Intent intent, @NotNull WeChatDTO weChatDTO) {
        intent.putExtra(WECHAT_DTO_INTENT_KEY, weChatDTO.getArgs());
    }

    public static WeChatDTO getWeChatDTO(@NotNull Intent intent) {
        Bundle args = intent.getBundleExtra(WECHAT_DTO_INTENT_KEY);
        if (args == null) {
            return null;
        }
        return new WeChatDTO(args);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
        // TODO take this intent extraction into a separate method and use a new
        // WeChatDTO method to read from Intent.
        requestWindowFeature(Window.FEATURE_NO_TITLE);


        weChatDTO = getWeChatDTO(getIntent());
        if (weChatDTO == null) {
            finish();
        }
        //loadImage();

        boolean isWXInstalled = mWeChatApi.isWXAppInstalled();
        if(weChatDTO == null){
            return;
        }
        if (isWXInstalled && weChatDTO.type != null) {
            if (mWeChatApi.getWXAppSupportAPI()
                    >= TIMELINE_SUPPORTED_VERSION) //wechat 4.2 support timeline
            {
                if (weChatDTO.type == WeChatMessageType.Auth) {
                    weChatAuth();
                } else {
                    WXMediaMessage weChatMessage = buildMessage(weChatDTO.type);
                    SendMessageToWX.Req weChatReq = buildRequest(weChatMessage);
                    mWeChatApi.sendReq(weChatReq);
                }
            } else {
                THToast.show(getString(R.string.need_update_wechat));
                finish();
            }
        } else {
            THToast.show(getString(R.string.need_install_wechat));
            finish();
        }
    }

    private void weChatAuth() {
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        mWeChatApi.sendReq(req);
    }

    private WXMediaMessage buildMessage(WeChatMessageType weChatMessageType) {
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.share_wechat_logo);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(thumb, 150, 150, true);
        String title = getResources().getString(R.string.app_name_cn);
        String content = "";
        String url = "";
        String totalShare = "";
        if (weChatDTO != null && weChatDTO.title != null) {
            totalShare = weChatDTO.title;
            String[] contents = parseContent(totalShare);
            content = contents[0];
            url = contents[1];
        }

        if (weChatMessageType == WeChatMessageType.ShareSell || weChatMessageType == WeChatMessageType.ShareSellToTimeline) {
            if(TextUtils.isEmpty(url)){
                WXTextObject textObject = new WXTextObject();
                textObject.text = totalShare;
                WXMediaMessage msg = new WXMediaMessage();
                msg.title = title;
                msg.mediaObject = textObject;
                msg.description = textObject.text;
                return msg;
            }else{
                WXWebpageObject sellWebPage = new WXWebpageObject();
                sellWebPage.webpageUrl = url;
                WXMediaMessage msg = new WXMediaMessage(sellWebPage);
                msg.title = title;
                msg.description = content;
                msg.setThumbImage(thumbBmp);
                return msg;
            }
        } else {
            WXWebpageObject webPage = new WXWebpageObject();
            webPage.webpageUrl = Constants.WECHAT_SHARE_URL;

            WXMediaMessage weChatMsg = new WXMediaMessage(webPage);
            if(TextUtils.isEmpty(totalShare)){
                weChatMsg.description = getString(weChatMessageType.getTitleResId());
                weChatMsg.title = title;
            }else{
                weChatMsg.title = title;
                weChatMsg.description = totalShare;
            }
            weChatMsg.setThumbImage(thumbBmp);
            return weChatMsg;
        }
    }

    private void loadImage() {
        final WeChatDTO weChatDTOCopy = weChatDTO;
        if (weChatDTOCopy != null && weChatDTOCopy.imageURL != null && !weChatDTOCopy.imageURL.isEmpty()) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Bitmap picassoBmp = picassoLazy.get().load(weChatDTOCopy.imageURL).get();
                        if (picassoBmp != null) {
                            Bitmap tempBitmap = Bitmap.createBitmap(picassoBmp);
                            // TODO find a way to force picasso to redownload and not have a recycled image.
                            if (tempBitmap != null && !tempBitmap.isRecycled()) {
                                mBitmap = Bitmap.createScaledBitmap(tempBitmap, 250, 250, false);
                            }
                        }
                    } catch (IOException e) {
                        THToast.show(e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }

    private SendMessageToWX.Req buildRequest(WXMediaMessage weChatMsg) {
        SendMessageToWX.Req weChatReq = new SendMessageToWX.Req();
        weChatReq.transaction = String.valueOf(System.currentTimeMillis());
        //not sure for transaction, maybe identify id?
        if (weChatDTO.type == WeChatMessageType.Invite || weChatDTO.type == WeChatMessageType.ShareSell) {
            weChatReq.scene = SendMessageToWX.Req.WXSceneSession;
        } else {
            weChatReq.scene = SendMessageToWX.Req.WXSceneTimeline;
        }

        weChatReq.message = weChatMsg;
        return weChatReq;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mWeChatApi.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        switch (req.getType()) {
            case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
                break;
            case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
                break;
            default:
                break;
        }
    }

    @Override
    public void onResp(BaseResp resp) {
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                if (resp instanceof SendAuth.Resp) {
                    WECHAT_CODE = ((SendAuth.Resp) resp).code;
                    finish();
                } else {
                    THToast.show(getString(R.string.share_success));
                    reportWeChatSuccessShareToServer();
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            default:
                finish();
                break;
        }
    }


    private void reportWeChatSuccessShareToServer() {
        WeChatTrackShareFormDTO weChatTrackShareFormDTO = new WeChatTrackShareFormDTO();
        weChatTrackShareFormDTO.msg = WECHAT_SHARE_NEWS_KEY + weChatDTO.id;
        weChatTrackShareFormDTO.type = WECHAT_SHARE_TYPE_VALUE;

        detachTrackShareMiddleCallback();
        trackShareMiddleCallback =
                weChatServiceWrapper.trackShare(currentUserId.get(), weChatTrackShareFormDTO,
                        new TrackShareCallback());
    }

    private void detachTrackShareMiddleCallback() {
        if (trackShareMiddleCallback != null) {
            trackShareMiddleCallback.setPrimaryCallback(null);
        }
        trackShareMiddleCallback = null;
    }

    @Override
    protected void onDestroy() {
        detachTrackShareMiddleCallback();
        super.onDestroy();
    }


    private class TrackShareCallback implements Callback<Response> {
        @Override
        public void success(Response response, Response response2) {
            // do nothing for now
            finish();
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            THToast.show(new THException(retrofitError));
            finish();
        }
    }

    /*
        Fixed a bug by WeChat SDK
        https://www.pivotaltracker.com/story/show/75789704
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return super.onTouchEvent(event);
    }

    public static String getWeChatCode() {
        return WECHAT_CODE;
    }

    public static void setWeChatCodeNull() {
        WECHAT_CODE = null;
    }

    public static String[] parseContent(String content) {
        String[] result = new String[2];
        int index = content.lastIndexOf("http");
        if (index <= 0) {
            result[0] = content;
            result[1] = "";
            return result;
        }
        result[0] = content.substring(0, index);
        if (result[0] == null) {
            result[0] = "";
        }
        result[1] = content.substring(index);
        if (result[1] == null) {
            result[1] = "";
        }
        return result;
    }

}