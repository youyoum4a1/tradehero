package com.tradehero.th.api.security;

import com.tradehero.th.api.competition.BasicProviderSecurityListType;
import com.tradehero.th.api.competition.ProviderSecurityListType;
import com.tradehero.th.api.competition.WarrantProviderSecurityListType;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by xavier on 1/21/14.
 */
@Singleton public class SecurityListTypeFactory
{
    public static final String TAG = SecurityListTypeFactory.class.getSimpleName();

    @Inject public SecurityListTypeFactory()
    {
    }

    public SecurityListType cloneAtPage(SecurityListType initial, int page)
    {
        if (initial instanceof TrendingSecurityListType)
        {
            TrendingSecurityListType trendingInitial = (TrendingSecurityListType) initial;
            if (trendingInitial instanceof TrendingBasicSecurityListType)
            {
                return new TrendingBasicSecurityListType(trendingInitial.exchange, page, initial.perPage);
            }
            else if (trendingInitial instanceof TrendingVolumeSecurityListType)
            {
                return new TrendingVolumeSecurityListType(trendingInitial.exchange, page, initial.perPage);
            }
            else if (trendingInitial instanceof TrendingPriceSecurityListType)
            {
                return new TrendingPriceSecurityListType(trendingInitial.exchange, page, initial.perPage);
            }
            else if (trendingInitial instanceof TrendingAllSecurityListType)
            {
                return new TrendingAllSecurityListType(trendingInitial.exchange, page, initial.perPage);
            }
            else
            {
                return new TrendingSecurityListType(trendingInitial.exchange, page, initial.perPage);
            }
        }
        else if (initial instanceof SearchSecurityListType)
        {
            SearchSecurityListType searchInitial = (SearchSecurityListType) initial;
            return new SearchSecurityListType(searchInitial.searchString, page, initial.perPage);
        }
        else if (initial instanceof ProviderSecurityListType)
        {
            ProviderSecurityListType providerSecurityListType = (ProviderSecurityListType) initial;
            if (providerSecurityListType instanceof BasicProviderSecurityListType)
            {
                return new BasicProviderSecurityListType(providerSecurityListType.getProviderId(), initial.page, initial.perPage);
            }
            else if (providerSecurityListType instanceof WarrantProviderSecurityListType)
            {
                return new WarrantProviderSecurityListType(providerSecurityListType.getProviderId(), initial.page, initial.perPage);
            }
        }
        return null;
    }
}
