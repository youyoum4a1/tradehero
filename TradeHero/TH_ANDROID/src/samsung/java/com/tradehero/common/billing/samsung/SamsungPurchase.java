package com.tradehero.common.billing.samsung;

import android.support.annotation.NonNull;
import com.samsung.android.sdk.iap.lib.vo.InboxVo;
import com.samsung.android.sdk.iap.lib.vo.PurchaseVo;
import com.tradehero.common.billing.ProductPurchase;

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
