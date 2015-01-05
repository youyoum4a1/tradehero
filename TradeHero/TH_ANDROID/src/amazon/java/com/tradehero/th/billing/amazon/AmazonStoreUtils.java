package com.tradehero.th.billing.amazon;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.th.billing.BillingUtils;
import com.tradehero.th.utils.VersionUtils;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class AmazonStoreUtils
    extends BillingUtils<
        AmazonSKU,
        THAmazonProductDetail,
        THAmazonOrderId,
        THAmazonPurchase>
{
    @Inject public AmazonStoreUtils(@NonNull VersionUtils versionUtils)
    {
        super(versionUtils);
    }

    @Override public String getStoreName()
    {
        return "Amazon Store";
    }

    @Override protected List<String> getPurchaseReportStrings(THAmazonPurchase purchase)
    {
        List<String> reported = new ArrayList<>();

        if (purchase != null)
        {
            // TODO
        }
        return reported;
    }
}
