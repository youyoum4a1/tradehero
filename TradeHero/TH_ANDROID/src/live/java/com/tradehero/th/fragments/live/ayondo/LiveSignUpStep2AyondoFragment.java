package com.tradehero.th.fragments.live.ayondo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.api.live.KYCFormOptionsDTO;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.fragments.live.LiveSignUpStepBaseFragment;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class LiveSignUpStep2AyondoFragment extends LiveSignUpStepBaseFragment
{
    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_sign_up_live_ayondo_step_2, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        onDestroyViewSubscriptions.add(
                getKYCFormOptionsObservable()
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(new Action1<KYCFormOptionsDTO>()
                        {
                            @Override public void call(KYCFormOptionsDTO kycFormOptionsDTO)
                            {

                            }
                        })
                        .flatMap(new Func1<KYCFormOptionsDTO, Observable<LiveBrokerSituationDTO>>()
                        {
                            @Override public Observable<LiveBrokerSituationDTO> call(KYCFormOptionsDTO kycFormOptionsDTO)
                            {
                                return getBrokerSituationObservable();
                            }
                        })
                        .subscribe(new Action1<LiveBrokerSituationDTO>()
                        {
                            @Override public void call(LiveBrokerSituationDTO liveBrokerSituationDTO)
                            {

                            }
                        })
        );
    }
}
