package com.tradehero.th.models.kyc;

import android.support.annotation.NonNull;
import com.tradehero.th.models.kyc.ayondo.KYCAyondoForm;
import rx.Observable;
import rx.schedulers.Schedulers;

public class KYCFormFactory
{
    @NonNull public static Observable<KYCForm> createDefaultForm() // Eventually, the server may return an empty form
    {
        return Observable.<KYCForm>just(new KYCAyondoForm())
                .subscribeOn(Schedulers.computation());
    }
}
