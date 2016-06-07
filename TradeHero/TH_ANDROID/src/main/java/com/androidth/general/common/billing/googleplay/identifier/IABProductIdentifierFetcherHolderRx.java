package com.androidth.general.common.billing.googleplay.identifier;

import com.androidth.general.common.billing.googleplay.BaseIABSKUList;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.IABSKUListKey;
import com.androidth.general.common.billing.identifier.ProductIdentifierFetcherHolderRx;

public interface IABProductIdentifierFetcherHolderRx<
        IABSKUListKeyType extends IABSKUListKey,
        IABSKUType extends IABSKU,
        IABSKUListType extends BaseIABSKUList<IABSKUType>>
        extends ProductIdentifierFetcherHolderRx<
        IABSKUListKeyType,
        IABSKUType,
        IABSKUListType>
{
}
