package com.tradehero.th.billing.samsung;

import com.android.internal.util.Predicate;
import com.sec.android.iap.lib.vo.ItemVo;
import com.tradehero.common.billing.samsung.BaseSamsungProductDetail;
import com.tradehero.common.billing.samsung.SamsungProductDetail;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.th.R;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THProductDetail;


public class THSamsungProductDetail
        extends BaseSamsungProductDetail<SamsungSKU>
        implements THProductDetail<SamsungSKU>
{
    public static final String TAG = THSamsungProductDetail.class.getSimpleName();

    public int iconResId;
    public boolean hasFurtherDetails = false;
    public int furtherDetailsResId = R.string.na;
    public boolean hasRibbon = false;
    public int iconRibbonResId = R.drawable.default_image;
    public ProductIdentifierDomain domain;

    //<editor-fold desc="Constructors">
    public THSamsungProductDetail(SamsungSKU samsungSKU)
    {
        super(samsungSKU);
    }

    public THSamsungProductDetail(SamsungSKU samsungSKU, String _jsonString)
    {
        super(samsungSKU, _jsonString);
    }

    public THSamsungProductDetail(SamsungSKU samsungSKU, ItemVo itemVo)
    {
        super(samsungSKU, itemVo);
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

    @Override public Double getPrice()
    {
        return getItemPrice();
    }

    @Override public String getPriceText()
    {
        return getItemPriceString();
    }

    @Override public String getDescription()
    {
        return getItemDesc();
    }
}
