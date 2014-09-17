package com.tradehero.th.billing.samsung;

import com.sec.android.iap.lib.vo.InboxVo;
import com.tradehero.common.billing.samsung.SamsungPurchase;
import com.tradehero.common.billing.samsung.SamsungSKU;
import org.jetbrains.annotations.NotNull;

public class THSamsungPurchaseIncomplete
        extends SamsungPurchase<
        SamsungSKU,
        THSamsungOrderId>
{
    //<editor-fold desc="Constructors">
    public THSamsungPurchaseIncomplete(@NotNull String groupId, @NotNull InboxVo toCopyFrom)
    {
        super(groupId, toCopyFrom);
    }
    //</editor-fold>

    @Override @NotNull public SamsungSKU getProductIdentifier()
    {
        return new SamsungSKU(getGroupId(), getItemId());
    }

    @Override @NotNull public THSamsungOrderId getOrderId()
    {
        return new THSamsungOrderId(getPurchaseId());
    }
}
