package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.ProductDetail;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 5:23 PM To change this template use File | Settings | File Templates. */
public interface IABProductDetail<IABSKUType extends IABSKU>
        extends ProductDetail<IABSKUType>
{
    String getType();
    boolean isOfType(String type);
}
