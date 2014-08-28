package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABProductIdentifierFetcherHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.exception.IABException;

public interface THIABProductIdentifierFetcherHolder
    extends IABProductIdentifierFetcherHolder<
            IABSKUListKey,
            IABSKU,
            IABSKUList,
            IABException>
{
}
