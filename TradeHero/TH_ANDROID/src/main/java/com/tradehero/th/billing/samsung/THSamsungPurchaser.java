package com.tradehero.th.billing.samsung;

import android.content.Context;
import com.sec.android.iap.lib.vo.ErrorVo;
import com.sec.android.iap.lib.vo.PurchaseVo;
import com.tradehero.common.billing.PurchaseOrder;
import com.tradehero.common.billing.samsung.BaseSamsungPurchaser;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.common.billing.samsung.exception.SamsungExceptionFactory;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

/**
 * Created by xavier on 3/27/14.
 */
public class THSamsungPurchaser
    extends BaseSamsungPurchaser<
        SamsungSKU,
        THSamsungPurchaseOrder,
        THSamsungOrderId,
        THSamsungPurchase,
        SamsungException>
{
    @Inject protected SamsungExceptionFactory samsungExceptionFactory;

    public THSamsungPurchaser(Context context, int mode)
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
