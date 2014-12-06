package com.tradehero.common.billing.samsung;

import android.content.Intent;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.BaseBillingPurchaserHolder;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import java.util.HashMap;
import java.util.Map;

abstract public class BaseSamsungPurchaserHolder<
        SamsungSKUType extends SamsungSKU,
        SamsungPurchaseOrderType extends SamsungPurchaseOrder<SamsungSKUType>,
        SamsungOrderIdType extends SamsungOrderId,
        SamsungPurchaseType extends SamsungPurchase<SamsungSKUType, SamsungOrderIdType>,
        SamsungPurchaserType extends SamsungPurchaser<
                        SamsungSKUType,
                        SamsungPurchaseOrderType,
                        SamsungOrderIdType,
                        SamsungPurchaseType,
                        SamsungExceptionType>,
        SamsungExceptionType extends SamsungException>
    extends BaseBillingPurchaserHolder<
        SamsungSKUType,
        SamsungPurchaseOrderType,
        SamsungOrderIdType,
        SamsungPurchaseType,
        SamsungExceptionType>
    implements SamsungPurchaserHolder<
        SamsungSKUType,
        SamsungPurchaseOrderType,
        SamsungOrderIdType,
        SamsungPurchaseType,
        SamsungExceptionType>
{
    @NonNull protected final Map<Integer /*requestCode*/, SamsungPurchaserType> purchasers;

    //<editor-fold desc="Constructors">
    public BaseSamsungPurchaserHolder()
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
        SamsungPurchaserType purchaser = purchasers.get(requestCode);
        if (purchaser != null)
        {
            purchaser.setPurchaseFinishedListener(null);
        }
        purchasers.remove(requestCode);
    }

    @Override public void launchPurchaseSequence(int requestCode, SamsungPurchaseOrderType purchaseOrder)
    {
        BillingPurchaser.OnPurchaseFinishedListener<SamsungSKUType, SamsungPurchaseOrderType, SamsungOrderIdType, SamsungPurchaseType, SamsungExceptionType> purchaseListener = createPurchaseFinishedListener();
        SamsungPurchaserType iabPurchaser = createPurchaser(requestCode);
        iabPurchaser.setPurchaseFinishedListener(purchaseListener);
        purchasers.put(requestCode, iabPurchaser);
        iabPurchaser.purchase(purchaseOrder);
    }

    @NonNull protected abstract SamsungPurchaserType createPurchaser(int requestCode);

    @Override public void onDestroy()
    {
        for (SamsungPurchaserType iabPurchaser: purchasers.values())
        {
            if (iabPurchaser != null)
            {
                iabPurchaser.setPurchaseFinishedListener(null);
            }
        }
        purchasers.clear();
        super.onDestroy();
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Nothing to do
    }
}
