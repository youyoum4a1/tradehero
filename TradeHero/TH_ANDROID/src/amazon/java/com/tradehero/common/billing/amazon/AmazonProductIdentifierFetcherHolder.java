package com.tradehero.common.billing.amazon;

import com.tradehero.common.billing.ProductIdentifierFetcherHolder;
import com.tradehero.common.billing.amazon.BaseAmazonSKUList;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.billing.amazon.exception.AmazonException;

public interface AmazonProductIdentifierFetcherHolder<
        AmazonSKUListKeyType extends AmazonSKUListKey,
        AmazonSKUType extends AmazonSKU,
        AmazonSKUListType extends BaseAmazonSKUList<AmazonSKUType>,
        AmazonExceptionType extends AmazonException>
    extends ProductIdentifierFetcherHolder<
        AmazonSKUListKeyType,
        AmazonSKUType,
        AmazonSKUListType,
        AmazonExceptionType>
{
}
