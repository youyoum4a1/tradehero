package com.tradehero.th.fragments.live.ayondo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.fragments.live.LiveSignUpStepBaseFragment;
import com.tradehero.th.models.kyc.KYCForm;
import com.tradehero.th.models.kyc.ayondo.KYCAyondoForm;
import rx.Observable;
import rx.functions.Func1;

abstract public class LiveSignUpStepBaseAyondoFragment extends LiveSignUpStepBaseFragment
{
    @Nullable private Observable<KYCAyondoForm> kycAyondoFormObservable;

    @NonNull public Observable<KYCAyondoForm> getKycAyondoFormObservable()
    {
        Observable<KYCAyondoForm> copy = kycAyondoFormObservable;
        if (copy == null)
        {
            copy = createKycAyondoFormObservable();
            kycAyondoFormObservable = copy;
        }
        return copy;
    }

    @NonNull public Observable<KYCAyondoForm> createKycAyondoFormObservable()
    {
        return getKycFormObservable()
                .filter(new Func1<KYCForm, Boolean>()
                {
                    @Override public Boolean call(KYCForm kycForm)
                    {
                        return kycForm instanceof KYCAyondoForm;
                    }
                })
                .cast(KYCAyondoForm.class)
                .share();
    }
}
