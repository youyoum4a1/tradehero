package com.androidth.general.common.billing.samsung.identifier;

import com.androidth.general.common.billing.identifier.ProductIdentifierFetcherHolderRx;
import com.androidth.general.common.billing.samsung.BaseSamsungSKUList;
import com.androidth.general.common.billing.samsung.SamsungSKU;
import com.androidth.general.common.billing.samsung.SamsungSKUListKey;

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
