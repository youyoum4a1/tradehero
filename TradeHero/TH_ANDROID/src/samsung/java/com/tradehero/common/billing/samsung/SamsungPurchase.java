package com.androidth.general.common.billing.samsung;

import android.support.annotation.NonNull;
import com.samsung.android.sdk.iap.lib.vo.InboxVo;
import com.samsung.android.sdk.iap.lib.vo.PurchaseVo;
import com.androidth.general.common.billing.ProductPurchase;

abstract public class SamsungPurchase<
        SamsungSKUType extends SamsungSKU,
        SamsungOrderIdType extends SamsungOrderId>
        extends PurchaseVo
        implements ProductPurchase<
        SamsungSKUType,
        SamsungOrderIdType>
{
    //<editor-fold desc="Constructors">
    public SamsungPurchase(@NonNull String _jsonString)
    {
        super(_jsonString);
    }

    public SamsungPurchase(@NonNull PurchaseVo toCopyFrom)
    {
        super(toCopyFrom.getJsonString());
    }

    public SamsungPurchase(@NonNull InboxVo toCopyFrom)
    {
        super(toCopyFrom.getJsonString());
    }
    //</editor-fold>
}
