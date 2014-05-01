package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABInventoryFetcherHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;

public class THBaseIABInventoryFetcherHolder
    extends BaseIABInventoryFetcherHolder<
        IABSKUListKey,
        IABSKU,
        IABSKUList,
        THIABProductDetail,
        THIABBillingInventoryFetcher>
    implements THIABInventoryFetcherHolder
{
    public THBaseIABInventoryFetcherHolder()
    {
        super();
    }

    @Override protected THIABBillingInventoryFetcher createInventoryFetcher()
    {
        return new THIABBillingInventoryFetcher();
    }
}
