package com.tradehero.th.fragments.billing.store;

import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.persistence.DTO;

public class StoreItemDisplayDTO implements DTO
{
    public final ProductDetail productDetail;

    public StoreItemDisplayDTO(ProductDetail productDetail)
    {
        this.productDetail = productDetail;
    }
}
