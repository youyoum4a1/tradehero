package com.tradehero.th.fragments.social.friend;

import android.support.annotation.NonNull;
import com.tradehero.th.api.social.SocialNetworkEnum;

public class SocialTypeItem
{
    @NonNull public final SocialNetworkEnum socialNetwork;
    public final int imageResource;
    public final int titleResource;
    public final int backgroundResource;

    //<editor-fold desc="Constructors">
    public SocialTypeItem(int imageResource, int titleResource, int backgroundResource, @NonNull SocialNetworkEnum socialNetwork)
    {
        this.imageResource = imageResource;
        this.titleResource = titleResource;
        this.socialNetwork = socialNetwork;
        this.backgroundResource = backgroundResource;
    }
    //</editor-fold>
}
