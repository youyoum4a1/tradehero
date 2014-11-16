package com.tradehero.common.billing.googleplay;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseBillingAvailableTesterHolder;
import com.tradehero.common.billing.BillingAvailableTester;
import com.tradehero.common.billing.googleplay.exception.IABException;
import java.util.HashMap;
import java.util.Map;

abstract public class BaseIABBillingAvailableTesterHolder<
        IABBillingAvailableTesterType extends IABBillingAvailableTester<IABExceptionType>,
        IABExceptionType extends IABException>
        extends BaseBillingAvailableTesterHolder<IABExceptionType>
{
    @NonNull protected final Map<Integer /*requestCode*/, IABBillingAvailableTesterType> billingAvailableTesters;

    //<editor-fold desc="Constructors">
    public BaseIABBillingAvailableTesterHolder()
    {
        super();
        billingAvailableTesters = new HashMap<>();
    }
    //</editor-fold>

    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return super.isUnusedRequestCode(requestCode) &&
                !billingAvailableTesters.containsKey(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        super.forgetRequestCode(requestCode);
        IABBillingAvailableTesterType purchaser = billingAvailableTesters.get(requestCode);
        if (purchaser != null)
        {
            purchaser.setListener(null);
            purchaser.setBillingAvailableListener(null);
        }
        billingAvailableTesters.remove(requestCode);
    }

    @Override public void launchBillingAvailableTestSequence(int requestCode)
    {
        BillingAvailableTester.OnBillingAvailableListener<IABExceptionType> billingAvailableListener = createBillingAvailableListener();
        IABBillingAvailableTesterType billingAvailableTester = createBillingAvailableTester(requestCode);
        billingAvailableTester.setBillingAvailableListener(billingAvailableListener);
        billingAvailableTesters.put(requestCode, billingAvailableTester);
        billingAvailableTester.testBillingAvailable();
    }

    @NonNull abstract protected IABBillingAvailableTesterType createBillingAvailableTester(int requestCode);

    @Override public void onDestroy()
    {
        for (IABBillingAvailableTester billingAvailableTester : billingAvailableTesters.values())
        {
            if (billingAvailableTester != null)
            {
                billingAvailableTester.onDestroy();
            }
        }
        billingAvailableTesters.clear();
        super.onDestroy();
    }
}
