package com.tradehero.th.billing.amazon;

import com.tradehero.common.billing.amazon.AmazonProductIdentifierFetcherHolder;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUList;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.billing.amazon.exception.AmazonException;

public interface THAmazonProductIdentifierFetcherHolder
    extends AmazonProductIdentifierFetcherHolder<
            AmazonSKUListKey,
            AmazonSKU,
            AmazonSKUList,
            AmazonException>
{
}
