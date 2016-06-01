package com.ayondo.academy.billing;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;

public interface THProductDetail<ProductIdentifierType extends ProductIdentifier>
    extends ProductDetail<ProductIdentifierType>
{
    @DrawableRes int getIconResId();
    boolean getHasFurtherDetails();
    @StringRes int getFurtherDetailsResId();
    ProductIdentifierDomain getDomain();
    Double getPrice();
    String getPriceText();
    String getDescription();
    int getDisplayOrder();
    @StringRes int getStoreTitleResId();
    @StringRes int getStoreDescriptionResId();
}
