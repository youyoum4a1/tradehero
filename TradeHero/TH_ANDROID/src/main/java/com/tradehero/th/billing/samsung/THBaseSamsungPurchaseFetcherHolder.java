package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.BaseSamsungPurchaseFetcherHolder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

/**
 * Created by xavier on 2/24/14.
 */
public class THBaseSamsungPurchaseFetcherHolder
    extends BaseSamsungPurchaseFetcherHolder<
        SamsungSKU,
        THSamsungOrderId,
        THSamsungPurchase,
        THSamsungPurchaseFetcher,
        SamsungException>
    implements THSamsungPurchaseFetcherHolder
{
    @Inject CurrentActivityHolder currentActivityHolder;

    public THBaseSamsungPurchaseFetcherHolder()
    {
        super();
        DaggerUtils.inject(this);
    }

    @Override protected THSamsungPurchaseFetcher createPurchaseFetcher()
    {
        return new THSamsungPurchaseFetcher(currentActivityHolder.getCurrentContext(), THSamsungConstants.PURCHASE_MODE);
    }
}
