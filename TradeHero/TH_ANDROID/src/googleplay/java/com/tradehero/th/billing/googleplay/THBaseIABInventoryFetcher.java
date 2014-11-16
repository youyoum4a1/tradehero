package com.tradehero.th.billing.googleplay;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.BaseIABInventoryFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import dagger.Lazy;
import org.json.JSONException;

public class THBaseIABInventoryFetcher
        extends BaseIABInventoryFetcher<
        IABSKU,
        THIABProductDetail>
        implements THIABInventoryFetcher
{
    //<editor-fold desc="Constructors">
    public THBaseIABInventoryFetcher(
            int requestCode,
            @NonNull Context context,
            @NonNull Lazy<IABExceptionFactory> iabExceptionFactory)
    {
        super(requestCode, context, iabExceptionFactory);
    }
    //</editor-fold>

    @Override @NonNull protected THIABProductDetail createSKUDetails(IABSKUListKey itemType, String json) throws JSONException
    {
        return new THIABProductDetail(itemType, json);
    }
}
