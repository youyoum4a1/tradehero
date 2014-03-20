package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABBillingInventoryFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUList;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import org.json.JSONException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/6/13 Time: 3:48 PM To change this template use File | Settings | File Templates. */
public class THIABBillingInventoryFetcher
        extends IABBillingInventoryFetcher<
        IABSKUListKey,
        IABSKU,
        IABSKUList,
        THIABProductDetail>
{
    public static final String TAG = THIABBillingInventoryFetcher.class.getSimpleName();

    public THIABBillingInventoryFetcher()
    {
        super();
    }

    @Override protected THIABProductDetail createSKUDetails(IABSKUListKey itemType, String json) throws JSONException
    {
        return new THIABProductDetail(itemType, json);
    }
}
