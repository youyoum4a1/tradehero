package com.tradehero.th.fragments.social.friend;

import android.app.Activity;
import android.content.res.ColorStateList;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.auth.operator.LinkedIn;
import com.tradehero.th.fragments.social.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangliang on 14-5-26.
 */
@Singleton
public class SocialNetworkFactory {
    private Map<SocialNetworkEnum,SocialLinkHelper> socialLinkHelperCache;

    @Inject
    public SocialNetworkFactory() {
    }


    public List<SocalTypeItem> getSocialTypeList() {
        List<SocalTypeItem> socialList = new ArrayList<SocalTypeItem>();

        //new ColorStateList()
        socialList.add(new SocalTypeItem(R.drawable.icn_fb_round,  R.string.inivte_from_facebook, R.drawable.social_item_fb, SocialNetworkEnum.FB));
        socialList.add(new SocalTypeItem(R.drawable.icn_twitter_round, R.string.inivte_from_twitter, R.drawable.social_item_tw, SocialNetworkEnum.TW));
        socialList.add(new SocalTypeItem(R.drawable.icn_linkedin_round, R.string.inivte_from_linkedIn, R.drawable.social_item_ln, SocialNetworkEnum.LN));
        socialList.add(new SocalTypeItem(R.drawable.icn_weibo_round, R.string.inivte_from_weibo, R.drawable.social_item_weibo, SocialNetworkEnum.WB));

        return socialList;
    }


    public Class<? extends SocialFriendsFragment> findProperTargetFragment(SocialNetworkEnum socialNetworkEnum) {
        switch (socialNetworkEnum) {
            case FB:
                return FacebookSocialFriendsFragment.class;

            case TW:
                return TwitterSocialFriendsFragment.class;

            case LN:
                return LinkedInSocialFriendsFragment.class;

            case WB:
                return WeiboSocialFriendsFragment.class;

        }
        throw new IllegalArgumentException("Do not support " + socialNetworkEnum);
    }

    public SocialLinkHelper buildSocialLinkerHelper(Activity context, SocialNetworkEnum socialNetwork)
    {
        if (socialLinkHelperCache == null ){
            socialLinkHelperCache = new HashMap<>();
        }
        if (socialLinkHelperCache.get(socialNetwork) != null)
        {
            return socialLinkHelperCache.get(socialNetwork);
        }
        SocialLinkHelper socialLinkHelper = null;
        switch (socialNetwork) {
            case FB:
                socialLinkHelper = new FacebookSocialLinkHelper(context);
                break;
            case TW:
                socialLinkHelper = new TwitterSocialLinkHelper(context);
                break;
            case LN:
                socialLinkHelper = new LinkedInSocialLinkHelper(context);
                break;
            case WB:
                socialLinkHelper = new WeiboSocialLinkHelper(context);
                break;
                default:
                    throw new IllegalArgumentException("Do not support " + socialNetwork);
        }
        socialLinkHelperCache.put(socialNetwork,socialLinkHelper);
        return socialLinkHelper;

    }

}
