package com.ayondo.academy.billing.googleplay.inventory;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.common.billing.googleplay.inventory.BaseIABInventoryFetcherRx;
import com.ayondo.academy.billing.googleplay.THIABProductDetail;
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
