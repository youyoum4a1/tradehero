package com.tradehero.th.models.user;

import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.api.users.UpdateCountryCodeDTO;
import com.tradehero.th.api.users.UpdateCountryCodeFormDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.competition.ProviderCache;
import com.tradehero.th.persistence.competition.ProviderListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorUpdateCountryCode implements DTOProcessor<UpdateCountryCodeDTO>
{
    @NotNull private final UserProfileCache userProfileCache;
    @NotNull private final ProviderListCache providerListCache;
    @NotNull private final ProviderCache providerCache;
    @NotNull private final UserBaseKey playerId;
    @NotNull private final UpdateCountryCodeFormDTO updateCountryCodeFormDTO;

    public DTOProcessorUpdateCountryCode(
            @NotNull UserProfileCache userProfileCache,
            @NotNull ProviderListCache providerListCache,
            @NotNull ProviderCache providerCache,
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
            UserProfileDTO cachedUserProfile = userProfileCache.get(playerId);
            if (cachedUserProfile != null
                    && updateCountryCodeFormDTO.countryCode != null)
            {
                cachedUserProfile.countryCode = updateCountryCodeFormDTO.countryCode;
            }
            userProfileCache.getOrFetchAsync(playerId, true);

            providerCache.invalidateAll();
            providerCache.invalidateAll();
            providerListCache.invalidateAll();
            providerListCache.getOrFetchAsync(new ProviderListKey(), true);
        }

        return value;
    }
}
