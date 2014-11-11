package com.tradehero.th.api.achievement;

import com.tradehero.th.api.social.HasSocialNetworkEnumList;
import com.tradehero.th.api.social.SocialNetworkEnum;
import java.util.Collections;
import java.util.List;
import android.support.annotation.NonNull;

public class AchievementShareReqFormDTO implements HasSocialNetworkEnumList
{
    @NonNull public final List<SocialNetworkEnum> networks;

    //<editor-fold desc="Constructors">
    public AchievementShareReqFormDTO(@NonNull List<SocialNetworkEnum> networks)
    {
        this.networks = networks;
    }
    //</editor-fold>

    @Override @NonNull public List<SocialNetworkEnum> getSocialNetworkEnumList()
    {
        return Collections.unmodifiableList(networks);
    }
}
