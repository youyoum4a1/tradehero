package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.ProductIdentifierFetcher;
import com.tradehero.common.billing.googleplay.BaseIABProductIdentifierFetcherHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;

/**
 * Created by xavier on 2/24/14.
 */
public class THBaseIABProductIdentifierFetcherHolder
    extends BaseIABProductIdentifierFetcherHolder<
            IABSKU,
            THIABProductIdentifierFetcher,
            ProductIdentifierFetcher.OnProductIdentifierFetchedListener<
            IABSKU,
            IABException>,
            IABException>
    implements THIABProductIdentifierFetcherHolder
{
    @Override protected THIABProductIdentifierFetcher createProductIdentifierFetcher()
    {
        return new THIABProductIdentifierFetcher();
    }
}
