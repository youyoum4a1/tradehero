package com.tradehero.th.billing;

import com.tradehero.common.billing.ProductDetailTuner;
import com.tradehero.common.billing.ProductIdentifier;

/**
 * Created by xavier on 3/28/14.
 */
public interface THProductDetailTuner<
        ProductIdentifierType extends ProductIdentifier,
        THProductDetailType extends THProductDetail<ProductIdentifierType>>
        extends ProductDetailTuner<
        ProductIdentifierType,
        THProductDetailType>
{
}
