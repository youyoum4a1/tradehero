package com.androidth.general.billing.samsung.identifier;

import com.androidth.general.common.billing.samsung.SamsungSKU;
import com.androidth.general.common.billing.samsung.SamsungSKUList;
import com.androidth.general.common.billing.samsung.SamsungSKUListKey;
import com.androidth.general.common.billing.samsung.identifier.SamsungProductIdentifierFetcherRx;

public interface THSamsungProductIdentifierFetcherRx
    extends SamsungProductIdentifierFetcherRx<
            SamsungSKUListKey,
            SamsungSKU,
            SamsungSKUList>
{
}
