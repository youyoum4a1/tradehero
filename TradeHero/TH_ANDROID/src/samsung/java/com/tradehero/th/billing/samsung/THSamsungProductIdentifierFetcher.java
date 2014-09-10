package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.SamsungProductIdentifierFetcher;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import com.tradehero.common.billing.samsung.exception.SamsungException;

public interface THSamsungProductIdentifierFetcher
    extends SamsungProductIdentifierFetcher<
        SamsungSKUListKey,
        SamsungSKU,
        SamsungSKUList,
        SamsungException>
{
}
