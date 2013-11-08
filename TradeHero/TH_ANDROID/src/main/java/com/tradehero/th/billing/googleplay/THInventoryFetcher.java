package com.tradehero.th.billing.googleplay;

import android.content.Context;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.InventoryFetcher;
import java.util.List;
import org.json.JSONException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/6/13 Time: 3:48 PM To change this template use File | Settings | File Templates. */
public class THInventoryFetcher extends InventoryFetcher<THSKUDetails>
{
    public static final String TAG = THInventoryFetcher.class.getSimpleName();

    public THInventoryFetcher(Context ctx, List<IABSKU> iabSKUs)
    {
        super(ctx, iabSKUs);
    }

    @Override protected THSKUDetails createSKUDetails(String itemType, String json) throws JSONException
    {
        return new THSKUDetails(itemType, json);
    }
}
