package com.ayondo.academy.api.competition;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.social.SocialNetworkEnum;
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