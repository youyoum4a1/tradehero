package com.tradehero.th.fragments.live;

import android.app.Activity;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.models.kyc.KYCForm;
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

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        kycFormSubject.onNext(kycFormPreference.get());
    }

    @CallSuper public void onNext(@NonNull KYCForm kycForm)
    {
        KYCForm previous = kycFormPreference.get();
        kycFormPreference.set(kycForm);
        if (!previous.hasSameFields(kycForm))
        {
            kycFormSubject.onNext(kycForm);
        }
    }

    @NonNull public Observable<KYCForm> getKycFormObservable()
    {
        return kycFormSubject.asObservable();
    }
}
