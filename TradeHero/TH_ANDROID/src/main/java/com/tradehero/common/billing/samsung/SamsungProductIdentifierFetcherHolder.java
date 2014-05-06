package com.tradehero.common.billing.samsung;

import com.tradehero.common.billing.ProductIdentifierFetcherHolder;
import com.tradehero.common.billing.samsung.exception.SamsungException;


public interface SamsungProductIdentifierFetcherHolder<
        SamsungSKUListKeyType extends SamsungSKUListKey,
        SamsungSKUType extends SamsungSKU,
        SamsungSKUListType extends BaseSamsungSKUList<SamsungSKUType>,
        SamsungExceptionType extends SamsungException>
    extends ProductIdentifierFetcherHolder<
        SamsungSKUListKeyType,
        SamsungSKUType,
        SamsungSKUListType,
        SamsungExceptionType>
{
}
