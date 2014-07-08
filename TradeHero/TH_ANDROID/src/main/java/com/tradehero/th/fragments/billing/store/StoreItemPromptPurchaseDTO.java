package com.tradehero.th.fragments.billing.store;

import com.tradehero.th.billing.ProductIdentifierDomain;

public class StoreItemPromptPurchaseDTO extends StoreItemClickableDTO
{
    public int buttonIconResId;
    public ProductIdentifierDomain productIdentifierDomain;

    //<editor-fold desc="Constructors">
    public StoreItemPromptPurchaseDTO(
            int titleResId,
            int iconResId,
            int buttonIconResId,
            ProductIdentifierDomain productIdentifierDomain)
    {
        super(titleResId, iconResId);
        this.buttonIconResId = buttonIconResId;
        this.productIdentifierDomain = productIdentifierDomain;
    }
    //</editor-fold>
}
