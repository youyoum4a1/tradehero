package com.tradehero.th.fragments.billing.store;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.th.billing.THProductDetail;

public class StoreItemProductDisplayDTO extends StoreItemDisplayDTO
{
    public final ProductDetail productDetail;
    public final String priceText;
    @StringRes public final int descriptionResId;

    public StoreItemProductDisplayDTO(@NonNull THProductDetail productDetail)
    {
        super(productDetail.getIconResId(), productDetail.getStoreTitleResId(), productDetail.getDisplayOrder());
        this.productDetail = productDetail;
        this.priceText = productDetail.getPriceText();
        this.descriptionResId = productDetail.getStoreDescriptionResId();
    }
}
