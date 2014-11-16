package com.tradehero.th.billing.amazon.tester;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.tester.BaseAmazonBillingAvailableTesterHolderRx;
import javax.inject.Inject;

public class THBaseAmazonBillingAvailableTesterHolderRx
    extends BaseAmazonBillingAvailableTesterHolderRx
    implements THAmazonBillingAvailableTesterHolderRx
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonBillingAvailableTesterHolderRx()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override protected THBaseAmazonBillingAvailableTesterRx createTester(int requestCode)
    {
        return new THBaseAmazonBillingAvailableTesterRx(requestCode);
    }
}
