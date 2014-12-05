package com.tradehero.th.billing.amazon;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.BaseAmazonBillingAvailableTesterHolder;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingService;
import javax.inject.Inject;

public class THBaseAmazonBillingAvailableTesterHolder
    extends BaseAmazonBillingAvailableTesterHolder<
        THAmazonBillingAvailableTester,
        AmazonException>
    implements THAmazonBillingAvailableTesterHolder
{
    @NonNull protected final AmazonPurchasingService purchasingService;

    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonBillingAvailableTesterHolder(
            @NonNull AmazonPurchasingService purchasingService)
    {
        super();
        this.purchasingService = purchasingService;
    }
    //</editor-fold>

    @NonNull @Override protected THAmazonBillingAvailableTester createTester(int requestCode)
    {
        return new THBaseAmazonBillingAvailableTester(
                requestCode,
                purchasingService);
    }
}
