package com.androidth.general.fragments.security;

import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.DTOCacheRx;
import com.androidth.general.api.security.SecurityCompactDTO;
import com.androidth.general.api.security.SecurityCompactDTOList;
import com.androidth.general.api.security.key.SecurityListType;
import com.androidth.general.fragments.BasePagedListRxFragment;
import com.androidth.general.persistence.security.SecurityCompactListCacheRx;
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
