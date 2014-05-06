package com.tradehero.th.billing;

import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.th.R;

public interface THProductDetail<ProductIdentifierType extends ProductIdentifier>
    extends ProductDetail<ProductIdentifierType>
{
    int getIconResId();
    boolean getHasFurtherDetails();
    int getFurtherDetailsResId();
    boolean getHasRibbon();
    int getIconRibbonResId();
    ProductIdentifierDomain getDomain();
    String getPriceText();
    String getDescription();
}
