package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.ProductDetail;


public interface IABProductDetail<IABSKUType extends IABSKU>
        extends ProductDetail<IABSKUType>
{
    String getType();
    boolean isOfType(String type);
}
