package com.tradehero.th.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.AbsListView;
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
import com.tradehero.th.BottomTabsQuickReturnListViewListener;
import com.tradehero.th.R;
import com.tradehero.th.UIModule;
import com.tradehero.th.activities.MarketUtil;
import com.tradehero.th.api.share.TrackShareDTO;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.api.share.wechat.WeChatTrackShareFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.base.THApp;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.graphics.ForSecurityItemForeground;
import com.tradehero.th.network.service.WeChatServiceWrapper;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.dagger.AppModule;
import com.tradehero.th.utils.route.THRouter;
import dagger.Module;
import dagger.Provides;
import java.io.IOException;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class WXEntryActivity extends Activity
        implements IWXAPIEventHandler
{
    public static final int WECHAT_MESSAGE_TYPE_NONE = -1;
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
    private static final String WECHAT_DTO_INTENT_KEY = WXEntryActivity.class.getName() + ".weChatDTOKey";
    private static final String WECHAT_SHARE_NEWS_KEY = "news:";
    private static final String WECHAT_SHARE_TYPE_VALUE = "WeChat";

    private WeChatDTO weChatDTO;
    @Nullable private Subscription buildRequestSubscription;
    @Nullable private Subscription trackShareSubscription;

    @Inject CurrentUserId currentUserId;
    @Inject IWXAPI weChatApi;
    @Inject WeChatServiceWrapper weChatServiceWrapper;
    @Inject Picasso picasso;
    @Inject MarketUtil marketUtil;
    @Inject @ForSecurityItemForeground protected Transformation foregroundTransformation;

    public static void putWeChatDTO(@NonNull Intent intent, @NonNull WeChatDTO weChatDTO)
    {
        intent.putExtra(WECHAT_DTO_INTENT_KEY, weChatDTO.getArgs());
    }

    @NonNull public static WeChatDTO getWeChatDTO(@NonNull Intent intent)
    {
        return new WeChatDTO(intent.getBundleExtra(WECHAT_DTO_INTENT_KEY));
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        THApp app = THApp.get(this);
        app.plus(new WXEntryActivityModule()).inject(this);

        // TODO take this intent extraction into a separate method and use a new
        // WeChatDTO method to read from Intent.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (WeChatDTO.isValid(getIntent().getBundleExtra(WECHAT_DTO_INTENT_KEY)))
        {
            weChatDTO = getWeChatDTO(getIntent());
        }
        else
        {
            Timber.e(new IllegalArgumentException(), "invalid extras");
            THToast.show("Invalid extras");
            finish();
            return;
        }

        boolean isWXInstalled = weChatApi.isWXAppInstalled();
        if (isWXInstalled && weChatDTO.type != null)
        {
            if (weChatApi.getWXAppSupportAPI()
                    >= TIMELINE_SUPPORTED_VERSION) //wechat 4.2 support timeline
            {
                buildRequestSubscription = buildMessage(weChatDTO.type)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<WXMediaMessage>()
                                {
                                    @Override public void call(WXMediaMessage wxMediaMessage)
                                    {
                                        weChatApi.sendReq(buildRequest(wxMediaMessage));
                                    }
                                },
                                new TimberOnErrorAction("Failed to prepare message"));
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

    @NonNull private Observable<WXMediaMessage> buildMessage(WeChatMessageType weChatMessageType)
    {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = (marketUtil == null || marketUtil.getAppMarketUrl() == null) ? Constants.WECHAT_SHARE_URL
                : marketUtil.getAppMarketUrl();

        final WXMediaMessage weChatMsg = new WXMediaMessage(webpage);
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

        // Default image loader
        Observable<Bitmap> imageLoader = Observable.just(1)
                .subscribeOn(Schedulers.computation())
                .map(new Func1<Integer, Bitmap>()
                {
                    @Override public Bitmap call(Integer integer)
                    {
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.splash_logo);
                        //bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.notification_logo);
                        return Bitmap.createScaledBitmap(bitmap, 250, 250, false);
                    }
                });
        if (weChatDTO != null && weChatDTO.imageURL != null && !weChatDTO.imageURL.isEmpty())
        {
            imageLoader = Observable.just(weChatDTO.imageURL)
                    .subscribeOn(Schedulers.io())
                    .flatMap(new Func1<String, Observable<Bitmap>>()
                    {
                        @Override public Observable<Bitmap> call(String s)
                        {
                            try
                            {
                                Bitmap picassoBmp = picasso.load(weChatDTO.imageURL)
                                        .resize(250, 250).get();
                                if (picassoBmp != null)
                                {
                                    return Observable.just(picassoBmp);
                                }
                            } catch (IOException e)
                            {
                                return Observable.error(e);
                            }
                            return Observable.error(new NullPointerException("No image to download"));
                        }
                    })
                    // Fallback to default
                    .onErrorResumeNext(imageLoader);
        }

        return imageLoader.map(new Func1<Bitmap, WXMediaMessage>()
        {
            @Override public WXMediaMessage call(Bitmap bitmap)
            {
                weChatMsg.thumbData = Util.bmpToByteArray(bitmap, true);
                bitmap.recycle();
                return weChatMsg;
            }
        });
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

    @Override protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        weChatApi.handleIntent(intent, this);
    }

    //<editor-fold desc="WeChat Callbacks">
    @Override public void onReq(BaseReq req)
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

    @Override public void onResp(BaseResp resp)
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
        if (buildRequestSubscription != null)
        {
            buildRequestSubscription.unsubscribe();
        }
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

    @Module(
            addsTo = AppModule.class,
            includes = {
                    UIModule.class
            },
            library = true,
            complete = false,
            overrides = true
    )
    public class WXEntryActivityModule
    {
        @Provides Activity provideActivity()
        {
            return WXEntryActivity.this;
        }

        @Provides DashboardNavigator provideDashboardNavigator()
        {
            throw new IllegalStateException("No navigator available when in Wechat share");
        }

        @Provides @Singleton THRouter provideTHRouter(Context context, Provider<DashboardNavigator> navigatorProvider)
        {
            throw new IllegalStateException("No router available when in Wechat share");
        }

        @Provides @BottomTabsQuickReturnListViewListener AbsListView.OnScrollListener provideDashboardBottomTabScrollListener()
        {
            return new AbsListView.OnScrollListener()
            {
                @Override public void onScrollStateChanged(AbsListView absListView, int i)
                {
                }

                @Override public void onScroll(AbsListView absListView, int i, int i2, int i3)
                {
                }
            };
        }
    }
}