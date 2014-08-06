package com.tradehero.common.billing.samsung;

import com.tradehero.common.billing.BaseBillingAvailableTesterHolder;
import com.tradehero.common.billing.BillingAvailableTester;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xavier on 3/27/14.
 */
abstract public class BaseSamsungBillingAvailableTesterHolder<
        SamsungBillingAvailableTesterType extends SamsungBillingAvailableTester,
        SamsungExceptionType extends SamsungException>
    extends BaseBillingAvailableTesterHolder<SamsungExceptionType>
    implements SamsungBillingAvailableTesterHolder<SamsungExceptionType>
{
    protected Map<Integer /*requestCode*/, SamsungBillingAvailableTesterType> testers;

    public BaseSamsungBillingAvailableTesterHolder()
    {
        super();
        testers = new HashMap<>();
    }

    @Override public void launchBillingAvailableTestSequence(int requestCode)
    {
        BillingAvailableTester.OnBillingAvailableListener<SamsungExceptionType> skuFetchedListener = createBillingAvailableListener();
        SamsungBillingAvailableTesterType tester = createProductIdentifierFetcher();
        tester.setBillingAvailableListener(skuFetchedListener);
        testers.put(requestCode, tester);
        tester.testBillingAvailable(requestCode);
    }

    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return super.isUnusedRequestCode(requestCode) &&
                !testers.containsKey(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        super.forgetRequestCode(requestCode);
        SamsungBillingAvailableTesterType tester = testers.get(requestCode);
        if (tester != null)
        {
            tester.setBillingAvailableListener(null);
        }
        testers.remove(requestCode);
    }

    @Override public void onDestroy()
    {
        for (SamsungBillingAvailableTesterType tester : testers.values())
        {
            if (tester != null)
            {
                tester.setBillingAvailableListener(null);
            }
        }
        testers.clear();

        super.onDestroy();
    }

    abstract protected SamsungBillingAvailableTesterType createProductIdentifierFetcher();
}
