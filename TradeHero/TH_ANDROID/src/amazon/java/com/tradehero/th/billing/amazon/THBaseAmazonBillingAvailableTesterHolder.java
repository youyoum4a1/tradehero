package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.BaseAmazonBillingAvailableTesterHolder;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import javax.inject.Inject;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;

public class THBaseAmazonBillingAvailableTesterHolder
    extends BaseAmazonBillingAvailableTesterHolder<
        THAmazonBillingAvailableTester,
        AmazonException>
    implements THAmazonBillingAvailableTesterHolder
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonBillingAvailableTesterHolder(
            @NotNull Provider<THAmazonBillingAvailableTester> thAmazonBillingAvailableTesterProvider)
    {
        super(thAmazonBillingAvailableTesterProvider);
    }
    //</editor-fold>
}
