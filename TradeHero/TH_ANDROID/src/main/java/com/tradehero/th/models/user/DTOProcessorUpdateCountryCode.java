package com.tradehero.th.models.user;

import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.api.users.UpdateCountryCodeDTO;
import com.tradehero.th.api.users.UpdateCountryCodeFormDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.competition.ProviderCacheRx;
import com.tradehero.th.persistence.competition.ProviderListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorUpdateCountryCode implements DTOProcessor<UpdateCountryCodeDTO>
{
    @NotNull private final UserProfileCacheRx userProfileCache;
    @NotNull private final ProviderListCacheRx providerListCache;
    @NotNull private final ProviderCacheRx providerCache;
    @NotNull private final UserBaseKey playerId;
    @NotNull private final UpdateCountryCodeFormDTO updateCountryCodeFormDTO;

    public DTOProcessorUpdateCountryCode(
            @NotNull UserProfileCacheRx userProfileCache,
            @NotNull ProviderListCacheRx providerListCache,
            @NotNull ProviderCacheRx providerCache,
            @NotNull UserBaseKey playerId,
            @NotNull UpdateCountryCodeFormDTO updateCountryCodeFormDTO)
    {
        this.userProfileCache = userProfileCache;
        this.providerListCache = providerListCache;
        this.providerCache = providerCache;
        this.playerId = playerId;
        this.updateCountryCodeFormDTO = updateCountryCodeFormDTO;
    }

    @Override public UpdateCountryCodeDTO process(@NotNull UpdateCountryCodeDTO value)
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

            providerCache.invalidateAll();
            providerCache.invalidateAll();
            providerListCache.invalidateAll();
            providerListCache.get(new ProviderListKey());
        }

        return value;
    }
}
