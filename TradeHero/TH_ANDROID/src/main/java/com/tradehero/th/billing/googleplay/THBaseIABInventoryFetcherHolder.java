package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABInventoryFetcherHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;

public class THBaseIABInventoryFetcherHolder
    extends BaseIABInventoryFetcherHolder<
        IABSKU,
        THIABProductDetail,
        THBaseIABInventoryFetcher>
    implements THIABInventoryFetcherHolder
{
    //<editor-fold desc="Constructors">
    public THBaseIABInventoryFetcherHolder()
    {
        super();
    }
    //</editor-fold>

    @Override protected THBaseIABInventoryFetcher createInventoryFetcher()
    {
        return new THBaseIABInventoryFetcher();
    }
}
