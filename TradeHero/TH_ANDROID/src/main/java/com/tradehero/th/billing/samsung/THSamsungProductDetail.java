package com.tradehero.th.billing.samsung;

import com.android.internal.util.Predicate;
import com.tradehero.common.billing.samsung.SamsungProductDetail;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.thm.R;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THProductDetail;

public class THSamsungProductDetail
        implements SamsungProductDetail<SamsungSKU>,
            THProductDetail<SamsungSKU>
{
    private final SamsungSKU samsungSKU;
    public int iconResId;
    public boolean hasFurtherDetails = false;
    public int furtherDetailsResId = R.string.na;
    public boolean hasRibbon = false;
    public int iconRibbonResId = R.drawable.default_image;
    public ProductIdentifierDomain domain;
    public String price;
    public String description;

    //<editor-fold desc="Constructors">
    public THSamsungProductDetail(SamsungSKU samsungSKU)
    {
        super();
        this.samsungSKU = samsungSKU;
    }
    //</editor-fold>

    @Override public SamsungSKU getProductIdentifier()
    {
        return samsungSKU;
    }

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

    @Override public String getPriceText()
    {
        return price;
    }

    @Override public String getDescription()
    {
        return description;
    }

    public static Predicate<THSamsungProductDetail> getPredicateIsOfCertainDomain(final ProductIdentifierDomain domain)
    {
        return new Predicate<THSamsungProductDetail>()
        {
            @Override public boolean apply(THSamsungProductDetail thSamsungProductDetail)
            {
                return thSamsungProductDetail != null && (thSamsungProductDetail.domain == null ? domain == null : thSamsungProductDetail.domain.equals(domain));
            }
        };
    }
}
