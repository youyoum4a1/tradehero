package com.tradehero.th.billing.samsung.purchase;

import android.content.Context;
import android.support.annotation.NonNull;
import com.sec.android.iap.lib.vo.PurchaseVo;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.purchase.BaseSamsungPurchaserRx;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.billing.samsung.THSamsungOrderId;
import com.tradehero.th.billing.samsung.THSamsungPurchase;
import com.tradehero.th.billing.samsung.THSamsungPurchaseOrder;
import com.tradehero.th.billing.samsung.exception.SamsungSavingPurchaseException;
import java.io.IOException;
import java.util.Collections;

public class THBaseSamsungPurchaserRx
        extends BaseSamsungPurchaserRx<
        SamsungSKU,
        THSamsungPurchaseOrder,
        THSamsungOrderId,
        THSamsungPurchase>
        implements THSamsungPurchaserRx
{
    @NonNull protected final StringSetPreference processingPurchaseStringSet;

    //<editor-fold desc="Constructors">
    public THBaseSamsungPurchaserRx(
            int requestCode,
            @NonNull Context context,
            int mode,
            @NonNull THSamsungPurchaseOrder purchaseOrder,
            boolean showSucessDialog,
            @NonNull StringSetPreference processingPurchaseStringSet)
    {
        super(requestCode, context, mode, purchaseOrder, showSucessDialog);
        this.processingPurchaseStringSet = processingPurchaseStringSet;
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
        savePurchaseInPref(created);
        return created;
    }

    protected void savePurchaseInPref(THSamsungPurchase purchase)
    {
        String stringedPurchase;
        try
        {
            stringedPurchase = THJsonAdapter.getInstance().toStringBody(purchase.getPurchaseToSaveDTO());
        } catch (IOException e)
        {
            throw new SamsungSavingPurchaseException(e);
        }
        if (stringedPurchase != null)
        {
            processingPurchaseStringSet.add(Collections.singletonList(stringedPurchase));
        }
    }
}
