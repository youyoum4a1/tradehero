package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.googleplay.IABInventoryFetcherHolder;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exception.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface THIABInventoryFetcherHolder extends IABInventoryFetcherHolder<
        IABSKU,
        THIABProductDetail,
        BillingInventoryFetcher.OnInventoryFetchedListener<IABSKU, THIABProductDetail, IABException>,
        IABException>
{
}
