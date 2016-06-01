package com.ayondo.academy.billing.amazon.tester;

import android.app.Activity;
import android.content.Intent;
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

    @Override public void onActivityResult(@NonNull Activity activity, int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }
}
