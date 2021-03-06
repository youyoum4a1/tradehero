package com.tradehero.th.network.share;

import android.content.Context;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.auth.operator.ConsumerKey;
import com.tradehero.th.auth.operator.ConsumerSecret;
import com.tradehero.th.auth.operator.FacebookAppId;
import com.tradehero.th.auth.operator.FacebookPermissions;
import com.tradehero.th.auth.operator.ForWeiboAppAuthData;
import com.tradehero.th.auth.weibo.WeiboAppAuthData;
import com.tradehero.th.models.share.ShareDestination;
import com.tradehero.th.models.share.ShareDestinationFactory;
import com.tradehero.th.models.share.ShareDestinationFactoryByResources;
import com.tradehero.th.models.share.ShareDestinationId;
import com.tradehero.th.models.share.ShareDestinationIndexResComparator;
import dagger.Module;
import dagger.Provides;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Singleton;
import timber.log.Timber;

import static com.facebook.FacebookPermissionsConstants.EMAIL;
import static com.facebook.FacebookPermissionsConstants.PUBLIC_PROFILE;
import static com.facebook.FacebookPermissionsConstants.PUBLISH_WALL_FRIEND;

@Module(
        injects = {
        },
        complete = false,
        library = true
)
public class SocialNetworkAppModule
{
    private static final String TWITTER_CONSUMER_KEY = "sJY7n9k29TAhraq4VjDYeg";
    private static final String TWITTER_CONSUMER_SECRET = "gRLhwCd3YgdaKKEH7Gwq9FI75TJuqHfi2TiDRwUHo";
    private static final String LINKEDIN_CONSUMER_KEY = "afed437khxve";
    private static final String LINKEDIN_CONSUMER_SECRET = "hO7VeSyL4y1W2ZiK";
    private static final String FACEBOOK_APP_ID = "431745923529834";
    public static final String WECHAT_APP_ID = "wxe795a0ba8fa23cf7";//release
    //public static final String WECHAT_APP_ID = "wxbd1f7f377d636b55";//test

    private static final String WEIBO_APP_ID = "280704663";//release
    //private static final String WEIBO_APP_ID = "551229853";//test
    private static final String WEIBO_REDIRECT_URL = "http://www.tradehero.mobi";
    private static final String WEIBO_SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";

    @Provides @Singleton @ConsumerKey(SocialNetworkEnum.TW) String provideTwitterConsumerKey()
    {
        return TWITTER_CONSUMER_KEY;
    }

    @Provides @Singleton @ConsumerSecret(SocialNetworkEnum.TW) String provideTwitterConsumerSecret()
    {
        return TWITTER_CONSUMER_SECRET;
    }

    @Provides @Singleton @ConsumerKey(SocialNetworkEnum.LN) String provideLinkedInConsumerKey()
    {
        return LINKEDIN_CONSUMER_KEY;
    }
    @Provides @Singleton @ConsumerSecret(SocialNetworkEnum.LN) String provideLinkedInConsumerSecret()
    {
        return LINKEDIN_CONSUMER_SECRET;
    }

    @Provides @Singleton @FacebookAppId String provideFacebookAppId()
    {
        return FACEBOOK_APP_ID;
    }

    @Provides @Singleton @ForWeiboAppAuthData
    WeiboAppAuthData provideWeiboAppId()
    {
        return new WeiboAppAuthData(
            WEIBO_APP_ID,
            WEIBO_REDIRECT_URL,
            WEIBO_SCOPE);
    }

    @Provides @Singleton @FacebookPermissions List<String> provideFacebookPermissions()
    {
        // TODO separate read permission and publish/write permission
        return Arrays.asList(PUBLIC_PROFILE, EMAIL, PUBLISH_WALL_FRIEND);
    }

    @Provides @Singleton IWXAPI createWXAPI(Context context)
    {
        IWXAPI weChatApi = WXAPIFactory.createWXAPI(context, WECHAT_APP_ID, false);
        weChatApi.registerApp(WECHAT_APP_ID);
        return weChatApi;
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
