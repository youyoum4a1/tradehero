package com.tradehero.common.billing.samsung;

import com.tradehero.common.billing.BaseBillingPurchaserHolder;
import com.tradehero.common.billing.BillingPurchaser;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Provider;
import org.jetbrains.annotations.NotNull;

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
    @NotNull protected final Provider<SamsungPurchaserType> samsungPurchaserTypeProvider;
    @NotNull protected final Map<Integer /*requestCode*/, SamsungPurchaserType> purchasers;

    //<editor-fold desc="Constructors">
    public BaseSamsungPurchaserHolder(@NotNull Provider<SamsungPurchaserType> samsungPurchaserTypeProvider)
    {
        super();
        this.samsungPurchaserTypeProvider = samsungPurchaserTypeProvider;
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
        SamsungPurchaserType iabPurchaser = samsungPurchaserTypeProvider.get();
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
}
