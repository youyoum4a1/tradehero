package com.tradehero.th.fragments.live;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.models.kyc.KYCForm;
import rx.Observable;
import rx.subjects.BehaviorSubject;

abstract public class LiveSignUpStepBaseFragment extends BaseFragment
{
    @NonNull private final BehaviorSubject<KYCForm> kycFormSubject;

    public LiveSignUpStepBaseFragment()
    {
        this.kycFormSubject = BehaviorSubject.create();
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_live_action_screen, container, false);
    }

    abstract public void onNext(@NonNull KYCForm kycForm);

    @NonNull public Observable<KYCForm> getKycFormObservable()
    {
        return kycFormSubject.asObservable();
    }
}
