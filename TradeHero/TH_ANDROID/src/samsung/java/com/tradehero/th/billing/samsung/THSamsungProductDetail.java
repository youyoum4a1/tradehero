package com.androidth.general.billing.samsung;

import android.support.annotation.NonNull;
import com.samsung.android.sdk.iap.lib.vo.ItemVo;
import com.androidth.general.common.billing.samsung.BaseSamsungProductDetail;
import com.androidth.general.common.billing.samsung.SamsungSKU;
import com.tradehero.th.R;
import com.androidth.general.billing.ProductIdentifierDomain;
import com.androidth.general.billing.THProductDetail;

public class THSamsungProductDetail
        extends BaseSamsungProductDetail<SamsungSKU>
        implements THProductDetail<SamsungSKU>
{
    public int iconResId;
    public boolean hasFurtherDetails = false;
    public int furtherDetailsResId = R.string.na;
    public ProductIdentifierDomain domain;
    public int displayOrder;
    public int storeDescriptionResId;
    public int storeTitleResId;

    //<editor-fold desc="Constructors">
    public THSamsungProductDetail()
    {
        super();
    }

    public THSamsungProductDetail(String _jsonString)
    {
        super(_jsonString);
    }

    public THSamsungProductDetail(ItemVo itemVo)
    {
        super(itemVo);
    }
    //</editor-fold>

    @NonNull @Override public SamsungSKU getProductIdentifier()
    {
        return new SamsungSKU(getItemId());
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
