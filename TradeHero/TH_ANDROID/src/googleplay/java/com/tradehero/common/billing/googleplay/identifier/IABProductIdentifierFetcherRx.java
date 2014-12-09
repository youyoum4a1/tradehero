package com.tradehero.common.billing.googleplay.identifier;

import com.tradehero.common.billing.BaseProductIdentifierList;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.identifier.ProductIdentifierFetcherRx;

public interface IABProductIdentifierFetcherRx<
        IABSKUListKeyType extends IABSKUListKey,
        IABSKUType extends IABSKU,
        IABSKUListType extends BaseProductIdentifierList<IABSKUType>>
        extends ProductIdentifierFetcherRx<
        IABSKUListKeyType,
        IABSKUType,
        IABSKUListType>
{
    void onDestroy();
}
