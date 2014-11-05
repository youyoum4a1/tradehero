package com.tradehero.common.billing.samsung;

import com.sec.android.iap.lib.vo.InboxVo;
import com.sec.android.iap.lib.vo.PurchaseVo;
import com.tradehero.common.billing.ProductPurchase;
import android.support.annotation.NonNull;

abstract public class SamsungPurchase<
        SamsungSKUType extends SamsungSKU,
        SamsungOrderIdType extends SamsungOrderId>
        extends PurchaseVo
        implements ProductPurchase<
        SamsungSKUType,
        SamsungOrderIdType>
{
    @NonNull protected final String groupId;

    //<editor-fold desc="Constructors">
    public SamsungPurchase(@NonNull String groupId, @NonNull String _jsonString)
    {
        super(_jsonString);
        this.groupId = groupId;
    }

    public SamsungPurchase(@NonNull String groupId, @NonNull PurchaseVo toCopyFrom)
    {
        super(toCopyFrom.getJsonString());
        this.groupId = groupId;
    }

    public SamsungPurchase(@NonNull String groupId, @NonNull InboxVo toCopyFrom)
    {
        super(toCopyFrom.getJsonString());
        this.groupId = groupId;
    }
    //</editor-fold>

    @NonNull public String getGroupId()
    {
        return groupId;
    }
}
