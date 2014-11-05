package com.tradehero.th.billing.samsung;

import com.sec.android.iap.lib.vo.InboxVo;
import com.tradehero.common.billing.samsung.SamsungPurchase;
import com.tradehero.common.billing.samsung.SamsungSKU;
import android.support.annotation.NonNull;

public class THSamsungPurchaseIncomplete
        extends SamsungPurchase<
        SamsungSKU,
        THSamsungOrderId>
{
    //<editor-fold desc="Constructors">
    public THSamsungPurchaseIncomplete(@NonNull String groupId, @NonNull InboxVo toCopyFrom)
    {
        super(groupId, toCopyFrom);
    }
    //</editor-fold>

    @Override @NonNull public SamsungSKU getProductIdentifier()
    {
        return new SamsungSKU(getGroupId(), getItemId());
    }

    @Override @NonNull public THSamsungOrderId getOrderId()
    {
        return new THSamsungOrderId(getPurchaseId());
    }
}
