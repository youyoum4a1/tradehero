package com.tradehero.common.billing.samsung;

import com.tradehero.common.billing.BaseBillingAvailableTesterHolder;
import com.tradehero.common.billing.BillingAvailableTester;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Provider;
import android.support.annotation.NonNull;

abstract public class BaseSamsungBillingAvailableTesterHolder<
        SamsungBillingAvailableTesterType extends SamsungBillingAvailableTester<SamsungExceptionType>,
        SamsungExceptionType extends SamsungException>
    extends BaseBillingAvailableTesterHolder<SamsungExceptionType>
    implements SamsungBillingAvailableTesterHolder<SamsungExceptionType>
{
    @NonNull protected final Provider<SamsungBillingAvailableTesterType> samsungBillingAvailableTesterTypeProvider;
    @NonNull protected final Map<Integer /*requestCode*/, SamsungBillingAvailableTesterType> testers;

    //<editor-fold desc="Constructors">
    public BaseSamsungBillingAvailableTesterHolder(
            @NonNull Provider<SamsungBillingAvailableTesterType> samsungBillingAvailableTesterTypeProvider)
    {
        super();
        this.samsungBillingAvailableTesterTypeProvider = samsungBillingAvailableTesterTypeProvider;
        testers = new HashMap<>();
    }
    //</editor-fold>

    @Override public void launchBillingAvailableTestSequence(int requestCode)
    {
        BillingAvailableTester.OnBillingAvailableListener<SamsungExceptionType> skuFetchedListener = createBillingAvailableListener();
        SamsungBillingAvailableTesterType tester = samsungBillingAvailableTesterTypeProvider.get();
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
}
