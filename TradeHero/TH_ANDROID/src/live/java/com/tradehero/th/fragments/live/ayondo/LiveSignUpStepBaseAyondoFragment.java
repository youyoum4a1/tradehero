package com.tradehero.th.fragments.live.ayondo;

import android.support.annotation.NonNull;
import com.tradehero.th.fragments.live.LiveSignUpStepBaseFragment;
import com.tradehero.th.models.kyc.KYCForm;
import com.tradehero.th.models.kyc.ayondo.KYCAyondoForm;
import rx.Observable;
import rx.functions.Func1;

abstract public class LiveSignUpStepBaseAyondoFragment extends LiveSignUpStepBaseFragment
{
    @NonNull public Observable<KYCAyondoForm> getKycAyondoFormObservable()
    {
        return getKycFormObservable()
                .filter(new Func1<KYCForm, Boolean>()
                {
                    @Override public Boolean call(KYCForm kycForm)
                    {
                        return kycForm instanceof KYCAyondoForm;
                    }
                })
                .cast(KYCAyondoForm.class);
    }
}
