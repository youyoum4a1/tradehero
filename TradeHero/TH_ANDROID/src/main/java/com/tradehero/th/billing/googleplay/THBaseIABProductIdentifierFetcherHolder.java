package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABProductIdentifierFetcherHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.exception.IABException;

public class THBaseIABProductIdentifierFetcherHolder
    extends BaseIABProductIdentifierFetcherHolder<
        IABSKUListKey,
        IABSKU,
        IABSKUList,
        THBaseIABProductIdentifierFetcher,
        IABException>
    implements THIABProductIdentifierFetcherHolder
{
    @Override protected THBaseIABProductIdentifierFetcher createProductIdentifierFetcher()
    {
        return new THBaseIABProductIdentifierFetcher();
    }
}
