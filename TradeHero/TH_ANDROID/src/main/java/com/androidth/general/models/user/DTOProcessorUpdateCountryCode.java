package com.androidth.general.models.user;

import android.support.annotation.NonNull;
import com.androidth.general.api.competition.key.ProviderListKey;
import com.androidth.general.api.users.UpdateCountryCodeDTO;
import com.androidth.general.api.users.UpdateCountryCodeFormDTO;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.models.ThroughDTOProcessor;
import com.androidth.general.persistence.competition.ProviderListCacheRx;
import com.androidth.general.persistence.user.UserProfileCacheRx;

import rx.schedulers.Schedulers;

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
            providerListCache.get(new ProviderListKey())
                    .subscribeOn(Schedulers.io());
        }

        return value;
    }
}
