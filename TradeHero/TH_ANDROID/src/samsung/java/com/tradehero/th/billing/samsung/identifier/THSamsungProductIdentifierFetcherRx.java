package com.ayondo.academy.billing.samsung.identifier;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import com.tradehero.common.billing.samsung.identifier.SamsungProductIdentifierFetcherRx;

public interface THSamsungProductIdentifierFetcherRx
    extends SamsungProductIdentifierFetcherRx<
            SamsungSKUListKey,
            SamsungSKU,
            SamsungSKUList>
{
}
