package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.BaseSamsungInventoryFetcherHolder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

/**
 * Created by xavier on 3/27/14.
 */
public class THBaseSamsungInventoryFetcherHolder
    extends BaseSamsungInventoryFetcherHolder<
        SamsungSKU,
        THSamsungProductDetail,
        THBaseSamsungInventoryFetcher,
        SamsungException>
    implements THSamsungInventoryFetcherHolder
{
    @Inject CurrentActivityHolder currentActivityHolder;

    public THBaseSamsungInventoryFetcherHolder()
    {
        super();
        DaggerUtils.inject(this);
    }

    @Override protected THBaseSamsungInventoryFetcher createProductIdentifierFetcher()
    {
        return new THBaseSamsungInventoryFetcher(currentActivityHolder.getCurrentContext(), THSamsungConstants.PURCHASE_MODE);
    }
}
