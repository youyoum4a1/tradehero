package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.BaseSamsungBillingAvailableTesterHolder;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

/**
 * Created by xavier on 3/27/14.
 */
public class THBaseSamsungBillingAvailableTesterHolder
    extends BaseSamsungBillingAvailableTesterHolder<
        THBaseSamsungBillingAvailableTester,
        SamsungException>
    implements THSamsungBillingAvailableTesterHolder
{
    @Inject protected CurrentActivityHolder currentActivityHolder;

    public THBaseSamsungBillingAvailableTesterHolder()
    {
        super();
        DaggerUtils.inject(this);
    }

    @Override protected THBaseSamsungBillingAvailableTester createProductIdentifierFetcher()
    {
        return new THBaseSamsungBillingAvailableTester(currentActivityHolder.getCurrentContext(), THSamsungConstants.PURCHASE_MODE);
    }
}
