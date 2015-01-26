package com.tradehero.common.billing.amazon.purchase;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.amazon.AmazonOrderId;
import com.tradehero.common.billing.amazon.AmazonPurchase;
import com.tradehero.common.billing.amazon.AmazonPurchaseOrder;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.purchase.BaseBillingPurchaserHolderRx;

abstract public class BaseAmazonPurchaserHolderRx<
        AmazonSKUType extends AmazonSKU,
        AmazonPurchaseOrderType extends AmazonPurchaseOrder<AmazonSKUType>,
        AmazonOrderIdType extends AmazonOrderId,
        AmazonPurchaseType extends AmazonPurchase<AmazonSKUType, AmazonOrderIdType>>
    extends BaseBillingPurchaserHolderRx<
            AmazonSKUType,
            AmazonPurchaseOrderType,
            AmazonOrderIdType,
            AmazonPurchaseType>
    implements AmazonPurchaserHolderRx<
            AmazonSKUType,
            AmazonPurchaseOrderType,
            AmazonOrderIdType,
            AmazonPurchaseType>
{
    //<editor-fold desc="Constructors">
    public BaseAmazonPurchaserHolderRx()
    {
        super();
    }
    //</editor-fold>

    @NonNull @Override abstract protected AmazonPurchaserRx<AmazonSKUType, AmazonPurchaseOrderType, AmazonOrderIdType, AmazonPurchaseType> createPurchaser(
            int requestCode,
            @NonNull AmazonPurchaseOrderType purchaseOrder);
}
