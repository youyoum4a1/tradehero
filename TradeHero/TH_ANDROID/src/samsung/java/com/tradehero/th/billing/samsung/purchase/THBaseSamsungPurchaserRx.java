package com.tradehero.th.billing.samsung.purchase;

import android.content.Context;
import android.support.annotation.NonNull;
import com.samsung.android.sdk.iap.lib.vo.PurchaseVo;
import com.tradehero.common.billing.samsung.SamsungBillingMode;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.purchase.BaseSamsungPurchaserRx;
import com.tradehero.th.billing.samsung.THSamsungOrderId;
import com.tradehero.th.billing.samsung.THSamsungPurchase;
import com.tradehero.th.billing.samsung.THSamsungPurchaseOrder;

public class THBaseSamsungPurchaserRx
        extends BaseSamsungPurchaserRx<
        SamsungSKU,
        THSamsungPurchaseOrder,
        THSamsungOrderId,
        THSamsungPurchase>
        implements THSamsungPurchaserRx
{
    //<editor-fold desc="Constructors">
    public THBaseSamsungPurchaserRx(
            int requestCode,
            @NonNull Context context,
            @SamsungBillingMode int mode,
            @NonNull THSamsungPurchaseOrder purchaseOrder,
            boolean showSucessDialog)
    {
        super(requestCode, context, mode, purchaseOrder, showSucessDialog);
    }
    //</editor-fold>

    @Override @NonNull protected THSamsungPurchase createSamsungPurchase(@NonNull PurchaseVo purchaseVo)
    {
        THSamsungPurchase created =
                new THSamsungPurchase(purchaseOrder.getProductIdentifier().groupId, purchaseVo, purchaseOrder.getApplicablePortfolioId());
        if (getPurchaseOrder().getUserToFollow() != null)
        {
            created.setUserToFollow(getPurchaseOrder().getUserToFollow());
        }
        return created;
    }
}
