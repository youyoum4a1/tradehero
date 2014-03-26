package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABProductIdentifierFetcherHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.exception.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/27/13 Time: 1:23 PM To change this template use File | Settings | File Templates. */
public interface THIABProductIdentifierFetcherHolder
    extends IABProductIdentifierFetcherHolder<
            IABSKUListKey,
            IABSKU,
            IABSKUList,
            IABException>
{
}
