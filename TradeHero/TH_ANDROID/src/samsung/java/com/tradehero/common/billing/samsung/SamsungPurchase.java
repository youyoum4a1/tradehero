package com.tradehero.common.billing.samsung;

import com.sec.android.iap.lib.vo.InboxVo;
import com.sec.android.iap.lib.vo.PurchaseVo;
import com.tradehero.common.billing.ProductPurchase;
import org.jetbrains.annotations.NotNull;

abstract public class SamsungPurchase<
        SamsungSKUType extends SamsungSKU,
        SamsungOrderIdType extends SamsungOrderId>
        extends PurchaseVo
        implements ProductPurchase<
        SamsungSKUType,
        SamsungOrderIdType>
{
    @NotNull protected final String groupId;

    //<editor-fold desc="Constructors">
    public SamsungPurchase(@NotNull String groupId, @NotNull String _jsonString)
    {
        super(_jsonString);
        this.groupId = groupId;
    }

    public SamsungPurchase(@NotNull String groupId, @NotNull PurchaseVo toCopyFrom)
    {
        super(toCopyFrom.getJsonString());
        this.groupId = groupId;
    }

    public SamsungPurchase(@NotNull String groupId, @NotNull InboxVo toCopyFrom)
    {
        super(toCopyFrom.getJsonString());
        this.groupId = groupId;
    }
    //</editor-fold>

    @NotNull public String getGroupId()
    {
        return groupId;
    }
}
