package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonProductIdentifierFetcher;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUList;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.billing.amazon.exception.AmazonException;

public interface THAmazonProductIdentifierFetcher
    extends AmazonProductIdentifierFetcher<
            AmazonSKUListKey,
            AmazonSKU,
            AmazonSKUList,
            AmazonException>
{
}
