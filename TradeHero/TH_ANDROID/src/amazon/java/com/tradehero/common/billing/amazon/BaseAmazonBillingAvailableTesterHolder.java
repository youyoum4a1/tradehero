package com.tradehero.common.billing.amazon;

import com.tradehero.common.billing.BaseBillingAvailableTesterHolder;
import com.tradehero.common.billing.BillingAvailableTester;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;

abstract public class BaseAmazonBillingAvailableTesterHolder<
        AmazonBillingAvailableTesterType extends AmazonBillingAvailableTester<AmazonExceptionType>,
        AmazonExceptionType extends AmazonException>
    extends BaseBillingAvailableTesterHolder<AmazonExceptionType>
    implements AmazonBillingAvailableTesterHolder<AmazonExceptionType>
{
    @NotNull protected final Provider<AmazonBillingAvailableTesterType> amazonBillingAvailableTesterTypeProvider;
    @NotNull protected final Map<Integer /*requestCode*/, AmazonBillingAvailableTesterType> testers;

    //<editor-fold desc="Constructors">
    public BaseAmazonBillingAvailableTesterHolder(
            @NotNull Provider<AmazonBillingAvailableTesterType> amazonBillingAvailableTesterTypeProvider)
    {
        super();
        this.amazonBillingAvailableTesterTypeProvider = amazonBillingAvailableTesterTypeProvider;
        testers = new HashMap<>();
    }
    //</editor-fold>

    @Override public void launchBillingAvailableTestSequence(int requestCode)
    {
        BillingAvailableTester.OnBillingAvailableListener<AmazonExceptionType> skuFetchedListener = createBillingAvailableListener();
        AmazonBillingAvailableTesterType tester = amazonBillingAvailableTesterTypeProvider.get();
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
        AmazonBillingAvailableTesterType tester = testers.get(requestCode);
        if (tester != null)
        {
            tester.setBillingAvailableListener(null);
        }
        testers.remove(requestCode);
    }

    @Override public void onDestroy()
    {
        for (AmazonBillingAvailableTesterType tester : testers.values())
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
