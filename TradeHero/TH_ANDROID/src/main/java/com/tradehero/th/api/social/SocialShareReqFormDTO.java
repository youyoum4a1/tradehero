package com.ayondo.academy.api.social;

import android.support.annotation.NonNull;
import java.util.Collections;
import java.util.List;

public class SocialShareReqFormDTO implements HasSocialNetworkEnumList
{
    @NonNull public final List<SocialNetworkEnum> networks;

    //<editor-fold desc="Constructors">
    public SocialShareReqFormDTO(@NonNull List<SocialNetworkEnum> networks)
    {
        this.networks = networks;
    }
    //</editor-fold>

    @Override @NonNull public List<SocialNetworkEnum> getSocialNetworkEnumList()
    {
        return Collections.unmodifiableList(networks);
    }
}
