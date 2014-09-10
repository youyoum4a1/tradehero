package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.BaseIABInventoryFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.th.activities.CurrentActivityHolder;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import javax.inject.Inject;

import dagger.Lazy;

public class THBaseIABInventoryFetcher
        extends BaseIABInventoryFetcher<
                        IABSKU,
                        THIABProductDetail>
    implements THIABInventoryFetcher
{
    //<editor-fold desc="Constructors">
    @Inject public THBaseIABInventoryFetcher(
            @NotNull CurrentActivityHolder currentActivityHolder,
            @NotNull Lazy<IABExceptionFactory> iabExceptionFactory)
    {
        super(currentActivityHolder, iabExceptionFactory);
    }
    //</editor-fold>

    @Override @NotNull protected THIABProductDetail createSKUDetails(IABSKUListKey itemType, String json) throws JSONException
    {
        return new THIABProductDetail(itemType, json);
    }
}
