package com.tradehero.th.billing.googleplay;

import android.content.Context;
import com.tradehero.common.billing.googleplay.BaseIABInventoryFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

public class THBaseIABInventoryFetcher
        extends BaseIABInventoryFetcher<
                        IABSKU,
                        THIABProductDetail>
    implements THIABInventoryFetcher
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseIABInventoryFetcher(
            @NotNull Context context,
            @NotNull Lazy<IABExceptionFactory> iabExceptionFactory)
    {
        super(context, iabExceptionFactory);
    }
    //</editor-fold>

    @Override @NotNull protected THIABProductDetail createSKUDetails(IABSKUListKey itemType, String json) throws JSONException
    {
        return new THIABProductDetail(itemType, json);
    }
}
