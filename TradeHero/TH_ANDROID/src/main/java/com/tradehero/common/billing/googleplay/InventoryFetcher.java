package com.tradehero.common.billing.googleplay;

import android.content.Context;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by julien on 4/11/13
 */
public class InventoryFetcher extends IABServiceConnector
{
    public static final String TAG = InventoryFetcher.class.getSimpleName();

    protected HashMap<SKU, SKUDetails> inventory;
    private List<SKU> skus;

    public InventoryFetcher(Context ctx, List<SKU> skus)
    {
       super(ctx);
       this.skus = skus;
    }


    public Map<SKU, SKUDetails> getInventory()
    {
        return Collections.unmodifiableMap(inventory);
    }


}
