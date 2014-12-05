package com.tradehero.th.billing.amazon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.th.billing.BillingUtils;
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
    @Inject public AmazonStoreUtils()
    {
        super();
    }

    @Override @NonNull public String getStoreName()
    {
        return "Amazon Store";
    }

    @Override @NonNull protected List<String> getPurchaseReportStrings(@Nullable THAmazonPurchase purchase)
    {
        List<String> reported = new ArrayList<>();

        if (purchase != null)
        {
            // TODO
        }
        return reported;
    }
}
