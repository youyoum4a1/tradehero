package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABInventoryFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import org.json.JSONException;

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
