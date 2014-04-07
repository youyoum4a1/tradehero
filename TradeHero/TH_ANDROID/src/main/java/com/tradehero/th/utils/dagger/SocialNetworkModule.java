package com.tradehero.th.utils.dagger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;
import com.tradehero.th.R;
import com.tradehero.th.activities.AuthenticationActivity;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.auth.operator.ConsumerKey;
import com.tradehero.th.auth.operator.ConsumerSecret;
import com.tradehero.th.auth.operator.FacebookAppId;
import com.tradehero.th.auth.operator.FacebookPermissions;
import com.tradehero.th.utils.Constants;
import dagger.Module;
import dagger.Provides;
import java.util.Collection;
import javax.inject.Singleton;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/27/14 Time: 11:42 AM Copyright (c) TradeHero
 */
@Module(
        injects = {
                AuthenticationActivity.class,
                DashboardActivity.class,
        },
        complete = false,
        library = true
)

public class SocialNetworkModule
{
    private static final String TWITTER_CONSUMER_KEY = "sJY7n9k29TAhraq4VjDYeg";
    private static final String TWITTER_CONSUMER_SECRET = "gRLhwCd3YgdaKKEH7Gwq9FI75TJuqHfi2TiDRwUHo";
    private static final String LINKEDIN_CONSUMER_KEY = "afed437khxve";
    private static final String LINKEDIN_CONSUMER_SECRET = "hO7VeSyL4y1W2ZiK";
    private static final String FACEBOOK_APP_ID = "431745923529834";
    public static final String WECHAT_APP_ID = "wxe795a0ba8fa23cf7";
    //public static final String WECHAT_APP_ID = "wxbd1f7f377d636b55";

    @Provides @Singleton @ConsumerKey("Twitter") String provideTwitterConsumerKey()
    {
        return TWITTER_CONSUMER_KEY;
    }
    @Provides @Singleton @ConsumerSecret("Twitter") String provideTwitterConsumerSecret()
    {
        return TWITTER_CONSUMER_SECRET;
    }

    @Provides @Singleton @ConsumerKey("LinkedIn") String provideLinkedInConsumerKey()
    {
        return LINKEDIN_CONSUMER_KEY;
    }
    @Provides @Singleton @ConsumerSecret("LinkedIn") String provideLinkedInConsumerSecret()
    {
        return LINKEDIN_CONSUMER_SECRET;
    }

    @Provides @Singleton @FacebookAppId String provideFacebookAppId()
    {
        return FACEBOOK_APP_ID;
    }

    @Provides @Singleton @FacebookPermissions Collection<String> provideFacebookPermissions()
    {
        return null;
    }

    @Provides @Singleton IWXAPI createWXAPI(Context context)
    {
        IWXAPI weChatApi = WXAPIFactory.createWXAPI(context, WECHAT_APP_ID, false);
        weChatApi.registerApp(WECHAT_APP_ID);
        return weChatApi;
    }

    @Provides WXMediaMessage createWXMsg()
    {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = Constants.BASE_STATIC_CONTENT_URL;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        return msg;
    }

    @Provides SendMessageToWX.Req createWXReq()
    {
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(
                System.currentTimeMillis()); //not sure for transaction, maybe identify id?
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        return req;
    }

}
