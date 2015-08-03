package com.tradehero.th.fragments.live.ayondo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.kyc.KYCFormOptionsDTO;
import com.tradehero.th.api.kyc.StepStatus;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoForm;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.fragments.live.LiveSignUpStepBaseFragment;
import com.tradehero.th.rx.view.adapter.OnItemSelectedEvent;
import com.tradehero.th.rx.view.adapter.OnNothingSelectedEvent;
import com.tradehero.th.rx.view.adapter.OnSelectedEvent;
import java.util.List;
import rx.Observable;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;

abstract public class LiveSignUpStepBaseAyondoFragment extends LiveSignUpStepBaseFragment
{
    @Nullable private ConnectableObservable<KYCAyondoFormOptionsDTO> kycAyondoFormOptionsObservable;

    @NonNull protected Observable<LiveBrokerSituationDTO> createBrokerSituationObservable()
    {
        return super.createBrokerSituationObservable()
                .filter(new Func1<LiveBrokerSituationDTO, Boolean>()
                {
                    @Override public Boolean call(LiveBrokerSituationDTO situationDTO)
                    {
                        List<StepStatus> stepStatuses = situationDTO.kycForm == null ? null : situationDTO.kycForm.getStepStatuses();
                        StepStatus firstStatus = stepStatuses == null || stepStatuses.size() == 0 ? null : stepStatuses.get(0);
                        if (btnNext != null)
                        {
                            // That's right, the first status decides for all Next buttons
                            btnNext.setEnabled(firstStatus != null && firstStatus.equals(StepStatus.COMPLETE));
                        }
                        return situationDTO.kycForm instanceof KYCAyondoForm;
                    }
                });
    }

    @Override public void onDestroyView()
    {
        kycAyondoFormOptionsObservable = null;
        super.onDestroyView();
    }

    @NonNull public ConnectableObservable<KYCAyondoFormOptionsDTO> getKYCAyondoFormOptionsObservable()
    {
        ConnectableObservable<KYCAyondoFormOptionsDTO> copy = kycAyondoFormOptionsObservable;
        if (copy == null)
        {
            copy = createKYCAyondoFormOptionsObservable().publish();
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

    @NonNull protected Func1<OnSelectedEvent, Integer> createSpinnerDistinctByPosition()
    {
        return new Func1<OnSelectedEvent, Integer>()
        {
            @Override public Integer call(OnSelectedEvent onSelectedEvent)
            {
                if (onSelectedEvent instanceof OnNothingSelectedEvent)
                {
                    return -1;
                }
                else if (onSelectedEvent instanceof OnItemSelectedEvent)
                {
                    return ((OnItemSelectedEvent) onSelectedEvent).position;
                }
                throw new IllegalArgumentException("Unhandled argument " + onSelectedEvent);
            }
        };
    }
}
