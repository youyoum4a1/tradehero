package com.tradehero.common.billing.amazon;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseBillingPurchaserHolder;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import java.util.HashMap;
import java.util.Map;

abstract public class BaseAmazonPurchaserHolder<
        AmazonSKUType extends AmazonSKU,
        AmazonPurchaseOrderType extends AmazonPurchaseOrder<AmazonSKUType>,
        AmazonOrderIdType extends AmazonOrderId,
        AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>,
        AmazonPurchaserType extends AmazonPurchaser<
                        AmazonSKUType,
                        AmazonPurchaseOrderType,
                        AmazonOrderIdType,
                        AmazonPurchaseType,
                        AmazonExceptionType>,
        AmazonExceptionType extends AmazonException>
    extends BaseBillingPurchaserHolder<
        AmazonSKUType,
        AmazonPurchaseOrderType,
        AmazonOrderIdType,
        AmazonPurchaseType,
        AmazonExceptionType>
    implements AmazonPurchaserHolder<
            AmazonSKUType,
            AmazonPurchaseOrderType,
            AmazonOrderIdType,
            AmazonPurchaseType,
            AmazonExceptionType>
{
    @NonNull protected final Map<Integer /*requestCode*/, AmazonPurchaserType> purchasers;

    //<editor-fold desc="Constructors">
    public BaseAmazonPurchaserHolder()
    {
        super();
        purchasers = new HashMap<>();
    }
    //</editor-fold>

    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return super.isUnusedRequestCode(requestCode) &&
                !purchasers.containsKey(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        super.forgetRequestCode(requestCode);
        AmazonPurchaserType purchaser = purchasers.get(requestCode);
        if (purchaser != null)
        {
            purchaser.setPurchaseFinishedListener(null);
        }
        purchasers.remove(requestCode);
    }

    @Override public void launchPurchaseSequence(int requestCode, AmazonPurchaseOrderType purchaseOrder)
    {
        BillingPurchaser.OnPurchaseFinishedListener<AmazonSKUType, AmazonPurchaseOrderType, AmazonOrderIdType, AmazonPurchaseType, AmazonExceptionType> purchaseListener = createPurchaseFinishedListener();
        AmazonPurchaserType iabPurchaser = createPurchaser(requestCode);
        iabPurchaser.setPurchaseFinishedListener(purchaseListener);
        purchasers.put(requestCode, iabPurchaser);
        iabPurchaser.purchase(purchaseOrder);
    }

    @NonNull protected abstract AmazonPurchaserType createPurchaser(int requestCode);

    @Override public void onDestroy()
    {
        for (AmazonPurchaserType purchaser: purchasers.values())
        {
            if (purchaser != null)
            {
                purchaser.onDestroy();
            }
        }
        purchasers.clear();
        super.onDestroy();
    }
}
