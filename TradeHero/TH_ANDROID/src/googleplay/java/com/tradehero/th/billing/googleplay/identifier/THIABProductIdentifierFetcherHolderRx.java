package com.androidth.general.billing.googleplay.identifier;

import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.IABSKUList;
import com.androidth.general.common.billing.googleplay.IABSKUListKey;
import com.androidth.general.common.billing.googleplay.identifier.IABProductIdentifierFetcherHolderRx;

public interface THIABProductIdentifierFetcherHolderRx
        extends IABProductIdentifierFetcherHolderRx<
        IABSKUListKey,
        IABSKU,
        IABSKUList>
{
}
