package com.androidth.general.api.competition;

import android.support.annotation.NonNull;
import com.androidth.general.api.social.HasSocialNetworkEnumList;
import com.androidth.general.api.social.SocialNetworkEnum;
import java.util.Collections;
import java.util.List;

public class CompetitionPreseasonShareFormDTO implements HasSocialNetworkEnumList
{
    @NonNull public final List<SocialNetworkEnum> networks;
    @NonNull public int providerId;

    public CompetitionPreseasonShareFormDTO(@NonNull ProviderId providerId, @NonNull List<SocialNetworkEnum> networks)
    {
        this.providerId = providerId.key;
        this.networks = networks;
    }

    @Override @NonNull public List<SocialNetworkEnum> getSocialNetworkEnumList()
    {
        return Collections.unmodifiableList(networks);
    }
}