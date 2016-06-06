package com.androidth.general.common.billing.samsung;

import com.samsung.android.sdk.iap.lib.vo.ItemVo;

abstract public class BaseSamsungProductDetail<SamsungSKUType extends SamsungSKU>
    extends ItemVo
    implements SamsungProductDetail<SamsungSKUType>
{
    //<editor-fold desc="Constructors">
    public BaseSamsungProductDetail()
    {
        super();
    }

    public BaseSamsungProductDetail(String _jsonString)
    {
        super(_jsonString);
    }

    public BaseSamsungProductDetail(ItemVo itemVo)
    {
        super(itemVo.getJsonString());
    }
    //</editor-fold>
}
