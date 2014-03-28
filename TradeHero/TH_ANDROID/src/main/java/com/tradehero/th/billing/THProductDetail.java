package com.tradehero.th.billing;

import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.th.R;

/**
 * Created by xavier on 3/26/14.
 */
public interface THProductDetail<ProductIdentifierType extends ProductIdentifier>
    extends ProductDetail<ProductIdentifierType>
{
    int getIconResId();
    boolean getHasFurtherDetails();
    int getFurtherDetailsResId();
    boolean getHasRibbon();
    int getIconRibbonResId();
    ProductIdentifierDomain getDomain();
    Double getPrice();
    String getPriceText();
    String getDescription();
}
