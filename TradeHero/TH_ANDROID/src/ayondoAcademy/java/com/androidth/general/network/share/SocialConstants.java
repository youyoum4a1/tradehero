package com.androidth.general.network.share;

import com.androidth.general.BuildConfig;
import com.androidth.general.auth.weibo.WeiboAppAuthData;
import com.androidth.general.utils.Constants;

public class SocialConstants
{
    public static final String LINKEDIN_CONSUMER_KEY = "afed437khxve";
    public static final String LINKEDIN_CONSUMER_SECRET = "hO7VeSyL4y1W2ZiK";

    public static final String TWITTER_CONSUMER_KEY = "sJY7n9k29TAhraq4VjDYeg";
    public static final String TWITTER_CONSUMER_SECRET = "gRLhwCd3YgdaKKEH7Gwq9FI75TJuqHfi2TiDRwUHo";

    public static final String FACEBOOK_APP_ID = "254854381560011";

    public static final String WECHAT_APP_ID_RELEASE = "wxe795a0ba8fa23cf7";//release
    public static final String WECHAT_APP_ID_TEST = "wxbd1f7f377d636b55";//test
    public static final String WECHAT_APP_ID = Constants.RELEASE ? WECHAT_APP_ID_RELEASE : WECHAT_APP_ID_TEST;

    public static final String WEIBO_APP_ID = "280704663";//release
    //private static final String WEIBO_APP_ID = "551229853";//test
    public static final String WEIBO_REDIRECT_URL = "http://www.tradehero.mobi";
    public static final String WEIBO_SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";
    public static final WeiboAppAuthData weiboAppAuthData = new WeiboAppAuthData(
        SocialConstants.WEIBO_APP_ID,
        SocialConstants.WEIBO_REDIRECT_URL,
        SocialConstants.WEIBO_SCOPE);
    
}
