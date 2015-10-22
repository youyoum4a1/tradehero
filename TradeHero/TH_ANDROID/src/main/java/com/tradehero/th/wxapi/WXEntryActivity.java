package com.tradehero.th.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.Window;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tradehero.chinabuild.data.sp.THSharePreferenceManager;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.api.share.wechat.WeChatTrackShareFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.network.service.WeChatServiceWrapper;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.NetworkUtils;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler //created by alex
{
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
    private static final String WECHAT_DTO_INTENT_KEY = WXEntryActivity.class.getName() + ".weChatDTOKey";
    private static final String WECHAT_SHARE_NEWS_KEY = "news:";
    private static final String WECHAT_SHARE_TYPE_VALUE = "WeChat";

    private WeChatDTO weChatDTO;
    private MiddleCallback<Response> trackShareMiddleCallback;

    @Inject Lazy<UserServiceWrapper> userServiceWrapper;

    @Inject CurrentUserId currentUserId;
    @Inject IWXAPI mWeChatApi;
    @Inject WeChatServiceWrapper weChatServiceWrapper;
    @Inject Analytics analytics;

    private static String WECHAT_CODE;

    private String traget_user_millionaire_page = "";

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

        String endPoint = THSharePreferenceManager.getShareEndPoint(this);
        traget_user_millionaire_page = getResources().getString(R.string.target_user_millionaire_page, endPoint);

        weChatDTO = getWeChatDTO(getIntent());
        if (weChatDTO == null) {
            finish();
        }

        boolean isWXInstalled = mWeChatApi.isWXAppInstalled();
        if (weChatDTO == null) {
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
        if (weChatMessageType == WeChatMessageType.Advertisement){
            WXWebpageObject sellWebPage = new WXWebpageObject();
            sellWebPage.webpageUrl = url;
            WXMediaMessage msg = new WXMediaMessage(sellWebPage);
            msg.title = content;
            msg.description = content;
            msg.setThumbImage(thumbBmp);
            return msg;
        }
        if (weChatMessageType == WeChatMessageType.ShareSell || weChatMessageType == WeChatMessageType.ShareSellToTimeline) {
            if ((TextUtils.isEmpty(url) || !NetworkUtils.isCNTradeHeroURL(url))) {
                WXWebpageObject sellWebPage = new WXWebpageObject();
                sellWebPage.webpageUrl = traget_user_millionaire_page;
                WXMediaMessage msg = new WXMediaMessage(sellWebPage);
                msg.title=totalShare;
                msg.description = totalShare;
                msg.setThumbImage(thumbBmp);
                return msg;
            } else {
                WXWebpageObject sellWebPage = new WXWebpageObject();
                sellWebPage.webpageUrl = url;
                WXMediaMessage msg = new WXMediaMessage(sellWebPage);
                msg.title = content;
                msg.description = content;
                msg.setThumbImage(thumbBmp);
                return msg;
            }
        }
        if (weChatMessageType == WeChatMessageType.StockRecommendToWeChat
                || weChatMessageType == WeChatMessageType.StockRecommendToMoment) {
            WXWebpageObject sellWebPage = new WXWebpageObject();
            sellWebPage.webpageUrl = "http://cn.tradehero.mobi/shr/recommend.html?timeline="+weChatDTO.id+"&type=stock";
            WXMediaMessage msg = new WXMediaMessage(sellWebPage);
            msg.title = weChatDTO.title;
            msg.description = weChatDTO.description;
            msg.setThumbImage(thumbBmp);
            return msg;
        }

        WXWebpageObject webPage = new WXWebpageObject();
        webPage.webpageUrl = Constants.WECHAT_SHARE_URL_INSTALL_APP;

        WXMediaMessage weChatMsg = new WXMediaMessage(webPage);
        if (TextUtils.isEmpty(totalShare)) {
            weChatMsg.description = getString(weChatMessageType.getTitleResId());
            weChatMsg.title = title;
        } else {
            weChatMsg.title = content;
            weChatMsg.description = content;
        }
        weChatMsg.setThumbImage(thumbBmp);
        return weChatMsg;
    }

    private SendMessageToWX.Req buildRequest(WXMediaMessage weChatMsg) {
        SendMessageToWX.Req weChatReq = new SendMessageToWX.Req();
        weChatReq.transaction = String.valueOf(System.currentTimeMillis());
        //not sure for transaction, maybe identify id?
        if (weChatDTO.type == WeChatMessageType.Invite || weChatDTO.type == WeChatMessageType.ShareSell
                || weChatDTO.type == WeChatMessageType.StockRecommendToWeChat) {
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
                    sendTackMessage();
                    try{
                        analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.SHARE_WECHAT_SUCCESSFULLY));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            default:
                finish();
                break;
        }
    }

    private void sendTackMessage(){
        if (weChatDTO == null) {
            return;
        }
        String shareContent = weChatDTO.title;
        if(TextUtils.isEmpty(shareContent)){
            return;
        }

        //If the number of fans more than 9, the share content of it does not have url but need to track the share.
        String fansSummary = getResources().getString(R.string.share_amount_fans_num_summary).substring(0, 10);
        if(shareContent.contains(fansSummary)){
            userServiceWrapper.get().trackShare("fans", new TrackCallback());
            return;
        }

        String loginContinuousStr = getResources().getString(R.string.login_continuous_share).substring(0,8);
        if(shareContent.contains(loginContinuousStr)){
            userServiceWrapper.get().trackShare("logincontinuous", new TrackCallback());
            return;
        }

        String[] contents = parseContent(shareContent);
        String url = contents[1];
        if(TextUtils.isEmpty(url)||!NetworkUtils.isCNTradeHeroURL(url)){
            return;
        }
        String eventName = NetworkUtils.getEventName(url);
        if(TextUtils.isEmpty(eventName)){
            return;
        }

        userServiceWrapper.get().trackShare(eventName, new TrackCallback());
    }

    private class TrackCallback implements Callback{
        @Override
        public void success(Object o, Response response) {
        }

        @Override
        public void failure(RetrofitError retrofitError) {

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