package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABProductIdentifierFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.exception.IABException;

/**
 * Created by xavier on 3/27/14.
 */
public interface THIABProductIdentifierFetcher
    extends IABProductIdentifierFetcher<
        IABSKUListKey,
        IABSKU,
        IABSKUList,
        IABException>
{
}
