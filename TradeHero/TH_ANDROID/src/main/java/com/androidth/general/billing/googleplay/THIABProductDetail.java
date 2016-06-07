package com.androidth.general.billing.googleplay;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import com.androidth.general.common.billing.googleplay.BaseIABProductDetail;
import com.androidth.general.common.billing.googleplay.IABSKU;
import com.androidth.general.common.billing.googleplay.IABSKUListKey;
import com.androidth.general.R;
import com.androidth.general.billing.ProductIdentifierDomain;
import com.androidth.general.billing.THProductDetail;
import org.json.JSONException;

public class THIABProductDetail
        extends BaseIABProductDetail
        implements THProductDetail<IABSKU>
{
    @DrawableRes int iconResId;
    boolean hasFurtherDetails = false;
    @StringRes int furtherDetailsResId = R.string.na;
    ProductIdentifierDomain domain;
    public int displayOrder;
    public int storeDescriptionResId;
    public int storeTitleResId;

    //<editor-fold desc="Constructors">
    public THIABProductDetail(@NonNull IABSKUListKey itemType, @NonNull String jsonSkuDetails) throws JSONException
    {
        super(itemType.key, jsonSkuDetails);
    }

    public THIABProductDetail(@NonNull String itemType, @NonNull String jsonSkuDetails) throws JSONException
    {
        super(itemType, jsonSkuDetails);
    }

    public THIABProductDetail(@NonNull String jsonSkuDetails) throws JSONException
    {
        super(jsonSkuDetails);
    }
    //</editor-fold>

    @DrawableRes @Override public int getIconResId()
    {
        return iconResId;
    }

    @Override public boolean getHasFurtherDetails()
    {
        return hasFurtherDetails;
    }

    @StringRes @Override public int getFurtherDetailsResId()
    {
        return furtherDetailsResId;
    }

    @Override public ProductIdentifierDomain getDomain()
    {
        return domain;
    }

    @Override public String toString()
    {
        return "THIABProductDetail:" + json;
    }

    @Override @Nullable public Double getPrice()
    {
        if (priceAmountMicros == null)
        {
            return null;
        }
        return (double) priceAmountMicros;
    }

    @Override public String getPriceText()
    {
        return price;
    }

    @Override public String getDescription()
    {
        return description;
    }

    @Override public int getDisplayOrder()
    {
        return displayOrder;
    }

    @Override public int getStoreTitleResId()
    {
        return storeTitleResId;
    }

    @Override public int getStoreDescriptionResId()
    {
        return storeDescriptionResId;
    }
}
