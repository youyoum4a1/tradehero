package com.tradehero.th.api.social;

import android.support.annotation.NonNull;
import com.tradehero.th.api.share.SocialShareFormDTO;
import java.util.Arrays;
import java.util.List;

public class ReferralCodeShareFormDTO implements SocialShareFormDTO
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
}
