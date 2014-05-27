package com.tradehero.th.fragments.social.friend;

import com.tradehero.th.api.social.SocialNetworkEnum;

/**
 * Created by wangliang on 14-5-26.
 */
public class SocalTypeItem {

    public SocialNetworkEnum socialNetwork;
    public int imageResource;
    public String title;
    public int backgroundResource;

    public SocalTypeItem()
    {

    }

    public SocalTypeItem(int imageResource, String title, int backgroundResource, SocialNetworkEnum socialNetwork)
    {
        this.imageResource = imageResource;
        this.title = title;
        this.socialNetwork = socialNetwork;
        this.backgroundResource = backgroundResource;
    }


}
