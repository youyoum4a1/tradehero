package com.tradehero.th.fragments.live.ayondo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.kyc.KYCFormOptionsDTO;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.tradehero.th.fragments.live.LiveSignUpStepBaseFragment;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoForm;
import rx.Observable;
import rx.functions.Func1;

abstract public class LiveSignUpStepBaseAyondoFragment extends LiveSignUpStepBaseFragment
{
    @Nullable private Observable<KYCAyondoFormOptionsDTO> kycAyondoFormOptionsObservable;

    @NonNull protected Observable<LiveBrokerSituationDTO> createBrokerSituationObservable()
    {
        return super.createBrokerSituationObservable()
                .filter(new Func1<LiveBrokerSituationDTO, Boolean>()
                {
                    @Override public Boolean call(LiveBrokerSituationDTO situationDTO)
                    {
                        return situationDTO.kycForm instanceof KYCAyondoForm;
                    }
                });
    }

    @NonNull public Observable<KYCAyondoFormOptionsDTO> getKYCAyondoFormOptionsObservable()
    {
        Observable<KYCAyondoFormOptionsDTO> copy = kycAyondoFormOptionsObservable;
        if (copy == null)
        {
            copy = createKYCAyondoFormOptionsObservable().share().cache(1);
            kycAyondoFormOptionsObservable = copy;
        }
        return copy;
    }

    @NonNull protected Observable<KYCAyondoFormOptionsDTO> createKYCAyondoFormOptionsObservable()
    {
        return getKYCFormOptionsObservable()
                .filter(new Func1<KYCFormOptionsDTO, Boolean>()
                {
                    @Override public Boolean call(KYCFormOptionsDTO kycFormOptionsDTO)
                    {
                        return kycFormOptionsDTO instanceof KYCAyondoFormOptionsDTO;
                    }
                })
                .cast(KYCAyondoFormOptionsDTO.class);
    }
}
