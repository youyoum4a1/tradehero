package com.tradehero.th.api.security.key;

import android.support.annotation.NonNull;
import com.tradehero.th.api.competition.key.BasicProviderSecurityListType;
import com.tradehero.th.api.competition.key.ProviderSecurityListType;
import com.tradehero.th.api.competition.key.WarrantUnderlyersProviderSecurityListType;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton public class SecurityListTypeFactory
{
    //<editor-fold desc="Constructors">
    @Inject public SecurityListTypeFactory()
    {
    }
    //</editor-fold>

    public SecurityListType cloneAtPage(@NonNull SecurityListType initial, int page)
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
                return new BasicProviderSecurityListType(providerSecurityListType.providerId, page, initial.perPage);
            }
            else if (providerSecurityListType instanceof WarrantUnderlyersProviderSecurityListType)
            {
                return new WarrantUnderlyersProviderSecurityListType(providerSecurityListType.providerId, page, initial.perPage);
            }
        }
        return null;
    }
}
