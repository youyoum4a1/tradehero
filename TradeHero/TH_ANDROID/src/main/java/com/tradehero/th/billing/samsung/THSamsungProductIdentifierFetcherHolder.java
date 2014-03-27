package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.samsung.SamsungProductIdentifierFetcherHolder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import com.tradehero.common.billing.samsung.exception.SamsungException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/27/13 Time: 1:23 PM To change this template use File | Settings | File Templates. */
public interface THSamsungProductIdentifierFetcherHolder
    extends SamsungProductIdentifierFetcherHolder<
        SamsungSKUListKey,
        SamsungSKU,
        SamsungSKUList,
        SamsungException>
{
}
