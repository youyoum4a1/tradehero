package com.ayondo.academy.models.user;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.competition.key.ProviderListKey;
import com.ayondo.academy.api.users.UpdateCountryCodeDTO;
import com.ayondo.academy.api.users.UpdateCountryCodeFormDTO;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.models.ThroughDTOProcessor;
import com.ayondo.academy.persistence.competition.ProviderListCacheRx;
import com.ayondo.academy.persistence.user.UserProfileCacheRx;

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
            UserProfileDTO cachedUserProfile = userProfileCache.getCachedValue(playerId);
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
