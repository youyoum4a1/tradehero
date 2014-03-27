package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABInventoryFetcherHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;

/**
 * Created by xavier on 2/24/14.
 */
public class THBaseIABInventoryFetcherHolder
    extends BaseIABInventoryFetcherHolder<
        IABSKU,
        THIABProductDetail,
        THBaseIABInventoryFetcher>
    implements THIABInventoryFetcherHolder
{
    public THBaseIABInventoryFetcherHolder()
    {
        super();
    }

    @Override protected THBaseIABInventoryFetcher createInventoryFetcher()
    {
        return new THBaseIABInventoryFetcher();
    }
}
