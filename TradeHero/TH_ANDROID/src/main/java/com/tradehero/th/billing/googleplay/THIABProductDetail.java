package com.tradehero.th.billing.googleplay;

import com.android.internal.util.Predicate;
import com.tradehero.common.billing.googleplay.BaseIABProductDetail;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.thm.R;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THProductDetail;
import org.json.JSONException;

public class THIABProductDetail extends BaseIABProductDetail
    implements THProductDetail<IABSKU>
{
    int iconResId;
    boolean hasFurtherDetails = false;
    int furtherDetailsResId = R.string.na;
    boolean hasRibbon = false;
    int iconRibbonResId = R.drawable.default_image;
    ProductIdentifierDomain domain;

    //<editor-fold desc="Constructors">
    public THIABProductDetail(IABSKUListKey itemType, String jsonSkuDetails) throws JSONException
    {
        super(itemType.key, jsonSkuDetails);
    }

    public THIABProductDetail(String itemType, String jsonSkuDetails) throws JSONException
    {
        super(itemType, jsonSkuDetails);
    }

    public THIABProductDetail(String jsonSkuDetails) throws JSONException
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

    @Override public String getPriceText()
    {
        return price;
    }

    @Override public String getDescription()
    {
        return description;
    }

    public static Predicate<THIABProductDetail> getPredicateIsOfCertainDomain(final ProductIdentifierDomain domain)
    {
        return new Predicate<THIABProductDetail>()
        {
            @Override public boolean apply(THIABProductDetail thIABProductDetail)
            {
                return thIABProductDetail != null && (thIABProductDetail.domain == null ? domain == null : thIABProductDetail.domain.equals(domain));
            }
        };
    }
}
