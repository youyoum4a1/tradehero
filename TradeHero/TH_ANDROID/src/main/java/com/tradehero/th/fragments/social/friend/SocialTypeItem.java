package com.tradehero.th.fragments.social.friend;

import com.tradehero.th.api.social.SocialNetworkEnum;
import org.jetbrains.annotations.NotNull;

public class SocialTypeItem
{
    @NotNull public SocialNetworkEnum socialNetwork;
    public int imageResource;
    public int titleResource;
    public int backgroundResource;

    //<editor-fold desc="Constructors">
    public SocialTypeItem(int imageResource, int titleResource, int backgroundResource, @NotNull SocialNetworkEnum socialNetwork)
    {
        this.imageResource = imageResource;
        this.titleResource = titleResource;
        this.socialNetwork = socialNetwork;
        this.backgroundResource = backgroundResource;
    }
    //</editor-fold>
}
