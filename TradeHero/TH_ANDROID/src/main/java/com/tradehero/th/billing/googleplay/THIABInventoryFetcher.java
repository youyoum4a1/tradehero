package com.tradehero.th.billing.googleplay;

import android.content.Context;
import android.os.RemoteException;
import com.tradehero.common.billing.googleplay.IABInventoryFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCache;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.HashMap;
import javax.inject.Inject;
import org.json.JSONException;

/** Created with IntelliJ IDEA. User: xavier Date: 11/6/13 Time: 3:48 PM To change this template use File | Settings | File Templates. */
public class THIABInventoryFetcher extends IABInventoryFetcher<IABSKU, THIABProductDetail>
{
    public static final String TAG = THIABInventoryFetcher.class.getSimpleName();

    @Inject protected Lazy<THIABProductDetailCache> skuDetailCache;

    public THIABInventoryFetcher(Context ctx)
    {
        super(ctx);
    }

    @Override protected THIABProductDetail createSKUDetails(String itemType, String json) throws JSONException
    {
        return new THIABProductDetail(itemType, json);
    }

    @Override protected HashMap<IABSKU, THIABProductDetail> internalFetchCompleteInventory() throws IABException, RemoteException, JSONException
    {
        HashMap<IABSKU, THIABProductDetail> inventory = super.internalFetchCompleteInventory();
        skuDetailCache.get().put(new ArrayList<>(inventory.values()));
        return inventory;
    }
}
