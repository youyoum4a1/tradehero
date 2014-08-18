package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.BaseSamsungPurchaseFetcherHolder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

public class THBaseSamsungPurchaseFetcherHolder
    extends BaseSamsungPurchaseFetcherHolder<
        SamsungSKU,
        THSamsungOrderId,
        THSamsungPurchase,
        THBaseSamsungPurchaseFetcher,
        SamsungException>
    implements THSamsungPurchaseFetcherHolder
{
    @Inject CurrentActivityHolder currentActivityHolder;

    public THBaseSamsungPurchaseFetcherHolder()
    {
        super();
        DaggerUtils.inject(this);
    }

    @Override protected THBaseSamsungPurchaseFetcher createPurchaseFetcher()
    {
        return new THBaseSamsungPurchaseFetcher(currentActivityHolder.getCurrentContext(), THSamsungConstants.PURCHASE_MODE);
    }
}
