package com.androidth.general.api.social;

import android.support.annotation.NonNull;
import com.androidth.general.api.share.SocialShareFormDTO;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReferralCodeShareFormDTO implements SocialShareFormDTO, HasSocialNetworkEnumList
{
    @NonNull public final List<SocialNetworkEnum> networks;

    //<editor-fold desc="Constructors">
    public ReferralCodeShareFormDTO(@NonNull SocialNetworkEnum network)
    {
        this(Arrays.asList(network));
    }

    public ReferralCodeShareFormDTO(@NonNull List<SocialNetworkEnum> networks)
    {
        this.networks = networks;
    }
    //</editor-fold>

    @NonNull @Override public List<SocialNetworkEnum> getSocialNetworkEnumList()
    {
        return Collections.unmodifiableList(networks);
    }
}
