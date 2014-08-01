package com.tradehero.th.fragments.billing.store;

public class StoreItemClickableDTO extends StoreItemDTO
{
    public int iconResId;

    //<editor-fold desc="Constructors">
    public StoreItemClickableDTO(int titleResId, int iconResId)
    {
        super(titleResId);
        this.iconResId = iconResId;
    }
    //</editor-fold>
}
