package com.tradehero.common.billing.samsung;

import com.sec.android.iap.lib.vo.ItemVo;

/**
 * Created by xavier on 3/27/14.
 */
public class BaseSamsungProductDetail<SamsungSKUType extends SamsungSKU>
    extends ItemVo
    implements SamsungProductDetail<SamsungSKUType>
{
    private final SamsungSKUType samsungSKU;

    //<editor-fold desc="Constructors">
    public BaseSamsungProductDetail(SamsungSKUType samsungSKU)
    {
        super();
        this.samsungSKU = samsungSKU;
    }

    public BaseSamsungProductDetail(SamsungSKUType samsungSKU, String _jsonString)
    {
        super(_jsonString);
        this.samsungSKU = samsungSKU;
    }

    public BaseSamsungProductDetail(SamsungSKUType samsungSKU, ItemVo itemVo)
    {
        super(itemVo.getJsonString());
        this.samsungSKU = samsungSKU;
    }
    //</editor-fold>

    @Override public SamsungSKUType getProductIdentifier()
    {
        return samsungSKU;
    }
}
