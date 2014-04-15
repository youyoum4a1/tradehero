package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABProductIdentifierFetcherHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.exception.IABException;

/**
 * Created by xavier on 2/24/14.
 */
public class THBaseIABProductIdentifierFetcherHolder
    extends BaseIABProductIdentifierFetcherHolder<
        IABSKUListKey,
        IABSKU,
        IABSKUList,
        THIABProductIdentifierFetcher,
        IABException>
    implements THIABProductIdentifierFetcherHolder
{
    @Override protected THIABProductIdentifierFetcher createProductIdentifierFetcher()
    {
        return new THIABProductIdentifierFetcher();
    }
}
