package com.androidth.general.common.billing.googleplay;

import android.support.annotation.NonNull;
import com.androidth.general.common.billing.ProductDetail;

public interface IABProductDetail<IABSKUType extends IABSKU>
        extends ProductDetail<IABSKUType>
{
    @NonNull String getType();
    boolean isOfType(String type);
}
