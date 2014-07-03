package com.tradehero.th.loaders.security;

import android.content.Context;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.api.security.key.SecurityListTypeFactory;
import com.tradehero.th.loaders.PagedDTOCacheLoaderNew;
import com.tradehero.th.persistence.security.SecurityCompactListCache;
import dagger.Lazy;
import javax.inject.Inject;

public class SecurityListPagedLoader extends PagedDTOCacheLoaderNew<
        SecurityListType,
        SecurityId,
        SecurityIdList>
{
    @Inject protected Lazy<SecurityCompactListCache> securityCompactListCache;
    @Inject protected SecurityListTypeFactory securityListTypeFactory;

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
    }

    public SecurityListPagedLoader(Context context)
    {
        super(context);
    }

    @Override protected DTOCacheNew<SecurityListType, SecurityIdList> getCache()
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
