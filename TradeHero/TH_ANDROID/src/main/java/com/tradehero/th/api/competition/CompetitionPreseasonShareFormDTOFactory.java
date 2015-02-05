package com.tradehero.th.api.competition;

import android.support.annotation.NonNull;
import com.tradehero.th.api.social.SocialNetworkEnum;
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