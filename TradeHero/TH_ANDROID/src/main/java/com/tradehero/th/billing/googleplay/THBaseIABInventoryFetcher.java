package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABInventoryFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import org.json.JSONException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/6/13 Time: 3:48 PM To change this template use File | Settings | File Templates. */
public class THBaseIABInventoryFetcher
        extends BaseIABInventoryFetcher<
                        IABSKU,
                        THIABProductDetail>
    implements THIABInventoryFetcher
{
    public THBaseIABInventoryFetcher()
    {
        super();
    }

    @Override protected THIABProductDetail createSKUDetails(IABSKUListKey itemType, String json) throws JSONException
    {
        return new THIABProductDetail(itemType, json);
    }
}
