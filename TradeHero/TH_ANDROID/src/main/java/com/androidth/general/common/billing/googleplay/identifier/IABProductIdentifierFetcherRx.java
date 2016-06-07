package com.androidth.general.common.billing.googleplay.identifier;

import com.androidth.general.common.billing.BaseProductIdentifierList;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.IABSKUListKey;
import com.androidth.general.common.billing.identifier.ProductIdentifierFetcherRx;

public interface IABProductIdentifierFetcherRx<
        IABSKUListKeyType extends IABSKUListKey,
        IABSKUType extends IABSKU,
        IABSKUListType extends BaseProductIdentifierList<IABSKUType>>
        extends ProductIdentifierFetcherRx<
        IABSKUListKeyType,
        IABSKUType,
        IABSKUListType>
{
}
