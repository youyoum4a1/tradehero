package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.BaseAmazonBillingAvailableTesterHolder;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import javax.inject.Inject;
import javax.inject.Provider;
import android.support.annotation.NonNull;

public class THBaseAmazonBillingAvailableTesterHolder
    extends BaseAmazonBillingAvailableTesterHolder<
        THAmazonBillingAvailableTester,
        AmazonException>
    implements THAmazonBillingAvailableTesterHolder
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonBillingAvailableTesterHolder(
            @NonNull Provider<THAmazonBillingAvailableTester> thAmazonBillingAvailableTesterProvider)
    {
        super(thAmazonBillingAvailableTesterProvider);
    }
    //</editor-fold>
}
