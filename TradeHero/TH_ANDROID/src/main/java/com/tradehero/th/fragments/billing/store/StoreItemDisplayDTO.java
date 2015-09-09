package com.tradehero.th.fragments.billing.store;

import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.persistence.DTO;

public class StoreItemDisplayDTO implements DTO
{
    public final String title;
    public final String description;
    public final String priceText;
    public final ProductDetail productDetail;
    public final int displayOrder;

    public StoreItemDisplayDTO(String title, String description, String priceText, ProductDetail productDetail, int displayOrder)
    {
        this.title = title;
        this.description = description;
        this.priceText = priceText;
        this.productDetail = productDetail;
        this.displayOrder = displayOrder;
    }
}
