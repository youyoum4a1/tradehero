package com.tradehero.th.fragments.security;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.fragments.BasePagedListRxFragment;
import com.tradehero.th.persistence.security.SecurityCompactListCacheRx;
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
