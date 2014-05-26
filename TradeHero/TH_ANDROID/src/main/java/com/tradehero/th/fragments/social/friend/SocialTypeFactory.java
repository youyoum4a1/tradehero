package com.tradehero.th.fragments.social.friend;

import android.content.res.ColorStateList;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangliang on 14-5-26.
 */
@Singleton
public class SocialTypeFactory {

    @Inject
    public SocialTypeFactory()
    {}


    public List<SocalTypeItem> getSocialTypeList()
    {
        List<SocalTypeItem> socialList = new ArrayList<SocalTypeItem>();

        //new ColorStateList()
        socialList.add(new SocalTypeItem(R.drawable.superman_facebook,"Invite from Facebook",R.drawable.social_item_fb,SocialNetworkEnum.FB));
        socialList.add(new SocalTypeItem(R.drawable.superman_facebook,"Invite from Twitter",R.drawable.social_item_tw,SocialNetworkEnum.TW));
        socialList.add(new SocalTypeItem(R.drawable.superman_facebook,"Invite from LinkedIn",R.drawable.social_item_ln,SocialNetworkEnum.LN));
        socialList.add(new SocalTypeItem(R.drawable.superman_facebook,"Invite from Weibo",R.drawable.social_item_weibo,SocialNetworkEnum.WEIBO));

        return socialList;
    }

}
