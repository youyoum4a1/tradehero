package com.tradehero.th.fragments.billing.store;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.tradehero.th.billing.ProductIdentifierDomain;

public class StoreItemPromptPurchaseDTO extends StoreItemClickableDTO
{
    @NonNull public final ProductIdentifierDomain productIdentifierDomain;

    //<editor-fold desc="Constructors">
    public StoreItemPromptPurchaseDTO(
            @StringRes int titleResId,
            @DrawableRes int iconResId,
            @NonNull ProductIdentifierDomain productIdentifierDomain)
    {
        super(titleResId, iconResId);
        this.productIdentifierDomain = productIdentifierDomain;
    }
    //</editor-fold>
}
