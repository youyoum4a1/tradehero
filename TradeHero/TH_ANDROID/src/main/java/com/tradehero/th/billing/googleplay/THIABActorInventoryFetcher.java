package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.BillingActorInventoryFetcher;
import com.tradehero.common.billing.InventoryFetcher;
import com.tradehero.common.billing.googleplay.IABActorInventoryFetcher;
import com.tradehero.common.billing.googleplay.IABProductDetails;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exceptions.IABException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 11:06 AM To change this template use File | Settings | File Templates. */
public interface THIABActorInventoryFetcher extends IABActorInventoryFetcher<
        IABSKU,
        THSKUDetails,
        InventoryFetcher.OnInventoryFetchedListener<IABSKU, THSKUDetails, IABException>,
        IABException>
{
}
