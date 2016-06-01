package com.ayondo.academy.billing.amazon.identifier;

import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUList;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.billing.amazon.identifier.AmazonProductIdentifierFetcherRx;

public interface THAmazonProductIdentifierFetcherRx
    extends AmazonProductIdentifierFetcherRx<
                AmazonSKUListKey,
                AmazonSKU,
                AmazonSKUList>
{
}
