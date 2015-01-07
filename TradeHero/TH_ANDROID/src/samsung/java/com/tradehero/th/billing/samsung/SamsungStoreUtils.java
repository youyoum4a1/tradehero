package com.tradehero.th.billing.samsung;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.th.billing.BillingUtils;
import com.tradehero.th.utils.VersionUtils;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class SamsungStoreUtils
    extends BillingUtils<
        SamsungSKU,
        THSamsungProductDetail,
        THSamsungOrderId,
        THSamsungPurchase>
{
    @Inject public SamsungStoreUtils(@NonNull VersionUtils versionUtils)
    {
        super(versionUtils);
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
