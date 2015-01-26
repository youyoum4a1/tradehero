package com.tradehero.common.billing.amazon;

import com.tradehero.common.billing.BillingInteractorRx;
import com.tradehero.common.billing.BillingLogicHolderRx;

public interface AmazonInteractorRx<
        AmazonSKUListKeyType extends AmazonSKUListKey,
        AmazonSKUType extends AmazonSKU,
        AmazonSKUListType extends BaseAmazonSKUList<AmazonSKUType>,
        AmazonProductDetailType extends AmazonProductDetail<AmazonSKUType>,
        AmazonPurchaseOrderType extends AmazonPurchaseOrder<AmazonSKUType>,
        AmazonOrderIdType extends AmazonOrderId,
        AmazonPurchaseType extends AmazonPurchase<
                AmazonSKUType,
                AmazonOrderIdType>,
        AmazonActorType extends BillingLogicHolderRx<
                        AmazonSKUListKeyType,
                        AmazonSKUType,
                        AmazonSKUListType,
                        AmazonProductDetailType,
                        AmazonPurchaseOrderType,
                        AmazonOrderIdType,
                        AmazonPurchaseType>>
        extends BillingInteractorRx<
                AmazonSKUListKeyType,
                AmazonSKUType,
                AmazonSKUListType,
                AmazonProductDetailType,
                AmazonPurchaseOrderType,
                AmazonOrderIdType,
                AmazonPurchaseType,
                AmazonActorType>
{
}
