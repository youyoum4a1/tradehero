package com.tradehero.th.api.competition;

import android.support.annotation.NonNull;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.SocialShareReqFormDTO;
import java.util.List;
import javax.inject.Inject;

public class CompetitionPreseasonShareFormDTOFactory
{
    //<editor-fold desc="Constructors">
    @Inject public CompetitionPreseasonShareFormDTOFactory()
    {
        super();
    }
    //</editor-fold>

    @NonNull public CompetitionPreseasonShareFormDTO createFrom(
            @NonNull List<SocialNetworkEnum> shareDestinationWithEnums,
            @NonNull ProviderId providerId)
    {
        return new CompetitionPreseasonShareFormDTO(providerId, shareDestinationWithEnums);
    }
}