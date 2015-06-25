package com.tradehero.th.models.kyc;

import android.support.annotation.NonNull;
import com.tradehero.th.models.kyc.ayondo.KYCAyondoForm;
import rx.Observable;
import rx.schedulers.Schedulers;

@Deprecated // Eventually, the server may return an empty form
public class KYCFormFactory
{
    @NonNull public static Observable<KYCForm> createDefaultForm()
    {
        return Observable.<KYCForm>just(new KYCAyondoForm())
                .subscribeOn(Schedulers.computation());
    }
}
