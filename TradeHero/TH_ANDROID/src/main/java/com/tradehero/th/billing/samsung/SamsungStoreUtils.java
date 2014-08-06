package com.tradehero.th.billing.samsung;

import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.th.billing.BillingUtils;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 10:55 AM To change this template use File | Settings | File Templates. */
public class SamsungStoreUtils
    extends BillingUtils<
        SamsungSKU,
        THSamsungProductDetail,
        THSamsungOrderId,
        THSamsungPurchase>
{
    @Inject public SamsungStoreUtils()
    {
        super();
    }

    @Override public String getStoreName()
    {
        return "Samsung Store";
    }

    @Override protected List<String> getPurchaseReportStrings(THSamsungPurchase purchase)
    {
        List<String> reported = new ArrayList<>();

        if (purchase != null)
        {
            // TODO
        }
        return reported;
    }
}
