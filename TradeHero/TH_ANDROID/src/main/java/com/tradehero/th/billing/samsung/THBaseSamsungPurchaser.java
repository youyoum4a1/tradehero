package com.tradehero.th.billing.samsung;

import android.content.Context;
import com.sec.android.iap.lib.vo.ErrorVo;
import com.sec.android.iap.lib.vo.PurchaseVo;
import com.tradehero.common.billing.samsung.BaseSamsungPurchaser;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.billing.samsung.exception.THSamsungExceptionFactory;
import com.tradehero.th.utils.DaggerUtils;
import timber.log.Timber;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xavier on 3/27/14.
 */
public class THBaseSamsungPurchaser
    extends BaseSamsungPurchaser<
        SamsungSKU,
        THSamsungPurchaseOrder,
        THSamsungOrderId,
        THSamsungPurchase,
        SamsungException>
    implements THSamsungPurchaser
{
    @Inject protected THSamsungExceptionFactory samsungExceptionFactory;
    @Inject @ProcessingPurchase StringSetPreference processingPurchaseStringSet;

    public THBaseSamsungPurchaser(Context context, int mode)
    {
        super(context, mode);
        DaggerUtils.inject(this);
    }

    @Override protected THSamsungPurchase createSamsungPurchase(PurchaseVo purchaseVo)
    {
        return new THSamsungPurchase(purchaseOrder.getProductIdentifier().groupId, purchaseVo, null);
    }

    @Override protected SamsungException createSamsungException(ErrorVo errorVo)
    {
        return samsungExceptionFactory.create(errorVo.getErrorCode());
    }

    @Override
    protected void handlePurchaseFinished(THSamsungPurchase purchase)
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
        }
        catch (IOException e)
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
