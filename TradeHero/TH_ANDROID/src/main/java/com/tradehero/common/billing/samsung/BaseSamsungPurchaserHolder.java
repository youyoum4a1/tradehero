package com.tradehero.common.billing.samsung;

import com.tradehero.common.billing.BaseBillingPurchaserHolder;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xavier on 2/24/14.
 */
abstract public class BaseSamsungPurchaserHolder<
        SamsungSKUType extends SamsungSKU,
        SamsungPurchaseOrderType extends SamsungPurchaseOrder<SamsungSKUType>,
        SamsungOrderIdType extends SamsungOrderId,
        SamsungPurchaseType extends SamsungPurchase<SamsungSKUType, SamsungOrderIdType>,
        SamsungPurchaserType extends BaseSamsungPurchaser<
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
    protected Map<Integer /*requestCode*/, SamsungPurchaserType> purchasers;

    public BaseSamsungPurchaserHolder()
    {
        super();
        purchasers = new HashMap<>();
    }

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
        SamsungPurchaserType iabPurchaser = createPurchaser();
        iabPurchaser.setPurchaseFinishedListener(purchaseListener);
        purchasers.put(requestCode, iabPurchaser);
        iabPurchaser.purchase(requestCode, purchaseOrder);
    }

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

    abstract protected SamsungPurchaserType createPurchaser();
}
