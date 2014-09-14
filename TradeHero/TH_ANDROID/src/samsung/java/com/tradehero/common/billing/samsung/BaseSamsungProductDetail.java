package com.tradehero.common.billing.samsung;

import com.sec.android.iap.lib.vo.ItemVo;

abstract public class BaseSamsungProductDetail<SamsungSKUType extends SamsungSKU>
    extends ItemVo
    implements SamsungProductDetail<SamsungSKUType>
{
    protected final SamsungItemGroup samsungItemGroup;

    //<editor-fold desc="Constructors">
    public BaseSamsungProductDetail(SamsungItemGroup samsungItemGroup)
    {
        super();
        this.samsungItemGroup = samsungItemGroup;
    }

    public BaseSamsungProductDetail(SamsungItemGroup samsungItemGroup, String _jsonString)
    {
        super(_jsonString);
        this.samsungItemGroup = samsungItemGroup;
    }

    public BaseSamsungProductDetail(SamsungItemGroup samsungItemGroup, ItemVo itemVo)
    {
        super(itemVo.getJsonString());
        this.samsungItemGroup = samsungItemGroup;
    }
    //</editor-fold>
}
