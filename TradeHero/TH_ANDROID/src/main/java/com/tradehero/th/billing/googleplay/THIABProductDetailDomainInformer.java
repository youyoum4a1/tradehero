package com.tradehero.th.billing.googleplay;

import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.th.billing.ProductDetailDomainInformer;

interface THIABProductDetailDomainInformer
    extends ProductDetailDomainInformer<IABSKU, THIABProductDetail>
{
}