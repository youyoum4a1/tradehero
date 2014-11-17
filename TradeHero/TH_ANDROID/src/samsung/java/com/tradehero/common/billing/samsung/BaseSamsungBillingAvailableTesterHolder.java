package com.tradehero.common.billing.samsung;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseBillingAvailableTesterHolder;
import com.tradehero.common.billing.BillingAvailableTester;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import java.util.HashMap;
import java.util.Map;

abstract public class BaseSamsungBillingAvailableTesterHolder<
        SamsungBillingAvailableTesterType extends SamsungBillingAvailableTester<SamsungExceptionType>,
        SamsungExceptionType extends SamsungException>
    extends BaseBillingAvailableTesterHolder<SamsungExceptionType>
    implements SamsungBillingAvailableTesterHolder<SamsungExceptionType>
{
    @NonNull protected final Map<Integer /*requestCode*/, SamsungBillingAvailableTesterType> testers;

    //<editor-fold desc="Constructors">
    public BaseSamsungBillingAvailableTesterHolder()
    {
        super();
        testers = new HashMap<>();
    }
    //</editor-fold>

    @Override public void launchBillingAvailableTestSequence(int requestCode)
    {
        BillingAvailableTester.OnBillingAvailableListener<SamsungExceptionType> skuFetchedListener = createBillingAvailableListener();
        SamsungBillingAvailableTesterType tester = createBillingTester(requestCode);
        tester.setBillingAvailableListener(skuFetchedListener);
        testers.put(requestCode, tester);
        tester.testBillingAvailable();
    }

    @NonNull protected abstract SamsungBillingAvailableTesterType createBillingTester(int requestCode);

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
}
