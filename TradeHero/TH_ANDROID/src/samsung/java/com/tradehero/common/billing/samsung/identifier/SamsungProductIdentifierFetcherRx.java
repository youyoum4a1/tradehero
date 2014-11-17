package com.tradehero.common.billing.samsung.identifier;

import com.tradehero.common.billing.identifier.ProductIdentifierFetcherRx;
import com.tradehero.common.billing.samsung.BaseSamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;

public interface SamsungProductIdentifierFetcherRx<
        SamsungSKUListKeyType extends SamsungSKUListKey,
        SamsungSKUType extends SamsungSKU,
        SamsungSKUListType extends BaseSamsungSKUList<SamsungSKUType>>
        extends ProductIdentifierFetcherRx<
        SamsungSKUListKeyType,
        SamsungSKUType,
        SamsungSKUListType>
{
}
