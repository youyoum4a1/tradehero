package com.tradehero.th.models.user;

import com.tradehero.th.api.market.ExchangeCompactDTO;
import com.tradehero.th.api.market.ExchangeCompactDTOList;
import com.tradehero.th.api.market.ExchangeListType;
import com.tradehero.th.api.security.key.TrendingBasicSecurityListType;
import com.tradehero.th.api.system.SystemStatusDTO;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.trending.TrendingFragment;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.persistence.market.ExchangeCompactListCache;
import com.tradehero.th.persistence.security.SecurityCompactListCache;
import com.tradehero.th.persistence.system.SystemStatusCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;

public class DTOProcessorUserLogin implements DTOProcessor<UserLoginDTO>
{
    @NotNull private final UserProfileCache userProfileCache;
    @NotNull private final SystemStatusCache systemStatusCache;
    @NotNull private final UserBaseDTOUtil userBaseDTOUtil;
    @NotNull private final ExchangeCompactListCache exchangeCompactListCache;
    @NotNull private final SecurityCompactListCache securityCompactListCache;

    //<editor-fold desc="Constructors">
    public DTOProcessorUserLogin(
            @NotNull UserProfileCache userProfileCache,
            @NotNull SystemStatusCache systemStatusCache,
            @NotNull UserBaseDTOUtil userBaseDTOUtil,
            @NotNull ExchangeCompactListCache exchangeCompactListCache,
            @NotNull SecurityCompactListCache securityCompactListCache)
    {
        this.userProfileCache = userProfileCache;
        this.systemStatusCache = systemStatusCache;
        this.userBaseDTOUtil = userBaseDTOUtil;
        this.exchangeCompactListCache = exchangeCompactListCache;
        this.securityCompactListCache = securityCompactListCache;
    }
    //</editor-fold>

    @Override public UserLoginDTO process(UserLoginDTO value)
    {
        if (value != null)
        {
            UserProfileDTO profile = value.profileDTO;
            if (profile != null)
            {
                UserBaseKey userKey = profile.getBaseKey();
                userProfileCache.put(userKey, profile);
                if (value.systemStatusDTO == null)
                {
                    value.systemStatusDTO = new SystemStatusDTO();
                }
                systemStatusCache.put(userKey, value.systemStatusDTO);

                ExchangeCompactDTOList exchangeCompacts = exchangeCompactListCache.get(new ExchangeListType());
                if (exchangeCompacts != null)
                {
                    ExchangeCompactDTO initialExchange = userBaseDTOUtil.getInitialExchange(profile, exchangeCompacts);
                    if (initialExchange != null)
                    {
                        securityCompactListCache.getOrFetchAsync(
                                new TrendingBasicSecurityListType(
                                        initialExchange.name,
                                        1,
                                        TrendingFragment.DEFAULT_PER_PAGE));
                    }
                }
            }
        }
        return value;
    }
}
