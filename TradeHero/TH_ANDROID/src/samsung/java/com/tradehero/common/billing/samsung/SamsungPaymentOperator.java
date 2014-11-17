package com.tradehero.common.billing.samsung;

import android.support.annotation.NonNull;
import com.sec.android.iap.lib.helper.SamsungIapHelper;
import com.sec.android.iap.lib.vo.PurchaseVo;
import com.tradehero.common.billing.samsung.exception.SamsungPurchaseException;
import rx.Observable;
import rx.Subscriber;

public class SamsungPaymentOperator extends BaseSamsungOperator
        implements Observable.OnSubscribe<PurchaseVo>
{
    @NonNull protected final String groupId;
    @NonNull protected final String itemId;
    protected final boolean showSuccessDialog;

    //<editor-fold desc="Constructors">
    public SamsungPaymentOperator(
            @NonNull SamsungIapHelper mIapHelper,
            @NonNull String groupId,
            @NonNull String itemId,
            boolean showSuccessDialog)
    {
        super(mIapHelper);
        this.groupId = groupId;
        this.itemId = itemId;
        this.showSuccessDialog = showSuccessDialog;
    }
    //</editor-fold>

    @Override public void call(Subscriber<? super PurchaseVo> subscriber)
    {
        mIapHelper.startPayment(
                groupId,
                itemId,
                showSuccessDialog,
                (errorVo, purchaseVo) -> {
                    if (errorVo.getErrorCode() == SamsungIapHelper.IAP_ERROR_NONE)
                    {
                        subscriber.onNext(purchaseVo);
                        subscriber.onCompleted();
                    }
                    else
                    {
                        subscriber.onError(new SamsungPurchaseException(
                                errorVo,
                                groupId,
                                itemId,
                                showSuccessDialog));
                    }
                });
    }
}
