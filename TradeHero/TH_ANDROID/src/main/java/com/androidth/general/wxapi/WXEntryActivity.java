package com.androidth.general.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
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
import com.androidth.general.common.utils.THToast;
import com.androidth.general.R;
import com.androidth.general.api.share.TrackShareDTO;
import com.androidth.general.api.share.wechat.WeChatDTO;
import com.androidth.general.api.share.wechat.WeChatMessageType;
import com.androidth.general.api.share.wechat.WeChatTrackShareFormDTO;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.base.THApp;
import com.androidth.general.exception.THException;
import com.androidth.general.models.graphics.ForSecurityItemForeground;
import com.androidth.general.network.service.WeChatServiceWrapper;
import com.androidth.general.utils.Constants;
import dagger.Lazy;
import java.io.IOException;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.functions.Action1;
import timber.log.Timber;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler //created by alex
{
    public static final int WECHAT_MESSAGE_TYPE_NONE = -1;
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
    private static final String WECHAT_DTO_INTENT_KEY = WXEntryActivity.class.getName() + ".weChatDTOKey";
    private static final String WECHAT_SHARE_NEWS_KEY = "news:";
    private static final String WECHAT_SHARE_TYPE_VALUE = "WeChat";

    private WeChatDTO weChatDTO;
    private Bitmap mBitmap;
    @Nullable private Subscription trackShareSubscription;

    @Inject CurrentUserId currentUserId;
    @Inject IWXAPI mWeChatApi;
    @Inject WeChatServiceWrapper weChatServiceWrapper;
    @Inject Lazy<Picasso> picassoLazy;
    @Inject @ForSecurityItemForeground protected Transformation foregroundTransformation;

    public static void putWeChatDTO(@NonNull Intent intent, @NonNull WeChatDTO weChatDTO)
    {
        intent.putExtra(WECHAT_DTO_INTENT_KEY, weChatDTO.getArgs());
    }

    @NonNull public static WeChatDTO getWeChatDTO(@NonNull Intent intent)
    {
        return new WeChatDTO(intent.getBundleExtra(WECHAT_DTO_INTENT_KEY));
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        THApp app = THApp.get(this);
        app.plus(new WXEntryActivityModule(this)).inject(this);

        // TODO take this intent extraction into a separate method and use a new
        // WeChatDTO method to read from Intent.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        weChatDTO = getWeChatDTO(getIntent());
        loadImage();

        boolean isWXInstalled = mWeChatApi.isWXAppInstalled();
        if (isWXInstalled && weChatDTO.type != null)
        {
            if (mWeChatApi.getWXAppSupportAPI()
                    >= TIMELINE_SUPPORTED_VERSION) //wechat 4.2 support timeline
            {
                WXMediaMessage weChatMessage = buildMessage(weChatDTO.type);
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
        if (weChatDTO != null && weChatDTO.title != null && !weChatDTO.title.isEmpty())
        {
            weChatMsg.title = weChatDTO.title;
            weChatMsg.description = weChatDTO.title;
        }
        else
        {
            weChatMsg.title = weChatMsg.description;
        }
        int i = 0;
        while (weChatDTO != null && weChatDTO.imageURL != null && mBitmap == null && i < 200)
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
        final WeChatDTO weChatDTOCopy = weChatDTO;
        if (weChatDTOCopy != null && weChatDTOCopy.imageURL != null && !weChatDTOCopy.imageURL.isEmpty())
        {
            Thread thread = new Thread(new Runnable()
            {
                @Override public void run()
                {
                    try
                    {
                        Bitmap picassoBmp = picassoLazy.get().load(weChatDTOCopy.imageURL).get();
                        if (picassoBmp != null)
                        {
                            Bitmap tempBitmap = Bitmap.createBitmap(picassoBmp);
                            // TODO find a way to force picasso to redownload and not have a recycled image.
                            if (tempBitmap != null && !tempBitmap.isRecycled())
                            {
                                mBitmap = Bitmap.createScaledBitmap(tempBitmap, 250, 250, false);
                            }
                        }
                    }
                    catch (IOException e)
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
        try
        {
            if (mBitmap == null)
            {
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.splash_logo);
                //mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.notification_logo);
                mBitmap = Bitmap.createScaledBitmap(mBitmap, 250, 250, false);
            }
        }
        catch (OutOfMemoryError e)
        {
            Timber.e(e, null);
        }
    }

    private SendMessageToWX.Req buildRequest(WXMediaMessage weChatMsg)
    {
        SendMessageToWX.Req weChatReq = new SendMessageToWX.Req();
        weChatReq.transaction = String.valueOf(System.currentTimeMillis());
        //not sure for transaction, maybe identify id?
        if (weChatDTO.type == WeChatMessageType.Invite)
        {
            weChatReq.scene = SendMessageToWX.Req.WXSceneSession;
        }
        else
        {
            weChatReq.scene = SendMessageToWX.Req.WXSceneTimeline;
        }

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
        WeChatTrackShareFormDTO weChatTrackShareFormDTO = new WeChatTrackShareFormDTO();
        weChatTrackShareFormDTO.msg = WECHAT_SHARE_NEWS_KEY + weChatDTO.id;
        weChatTrackShareFormDTO.type = WECHAT_SHARE_TYPE_VALUE;

        detachTrackShareSubscription();
        trackShareSubscription = AppObservable.bindActivity(
                this,
                weChatServiceWrapper.trackShareRx(currentUserId.toUserBaseKey(), weChatTrackShareFormDTO))
                .subscribe(
                        new Action1<TrackShareDTO>()
                        {
                            @Override public void call(TrackShareDTO shareDTO)
                            {
                                WXEntryActivity.this.onSharedToWeChat(shareDTO);
                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable error)
                            {
                                WXEntryActivity.this.onShareToWeChatError(error);
                            }
                        });
    }

    private void detachTrackShareSubscription()
    {
        Subscription copy = trackShareSubscription;
        if (copy != null)
        {
            copy.unsubscribe();
        }
        trackShareSubscription = null;
    }

    @Override protected void onDestroy()
    {
        detachTrackShareSubscription();
        super.onDestroy();
    }

    public void onSharedToWeChat(@SuppressWarnings("UnusedParameters") TrackShareDTO args)
    {
        finish();
    }

    public void onShareToWeChatError(Throwable e)
    {
        THToast.show(new THException(e));
        finish();
    }

    /*
        Fixed a bug by WeChat SDK
        https://www.pivotaltracker.com/story/show/75789704
     */
    @Override public boolean onTouchEvent(MotionEvent event)
    {
        finish();
        return super.onTouchEvent(event);
    }
}