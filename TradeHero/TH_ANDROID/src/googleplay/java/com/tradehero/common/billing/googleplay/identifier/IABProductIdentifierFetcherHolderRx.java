package com.tradehero.common.billing.googleplay.identifier;

import com.tradehero.common.billing.googleplay.BaseIABSKUList;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.identifier.ProductIdentifierFetcherHolderRx;

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
