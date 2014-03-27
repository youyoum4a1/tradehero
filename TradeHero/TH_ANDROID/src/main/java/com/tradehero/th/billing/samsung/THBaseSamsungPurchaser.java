package com.tradehero.th.billing.samsung;

import android.content.Context;
import com.sec.android.iap.lib.vo.ErrorVo;
import com.sec.android.iap.lib.vo.PurchaseVo;
import com.tradehero.common.billing.samsung.BaseSamsungPurchaser;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.samsung.exception.THSamsungExceptionFactory;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

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
}
