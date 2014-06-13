package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.BaseSamsungProductIdentifierFetcherHolder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

public class THBaseSamsungProductIdentifierFetcherHolder
    extends BaseSamsungProductIdentifierFetcherHolder<
        SamsungSKUListKey,
        SamsungSKU,
        SamsungSKUList,
        THBaseSamsungProductIdentifierFetcher,
        SamsungException>
    implements THSamsungProductIdentifierFetcherHolder
{
    @Inject CurrentActivityHolder currentActivityHolder;

    public THBaseSamsungProductIdentifierFetcherHolder()
    {
        super();
        DaggerUtils.inject(this);
    }

    @Override protected THBaseSamsungProductIdentifierFetcher createProductIdentifierFetcher()
    {
        return new THBaseSamsungProductIdentifierFetcher(currentActivityHolder.getCurrentContext(), THSamsungConstants.PURCHASE_MODE);
    }
}
