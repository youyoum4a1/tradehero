package com.tradehero.common.billing.amazon.identifier;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.billing.amazon.BaseAmazonSKUList;
import com.tradehero.common.billing.identifier.ProductIdentifierFetcherHolderRx;

public interface AmazonProductIdentifierFetcherHolderRx<
        AmazonSKUListKeyType extends AmazonSKUListKey,
        AmazonSKUType extends AmazonSKU,
        AmazonSKUListType extends BaseAmazonSKUList<AmazonSKUType>>
    extends ProductIdentifierFetcherHolderRx<
            AmazonSKUListKeyType,
            AmazonSKUType,
            AmazonSKUListType>
{
}
