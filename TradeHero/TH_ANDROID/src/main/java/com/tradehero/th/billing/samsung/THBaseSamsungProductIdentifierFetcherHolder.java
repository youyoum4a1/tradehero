package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.BaseSamsungProductIdentifierFetcherHolder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import com.tradehero.common.billing.samsung.exception.SamsungException;

public class THBaseSamsungProductIdentifierFetcherHolder
    extends BaseSamsungProductIdentifierFetcherHolder<
        SamsungSKUListKey,
        SamsungSKU,
        SamsungSKUList,
        THSamsungProductIdentifierFetcher,
        SamsungException>
    implements THSamsungProductIdentifierFetcherHolder
{
    @Override protected THSamsungProductIdentifierFetcher createProductIdentifierFetcher()
    {
        return new THSamsungProductIdentifierFetcher();
    }
}
