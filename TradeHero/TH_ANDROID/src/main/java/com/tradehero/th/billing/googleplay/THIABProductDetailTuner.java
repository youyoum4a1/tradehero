package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.ProductDetailTuner;
import com.tradehero.common.billing.googleplay.IABSKU;

public class THIABProductDetailTuner implements ProductDetailTuner<IABSKU, THIABProductDetail>
{
    public THIABProductDetailTuner()
    {
        super();
    }

    @Override public void fineTune(THIABProductDetail productDetails)
    {

    }
}
