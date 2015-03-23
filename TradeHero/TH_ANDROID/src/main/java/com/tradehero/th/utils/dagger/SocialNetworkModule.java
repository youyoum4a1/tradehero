package com.tradehero.th.utils.dagger;

import android.content.Context;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tradehero.th.R;
import com.tradehero.th.activities.AuthenticationActivity;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.activities.MainActivity;
import com.tradehero.th.activities.RecommendStocksActivity;
import com.tradehero.th.auth.operator.ConsumerKey;
import com.tradehero.th.auth.operator.ConsumerSecret;
import com.tradehero.th.auth.operator.ForWeiboAppAuthData;
import com.tradehero.th.auth.weibo.WeiboAppAuthData;
import com.tradehero.th.models.share.*;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.network.share.SocialSharerImpl;
import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

import javax.inject.Singleton;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

@Module(
        injects = {
                AuthenticationActivity.class,
                DashboardActivity.class,
                MainActivity.class,
                RecommendStocksActivity.class
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

    public static final String WECHAT_APP_SECRET = "a6afcadca7d218c9b2c44632fc8f884d";
    public static final String WECHAT_APP_ID = "wxe795a0ba8fa23cf7";//release

    private static final String WEIBO_APP_ID = "280704663";//release
    private static final String WEIBO_REDIRECT_URL = "http://www.tradehero.mobi";
    private static final String WEIBO_SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";

    private static final String BAIDU_API_KEY = "iI9WWqP3SfGApTW37UuSyIdc";
    private static final String BAIDU_SECRET_KEY = "i5xkWnVUQLYE703cYG85QoSkrPwjl3ip";

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

    @Provides @Singleton @ForWeiboAppAuthData
    WeiboAppAuthData provideWeiboAppId()
    {
        WeiboAppAuthData data = new WeiboAppAuthData();
        data.appId = WEIBO_APP_ID;
        data.redirectUrl = WEIBO_REDIRECT_URL;
        data.scope = WEIBO_SCOPE;
        return data;
    }


    @Provides @Singleton IWXAPI createWXAPI(Context context)
    {
        IWXAPI weChatApi = WXAPIFactory.createWXAPI(context, WECHAT_APP_ID, true);
        weChatApi.registerApp(WECHAT_APP_ID);
        return weChatApi;
    }

    @Provides SocialSharer provideSocialSharer(SocialSharerImpl socialSharerImpl)
    {
        return socialSharerImpl;
    }

    @Provides(type = Provides.Type.SET_VALUES) @ShareDestinationId Set<Integer> providesShareDestinationFromResources(Context context)
    {
        Set<Integer> destinationIds = new LinkedHashSet<>();
        for (int id : context.getResources().getIntArray(R.array.ordered_share_destinations))
        {
            if (destinationIds.contains(id))
            {
                Timber.e(new IllegalStateException("Destination ids contains twice the id " + id),
                        null);
            }
            destinationIds.add(id);
        }
        return destinationIds;

    }

    @Provides ShareDestinationFactory providesShareDestinationFactory(ShareDestinationFactoryByResources shareDestinationFactoryByResources)
    {
        return shareDestinationFactoryByResources;
    }

    @Provides Comparator<ShareDestination> providesShareDestinationComparator(ShareDestinationIndexResComparator shareDestinationComparator)
    {
        return shareDestinationComparator;
    }
}
