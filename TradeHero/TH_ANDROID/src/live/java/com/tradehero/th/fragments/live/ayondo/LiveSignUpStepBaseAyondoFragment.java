package com.tradehero.th.fragments.live.ayondo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.kyc.KYCFormOptionsDTO;
import com.tradehero.th.api.kyc.StepStatus;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoForm;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.tradehero.th.api.live.LiveBrokerDTO;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.fragments.live.LiveSignUpStepBaseFragment;
import com.tradehero.th.rx.view.adapter.OnItemSelectedEvent;
import com.tradehero.th.rx.view.adapter.OnNothingSelectedEvent;
import com.tradehero.th.rx.view.adapter.OnSelectedEvent;
import java.util.Collections;
import java.util.List;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

abstract public class LiveSignUpStepBaseAyondoFragment extends LiveSignUpStepBaseFragment
{
    @Nullable private ConnectableObservable<KYCAyondoFormOptionsDTO> kycAyondoFormOptionsObservable;
    private Subscription kycAyondoFormOptionsSubscription;

    @NonNull protected Observable<LiveBrokerSituationDTO> createBrokerSituationObservable()
    {
        return super.createBrokerSituationObservable()
                .observeOn(AndroidSchedulers.mainThread())
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
                })
                .observeOn(Schedulers.io());
    }

    @Override public void onDestroyView()
    {
        kycAyondoFormOptionsObservable = null;
        super.onDestroyView();
    }

    @NonNull
    @Override protected final List<Subscription> onInitSubscription(Observable<LiveBrokerDTO> brokerDTOObservable,
            Observable<LiveBrokerSituationDTO> liveBrokerSituationDTOObservable, Observable<KYCFormOptionsDTO> kycFormOptionsDTOObservable)
    {
        kycAyondoFormOptionsObservable = createKYCAyondoFormOptionsObservable(kycFormOptionsDTOObservable).publish();
        return this.onInitAyondoSubscription(brokerDTOObservable, liveBrokerSituationDTOObservable, kycAyondoFormOptionsObservable);
    }

    protected List<Subscription> onInitAyondoSubscription(Observable<LiveBrokerDTO> brokerDTOObservable,
            Observable<LiveBrokerSituationDTO> liveBrokerSituationDTOObservable,
            Observable<KYCAyondoFormOptionsDTO> kycAyondoFormOptionsDTOObservable)
    {
        return Collections.emptyList();
    }

    @Override protected void onConnectObservables()
    {
        if (kycAyondoFormOptionsObservable != null)
        {
            kycAyondoFormOptionsSubscription = kycAyondoFormOptionsObservable.connect();
        }
        super.onConnectObservables();
    }

    @Override protected void onDisconnectObservables()
    {
        if (kycAyondoFormOptionsSubscription != null)
        {
            kycAyondoFormOptionsSubscription.unsubscribe();
        }
        super.onDisconnectObservables();
    }

    @NonNull
    protected Observable<KYCAyondoFormOptionsDTO> createKYCAyondoFormOptionsObservable(Observable<KYCFormOptionsDTO> kycFormOptionsDTOObservable)
    {
        return kycFormOptionsDTOObservable
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
