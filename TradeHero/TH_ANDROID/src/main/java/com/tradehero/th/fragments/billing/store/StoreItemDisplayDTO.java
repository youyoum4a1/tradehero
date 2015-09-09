package com.tradehero.th.fragments.billing.store;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.billing.THProductDetail;

public class StoreItemDisplayDTO implements DTO
{
    public final ProductDetail productDetail;
    @DrawableRes public final int iconResId;
    @StringRes public final int titleResId;
    @StringRes public final int descriptionResId;
    public final String priceText;
    public final int displayOrder;

    public StoreItemDisplayDTO(THProductDetail productDetail)
    {
        this.productDetail = productDetail;
        this.iconResId = productDetail.getIconResId();
        this.titleResId = productDetail.getStoreTitleResId();
        this.descriptionResId = productDetail.getStoreDescriptionResId();
        this.priceText = productDetail.getPriceText();
        this.displayOrder = productDetail.getDisplayOrder();
    }
}
