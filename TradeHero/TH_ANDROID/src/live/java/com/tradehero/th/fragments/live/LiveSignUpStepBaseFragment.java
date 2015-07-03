package com.tradehero.th.fragments.live;

import android.support.annotation.NonNull;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.models.kyc.KYCForm;
import com.tradehero.th.models.kyc.ayondo.KYCAyondoForm;
import com.tradehero.th.persistence.prefs.KYCFormPreference;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.BehaviorSubject;

abstract public class LiveSignUpStepBaseFragment extends BaseFragment
{
    @Inject KYCFormPreference kycFormPreference;

    @NonNull private final BehaviorSubject<KYCForm> kycFormSubject;

    public LiveSignUpStepBaseFragment()
    {
        this.kycFormSubject = BehaviorSubject.create();
    }

    @NonNull protected KYCAyondoForm getKYCForm()
    {
        return (KYCAyondoForm) kycFormPreference.get();
    }

    abstract public void onNext(@NonNull KYCForm kycForm);

    @NonNull public Observable<KYCForm> getKycFormObservable()
    {
        return kycFormSubject.asObservable();
    }
}
