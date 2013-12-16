package com.tradehero.th.loaders.security;

import android.content.Context;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.api.security.SearchSecurityListType;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.security.SecurityListType;
import com.tradehero.th.api.security.TrendingAllSecurityListType;
import com.tradehero.th.api.security.TrendingBasicSecurityListType;
import com.tradehero.th.api.security.TrendingPriceSecurityListType;
import com.tradehero.th.api.security.TrendingSecurityListType;
import com.tradehero.th.api.security.TrendingVolumeSecurityListType;
import com.tradehero.th.loaders.PagedDTOCacheLoader;
import com.tradehero.th.persistence.security.SecurityCompactListCache;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Created by xavier on 12/13/13.
 */
public class SecurityListPagedLoader extends PagedDTOCacheLoader<
        SecurityListType,
        SecurityId,
        SecurityIdList>
{
    public static final String TAG = SecurityListPagedLoader.class.getSimpleName();

    @Inject protected Lazy<SecurityCompactListCache> securityCompactListCache;

    public SecurityListPagedLoader(Context context)
    {
        super(context);
    }

    @Override protected DTOCache<SecurityListType, SecurityIdList> getCache()
    {
        return securityCompactListCache.get();
    }

    @Override protected SecurityIdList createEmptyValue()
    {
        return new SecurityIdList();
    }

    @Override protected SecurityListType cloneAtPage(SecurityListType initial, int page)
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
        return null;
    }
}
