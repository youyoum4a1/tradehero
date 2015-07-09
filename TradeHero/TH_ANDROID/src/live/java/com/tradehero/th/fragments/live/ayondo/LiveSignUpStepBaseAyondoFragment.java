package com.tradehero.th.fragments.live.ayondo;

import android.support.annotation.NonNull;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.fragments.live.LiveSignUpStepBaseFragment;
import com.tradehero.th.models.kyc.ayondo.KYCAyondoForm;
import rx.Observable;
import rx.functions.Func1;

abstract public class LiveSignUpStepBaseAyondoFragment extends LiveSignUpStepBaseFragment
{
    @NonNull public Observable<LiveBrokerSituationDTO> getBrokerSituationObservable()
    {
        return super.getBrokerSituationObservable()
                .filter(new Func1<LiveBrokerSituationDTO, Boolean>()
                {
                    @Override public Boolean call(LiveBrokerSituationDTO situationDTO)
                    {
                        return situationDTO.kycForm instanceof KYCAyondoForm;
                    }
                })
                .share();
    }
}
