package com.tradehero.th.billing.amazon;

import android.content.Context;
import com.tradehero.common.billing.amazon.AmazonPurchasingService;
import com.tradehero.common.billing.amazon.BaseAmazonBillingAvailableTester;
import com.tradehero.common.billing.amazon.exception.AmazonBillingNotAvailableException;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import javax.inject.Inject;
import android.support.annotation.NonNull;

public class THBaseAmazonBillingAvailableTester
    extends BaseAmazonBillingAvailableTester<AmazonException>
    implements THAmazonBillingAvailableTester
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonBillingAvailableTester(
            @NonNull Context context,
            @NonNull AmazonPurchasingService purchasingService)
    {
        super(context, purchasingService);
    }
    //</editor-fold>

    @Override protected AmazonException createNotAvailable(Exception cause)
    {
        return new AmazonBillingNotAvailableException(cause);
    }
}
