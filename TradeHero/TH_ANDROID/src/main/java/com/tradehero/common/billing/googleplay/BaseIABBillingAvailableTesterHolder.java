package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.BaseBillingAvailableTesterHolder;
import com.tradehero.common.billing.BillingAvailableTester;
import com.tradehero.common.billing.googleplay.exception.IABException;
import java.util.HashMap;
import java.util.Map;


public class BaseIABBillingAvailableTesterHolder
    extends BaseBillingAvailableTesterHolder<IABException>
    implements IABBillingAvailableTesterHolder<IABException>
{
    protected Map<Integer /*requestCode*/, IABBillingAvailableTester> billingAvailableTesters;

    public BaseIABBillingAvailableTesterHolder()
    {
        super();
        billingAvailableTesters = new HashMap<>();
    }

    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return super.isUnusedRequestCode(requestCode) &&
                !billingAvailableTesters.containsKey(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        super.forgetRequestCode(requestCode);
        IABBillingAvailableTester purchaser = billingAvailableTesters.get(requestCode);
        if (purchaser != null)
        {
            purchaser.setListener(null);
            purchaser.setBillingAvailableListener(null);
        }
        billingAvailableTesters.remove(requestCode);
    }

    @Override public void launchBillingAvailableTestSequence(int requestCode)
    {
        BillingAvailableTester.OnBillingAvailableListener<IABException> billingAvailableListener = createBillingAvailableListener();
        IABBillingAvailableTester billingAvailableTester = createAvailableTester();
        billingAvailableTester.setBillingAvailableListener(billingAvailableListener);
        billingAvailableTesters.put(requestCode, billingAvailableTester);
        billingAvailableTester.testBillingAvailable(requestCode);
    }

    @Override public void onDestroy()
    {
        for (IABBillingAvailableTester billingAvailableTester: billingAvailableTesters.values())
        {
            if (billingAvailableTester != null)
            {
                billingAvailableTester.onDestroy();
            }
        }
        billingAvailableTesters.clear();
        super.onDestroy();
    }

    protected IABBillingAvailableTester createAvailableTester()
    {
        return new IABBillingAvailableTester();
    }
}
