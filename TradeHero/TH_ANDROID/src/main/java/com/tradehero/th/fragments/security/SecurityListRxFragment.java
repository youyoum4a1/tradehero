package com.tradehero.th.fragments.security;

import android.view.View;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityCompactDTOList;
import com.tradehero.th.api.security.key.SecurityListType;
import com.tradehero.th.fragments.BasePagedListRxFragment;
import com.tradehero.th.persistence.security.SecurityCompactListCacheRx;
import javax.inject.Inject;

abstract public class SecurityListRxFragment<ViewType extends View & DTOView<SecurityCompactDTO>>
        extends BasePagedListRxFragment<
        SecurityListType,
        SecurityCompactDTO,
        SecurityCompactDTOList,
        SecurityCompactDTOList,
        ViewType>
{
    @Inject protected SecurityCompactListCacheRx securityCompactListCache;
}
