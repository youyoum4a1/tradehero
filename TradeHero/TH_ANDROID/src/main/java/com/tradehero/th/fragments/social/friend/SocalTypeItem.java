package com.tradehero.th.fragments.social.friend;

import com.tradehero.th.api.social.SocialNetworkEnum;

/**
 * Created by wangliang on 14-5-26.
 */
public class SocalTypeItem {

    public SocialNetworkEnum socialNetwork;
    public int imageResource;
    public int titleResource;
    public int backgroundResource;

    public SocalTypeItem()
    {

    }

    public SocalTypeItem(int imageResource, int titleResource, int backgroundResource, SocialNetworkEnum socialNetwork)
    {
        this.imageResource = imageResource;
        this.titleResource = titleResource;
        this.socialNetwork = socialNetwork;
        this.backgroundResource = backgroundResource;
    }


}
