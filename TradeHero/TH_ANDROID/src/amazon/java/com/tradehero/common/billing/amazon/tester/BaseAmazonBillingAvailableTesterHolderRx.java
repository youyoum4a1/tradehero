package com.tradehero.common.billing.amazon.tester;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.tester.BaseBillingAvailableTesterHolderRx;

abstract public class BaseAmazonBillingAvailableTesterHolderRx
        extends BaseBillingAvailableTesterHolderRx
        implements AmazonBillingAvailableTesterHolderRx
{
    //<editor-fold desc="Constructors">
    public BaseAmazonBillingAvailableTesterHolderRx()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override abstract protected AmazonBillingAvailableTesterRx createTester(int requestCode);
}
