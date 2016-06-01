package com.ayondo.academy.billing.googleplay.identifier;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.identifier.IABProductIdentifierFetcherRx;

public interface THIABProductIdentifierFetcherRx
        extends IABProductIdentifierFetcherRx<
        IABSKUListKey,
        IABSKU,
        IABSKUList>
{
}
