package com.tradehero.th.billing.googleplay;

import android.content.Context;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.googleplay.BaseIABInventoryFetcherHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;

/**
 * Created by xavier on 2/24/14.
 */
public class THBaseIABInventoryFetcherHolder
    extends BaseIABInventoryFetcherHolder<
        IABSKU,
        THIABProductDetail,
        THIABBillingInventoryFetcher,
        BillingInventoryFetcher.OnInventoryFetchedListener<IABSKU, THIABProductDetail, IABException>>
    implements THIABInventoryFetcherHolder
{
    protected Context context;

    public THBaseIABInventoryFetcherHolder(Context context)
    {
        super();
        this.context = context;
    }

    @Override protected THIABBillingInventoryFetcher createInventoryFetcher()
    {
        return new THIABBillingInventoryFetcher(context);
    }
}
