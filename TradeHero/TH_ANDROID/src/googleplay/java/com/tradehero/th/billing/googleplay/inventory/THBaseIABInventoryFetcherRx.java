package com.androidth.general.billing.googleplay.inventory;

import android.content.Context;
import android.support.annotation.NonNull;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.IABSKUListKey;
import com.androidth.general.common.billing.googleplay.exception.IABExceptionFactory;
import com.androidth.general.common.billing.googleplay.inventory.BaseIABInventoryFetcherRx;
import com.androidth.general.billing.googleplay.THIABProductDetail;
import java.util.List;
import org.json.JSONException;

public class THBaseIABInventoryFetcherRx
        extends BaseIABInventoryFetcherRx<
        IABSKU,
        THIABProductDetail>
        implements THIABInventoryFetcherRx
{
    //<editor-fold desc="Constructors">
    public THBaseIABInventoryFetcherRx(
            int requestCode,
            @NonNull List<IABSKU> iabskus,
            @NonNull Context context,
            @NonNull IABExceptionFactory iabExceptionFactory)
    {
        super(requestCode, iabskus, context, iabExceptionFactory);
    }
    //</editor-fold>

    @Override @NonNull protected THIABProductDetail createSKUDetails(IABSKUListKey itemType, String json) throws JSONException
    {
        return new THIABProductDetail(itemType, json);
    }
}
