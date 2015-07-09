package com.tradehero.th.fragments.live;

import android.app.Activity;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.persistence.prefs.LiveBrokerSituationPreference;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.BehaviorSubject;

abstract public class LiveSignUpStepBaseFragment extends BaseFragment
{
    @Inject LiveBrokerSituationPreference liveBrokerSituationPreference;

    @NonNull private final BehaviorSubject<LiveBrokerSituationDTO> brokerSituationSubject;

    public LiveSignUpStepBaseFragment()
    {
        this.brokerSituationSubject = BehaviorSubject.create();
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        brokerSituationSubject.onNext(liveBrokerSituationPreference.get());
    }

    @CallSuper public void onNext(@NonNull LiveBrokerSituationDTO situationDTO)
    {
        LiveBrokerSituationDTO previous = liveBrokerSituationPreference.get();
        liveBrokerSituationPreference.set(situationDTO);
        if (!previous.hasSameFields(situationDTO))
        {
            brokerSituationSubject.onNext(situationDTO);
        }
    }

    @NonNull public Observable<LiveBrokerSituationDTO> getBrokerSituationObservable()
    {
        return brokerSituationSubject.asObservable();
    }
}
