package com.tradehero.common.billing.amazon.identifier;

import com.amazon.device.iap.PurchasingListener;
import com.tradehero.common.billing.amazon.AmazonActor;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.billing.amazon.BaseAmazonSKUList;
import com.tradehero.common.billing.identifier.ProductIdentifierFetcherRx;

public interface AmazonProductIdentifierFetcherRx<
        AmazonSKUListKeyType extends AmazonSKUListKey,
        AmazonSKUType extends AmazonSKU,
        AmazonSKUListType extends BaseAmazonSKUList<AmazonSKUType>>
    extends ProductIdentifierFetcherRx<
            AmazonSKUListKeyType,
            AmazonSKUType,
            AmazonSKUListType>,
        AmazonActor, PurchasingListener
{
}
