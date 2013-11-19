package com.tradehero.common.billing.googleplay;

import com.tradehero.common.billing.ProductDetails;

/** Created with IntelliJ IDEA. User: xavier Date: 11/8/13 Time: 5:23 PM To change this template use File | Settings | File Templates. */
public interface IABProductDetails<IABSKUType extends IABSKU> extends ProductDetails<IABSKUType>
{
    String getType();
    boolean isOfType(String type);
}
