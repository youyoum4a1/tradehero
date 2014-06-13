package com.tradehero.common.billing.samsung;

import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.samsung.exception.SamsungException;

public interface SamsungProductIdentifierFetcher<
        SamsungSKUListKeyType extends SamsungSKUListKey,
        SamsungSKUType extends SamsungSKU,
        SamsungSKUListType extends BaseSamsungSKUList<SamsungSKUType>,
        SamsungExceptionType extends SamsungException>
    extends ProductIdentifierFetcher<
        SamsungSKUListKeyType,
        SamsungSKUType,
        SamsungSKUListType,
        SamsungExceptionType>
{
}
