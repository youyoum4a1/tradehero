package com.tradehero.th.billing.googleplay;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.billing.googleplay.BaseIABProductDetail;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.th.R;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THProductDetail;
import org.json.JSONException;

public class THIABProductDetail
        extends BaseIABProductDetail
        implements THProductDetail<IABSKU>
{
    int iconResId;
    boolean hasFurtherDetails = false;
    int furtherDetailsResId = R.string.na;
    boolean hasRibbon = false;
    int iconRibbonResId = R.drawable.default_image;
    ProductIdentifierDomain domain;

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

    @Override public int getIconResId()
    {
        return iconResId;
    }

    @Override public boolean getHasFurtherDetails()
    {
        return hasFurtherDetails;
    }

    @Override public int getFurtherDetailsResId()
    {
        return furtherDetailsResId;
    }

    @Override public boolean getHasRibbon()
    {
        return hasRibbon;
    }

    @Override public int getIconRibbonResId()
    {
        return iconRibbonResId;
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
}
