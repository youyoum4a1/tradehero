package com.androidth.general.api.competition;

import android.support.annotation.NonNull;
import com.androidth.general.api.social.SocialNetworkEnum;
import java.util.List;

public class CompetitionPreseasonShareFormDTOFactory
{
    @NonNull public static CompetitionPreseasonShareFormDTO createFrom(
            @NonNull List<SocialNetworkEnum> shareDestinationWithEnums,
            @NonNull ProviderId providerId)
    {
        return new CompetitionPreseasonShareFormDTO(providerId, shareDestinationWithEnums);
    }
}