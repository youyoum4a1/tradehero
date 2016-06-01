package com.ayondo.academy.billing.googleplay.identifier;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.identifier.IABProductIdentifierFetcherHolderRx;

public interface THIABProductIdentifierFetcherHolderRx
        extends IABProductIdentifierFetcherHolderRx<
        IABSKUListKey,
        IABSKU,
        IABSKUList>
{
}
