package com.tradehero.th.billing.googleplay;

import android.content.Context;
import com.tradehero.common.billing.googleplay.InventoryFetcher;
import com.tradehero.common.billing.googleplay.SKU;
import java.util.List;
import org.json.JSONException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/6/13 Time: 3:48 PM To change this template use File | Settings | File Templates. */
public class THInventoryFetcher extends InventoryFetcher<THSKUDetails>
{
    public static final String TAG = THInventoryFetcher.class.getSimpleName();

    public THInventoryFetcher(Context ctx, List<SKU> skus)
    {
        super(ctx, skus);
    }

    @Override protected THSKUDetails createSKUDetails(String itemType, String json) throws JSONException
    {
        return new THSKUDetails(itemType, json);
    }
}
