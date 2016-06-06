package com.androidth.general.common.billing.samsung.identifier;

import com.androidth.general.common.billing.identifier.ProductIdentifierFetcherRx;
import com.androidth.general.common.billing.samsung.BaseSamsungSKUList;
import com.androidth.general.common.billing.samsung.SamsungSKU;
import com.androidth.general.common.billing.samsung.SamsungSKUListKey;

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
