package com.androidth.general.billing.googleplay.identifier;

import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.IABSKUList;
import com.androidth.general.common.billing.googleplay.IABSKUListKey;
import com.androidth.general.common.billing.googleplay.identifier.IABProductIdentifierFetcherRx;

public interface THIABProductIdentifierFetcherRx
        extends IABProductIdentifierFetcherRx<
        IABSKUListKey,
        IABSKU,
        IABSKUList>
{
}
