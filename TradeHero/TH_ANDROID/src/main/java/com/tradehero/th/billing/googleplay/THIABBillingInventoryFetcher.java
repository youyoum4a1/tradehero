package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABBillingInventoryFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import org.json.JSONException;

public class THIABBillingInventoryFetcher
        extends IABBillingInventoryFetcher<
        IABSKUListKey,
        IABSKU,
        IABSKUList,
        THIABProductDetail>
{
    public THIABBillingInventoryFetcher()
    {
        super();
    }

    @Override protected THIABProductDetail createSKUDetails(IABSKUListKey itemType, String json) throws JSONException
    {
        return new THIABProductDetail(itemType, json);
    }
}
