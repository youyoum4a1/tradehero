package com.tradehero.th.billing.samsung;

import android.content.Context;
import android.support.annotation.NonNull;
import com.sec.android.iap.lib.vo.ErrorVo;
import com.sec.android.iap.lib.vo.PurchaseVo;
import com.tradehero.common.billing.samsung.BaseSamsungPurchaser;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.billing.samsung.exception.THSamsungExceptionFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

public class THBaseSamsungPurchaser
        extends BaseSamsungPurchaser<
        SamsungSKU,
        THSamsungPurchaseOrder,
        THSamsungOrderId,
        THSamsungPurchase,
        SamsungException>
        implements THSamsungPurchaser
{
    @NonNull protected final THSamsungExceptionFactory samsungExceptionFactory;
    @NonNull protected final StringSetPreference processingPurchaseStringSet;

    //<editor-fold desc="Constructors">
    public THBaseSamsungPurchaser(
            int requestCode,
            @NonNull Context context,
            int mode,
            @NonNull THSamsungExceptionFactory samsungExceptionFactory,
            @NonNull StringSetPreference processingPurchaseStringSet)
    {
        super(requestCode, context, mode);
        this.samsungExceptionFactory = samsungExceptionFactory;
        this.processingPurchaseStringSet = processingPurchaseStringSet;
    }
    //</editor-fold>

    @Override protected THSamsungPurchase createSamsungPurchase(PurchaseVo purchaseVo)
    {
        return new THSamsungPurchase(purchaseOrder.getProductIdentifier().groupId, purchaseVo, purchaseOrder.getApplicablePortfolioId());
    }

    @Override protected SamsungException createSamsungException(ErrorVo errorVo)
    {
        return samsungExceptionFactory.create(errorVo.getErrorCode());
    }

    @Override protected void handlePurchaseFinished(THSamsungPurchase purchase)
    {
        savePurchaseInPref(purchase);
        super.handlePurchaseFinished(purchase);
    }

    protected void savePurchaseInPref(THSamsungPurchase purchase)
    {
        Timber.d("Saving purchase %s", purchase);
        String stringedPurchase = null;
        try
        {
            stringedPurchase = THJsonAdapter.getInstance().toStringBody(purchase.getPurchaseToSaveDTO());
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        if (stringedPurchase != null)
        {
            List<String> list = new ArrayList<>();
            list.add(stringedPurchase);
            processingPurchaseStringSet.add(list);
        }
    }
}
