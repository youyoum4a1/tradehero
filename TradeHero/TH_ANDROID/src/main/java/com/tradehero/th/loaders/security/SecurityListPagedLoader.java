package com.tradehero.th.loaders.security;

import android.content.Context;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.api.security.key.SecurityListTypeFactory;
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
    @Inject protected SecurityListTypeFactory securityListTypeFactory;

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
        return securityListTypeFactory.cloneAtPage(initial, page);
    }
}
