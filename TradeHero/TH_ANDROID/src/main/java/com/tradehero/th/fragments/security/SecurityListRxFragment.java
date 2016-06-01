package com.ayondo.academy.fragments.security;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTOCacheRx;
import com.ayondo.academy.api.security.SecurityCompactDTO;
import com.ayondo.academy.api.security.SecurityCompactDTOList;
import com.ayondo.academy.api.security.key.SecurityListType;
import com.ayondo.academy.fragments.BasePagedListRxFragment;
import com.ayondo.academy.persistence.security.SecurityCompactListCacheRx;
import javax.inject.Inject;

abstract public class SecurityListRxFragment
        extends BasePagedListRxFragment<
        SecurityListType,
        SecurityCompactDTO,
        SecurityCompactDTOList,
        SecurityCompactDTOList>
{
    @Inject protected SecurityCompactListCacheRx securityCompactListCache;

    @NonNull @Override protected DTOCacheRx<SecurityListType, SecurityCompactDTOList> getCache()
    {
        return securityCompactListCache;
    }
}
