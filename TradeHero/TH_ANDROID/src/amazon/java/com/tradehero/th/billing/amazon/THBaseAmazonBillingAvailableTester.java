package com.tradehero.th.billing.amazon;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.BaseAmazonBillingAvailableTester;
import com.tradehero.common.billing.amazon.exception.AmazonBillingNotAvailableException;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingService;
import javax.inject.Inject;

public class THBaseAmazonBillingAvailableTester
    extends BaseAmazonBillingAvailableTester<AmazonException>
    implements THAmazonBillingAvailableTester
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonBillingAvailableTester(
            int request,
            @NonNull AmazonPurchasingService purchasingService)
    {
        super(request, purchasingService);
    }
    //</editor-fold>

    @Override protected AmazonException createNotAvailable(Exception cause)
    {
        return new AmazonBillingNotAvailableException(cause);
    }
}
