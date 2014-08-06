package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.BaseSamsungPurchaserHolder;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

/**
 * Created by xavier on 3/27/14.
 */
public class THBaseSamsungPurchaserHolder
    extends BaseSamsungPurchaserHolder<
        SamsungSKU,
        THSamsungPurchaseOrder,
        THSamsungOrderId,
        THSamsungPurchase,
        THBaseSamsungPurchaser,
        SamsungException>
    implements THSamsungPurchaserHolder
{
    @Inject protected CurrentActivityHolder currentActivityHolder;

    public THBaseSamsungPurchaserHolder()
    {
        super();
        DaggerUtils.inject(this);
    }

    @Override protected THBaseSamsungPurchaser createPurchaser()
    {
        return new THBaseSamsungPurchaser(currentActivityHolder.getCurrentContext(), THSamsungConstants.PURCHASE_MODE);
    }
}
