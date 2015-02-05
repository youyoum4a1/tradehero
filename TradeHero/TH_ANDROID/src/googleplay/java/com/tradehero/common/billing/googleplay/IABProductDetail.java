package com.tradehero.common.billing.googleplay;

import android.support.annotation.NonNull;
import com.tradehero.common.billing.ProductDetail;

public interface IABProductDetail<IABSKUType extends IABSKU>
        extends ProductDetail<IABSKUType>
{
    @NonNull String getType();
    boolean isOfType(String type);
}
