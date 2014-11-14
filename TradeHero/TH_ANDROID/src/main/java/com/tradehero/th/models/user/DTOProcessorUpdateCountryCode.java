package com.tradehero.th.models.user;

import android.support.annotation.NonNull;
import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.api.users.UpdateCountryCodeDTO;
import com.tradehero.th.api.users.UpdateCountryCodeFormDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.ThroughDTOProcessor;
import com.tradehero.th.persistence.competition.ProviderListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;

public class DTOProcessorUpdateCountryCode extends ThroughDTOProcessor<UpdateCountryCodeDTO>
{
    @NonNull private final UserProfileCacheRx userProfileCache;
    @NonNull private final ProviderListCacheRx providerListCache;
    @NonNull private final UserBaseKey playerId;
    @NonNull private final UpdateCountryCodeFormDTO updateCountryCodeFormDTO;

    public DTOProcessorUpdateCountryCode(
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull ProviderListCacheRx providerListCache,
            @NonNull UserBaseKey playerId,
            @NonNull UpdateCountryCodeFormDTO updateCountryCodeFormDTO)
    {
        this.userProfileCache = userProfileCache;
        this.providerListCache = providerListCache;
        this.playerId = playerId;
        this.updateCountryCodeFormDTO = updateCountryCodeFormDTO;
    }

    @Override public UpdateCountryCodeDTO process(@NonNull UpdateCountryCodeDTO value)
    {
        if (value.updated)
        {
            UserProfileDTO cachedUserProfile = userProfileCache.getValue(playerId);
            if (cachedUserProfile != null
                    && updateCountryCodeFormDTO.countryCode != null)
            {
                cachedUserProfile.countryCode = updateCountryCodeFormDTO.countryCode;
            }
            userProfileCache.get(playerId);
            providerListCache.get(new ProviderListKey());
        }

        return value;
    }
}
