package com.tradehero.common.billing.samsung.identifier;

import com.tradehero.common.billing.identifier.ProductIdentifierFetcherHolderRx;
import com.tradehero.common.billing.samsung.BaseSamsungSKUList;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.SamsungSKUListKey;

public interface SamsungProductIdentifierFetcherHolderRx<
        SamsungSKUListKeyType extends SamsungSKUListKey,
        SamsungSKUType extends SamsungSKU,
        SamsungSKUListType extends BaseSamsungSKUList<SamsungSKUType>>
        extends ProductIdentifierFetcherHolderRx<
        SamsungSKUListKeyType,
        SamsungSKUType,
        SamsungSKUListType>
{
}
