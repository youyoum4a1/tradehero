package com.tradehero.th.billing.samsung;

import com.sec.android.iap.lib.vo.ItemVo;
import com.tradehero.common.billing.samsung.BaseSamsungProductDetail;
import com.tradehero.common.billing.samsung.SamsungItemGroup;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.th.R;
import com.tradehero.th.billing.ProductIdentifierDomain;
import com.tradehero.th.billing.THProductDetail;
import android.support.annotation.NonNull;

public class THSamsungProductDetail
        extends BaseSamsungProductDetail<SamsungSKU>
        implements THProductDetail<SamsungSKU>
{
    public int iconResId;
    public boolean hasFurtherDetails = false;
    public int furtherDetailsResId = R.string.na;
    public boolean hasRibbon = false;
    public int iconRibbonResId = R.drawable.default_image;
    public ProductIdentifierDomain domain;

    //<editor-fold desc="Constructors">
    public THSamsungProductDetail(SamsungItemGroup samsungItemGroup)
    {
        super(samsungItemGroup);
    }

    public THSamsungProductDetail(SamsungItemGroup samsungItemGroup, String _jsonString)
    {
        super(samsungItemGroup, _jsonString);
    }

    public THSamsungProductDetail(SamsungItemGroup samsungItemGroup, ItemVo itemVo)
    {
        super(samsungItemGroup, itemVo);
    }
    //</editor-fold>

    @NonNull @Override public SamsungSKU getProductIdentifier()
    {
        return new SamsungSKU(samsungItemGroup.groupId, getItemId());
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
