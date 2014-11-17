package com.tradehero.common.billing.samsung;

import android.support.annotation.NonNull;
import com.sec.android.iap.lib.helper.SamsungIapHelper;
import com.tradehero.common.billing.samsung.exception.SamsungBindException;
import rx.Observable;
import rx.Subscriber;

public class SamsungIapBindOperator extends BaseSamsungOperator
    implements Observable.OnSubscribe<Integer>
{
    //<editor-fold desc="Constructors">
    public SamsungIapBindOperator(@NonNull SamsungIapHelper mIapHelper)
    {
        super(mIapHelper);
    }
    //</editor-fold>

    @Override public void call(Subscriber<? super Integer> subscriber)
    {
        mIapHelper.bindIapService(result -> {
            if (result == SamsungIapHelper.IAP_RESPONSE_RESULT_OK)
            {
                subscriber.onNext(result);
                subscriber.onCompleted();
            }
            else
            {
                subscriber.onError(new SamsungBindException(result));
            }
        });
    }
}
