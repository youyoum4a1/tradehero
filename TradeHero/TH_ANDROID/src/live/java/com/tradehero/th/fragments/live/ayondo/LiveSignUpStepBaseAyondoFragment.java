package com.tradehero.th.fragments.live.ayondo;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.kyc.BrokerApplicationDTO;
import com.tradehero.th.api.kyc.KYCForm;
import com.tradehero.th.api.kyc.KYCFormOptionsDTO;
import com.tradehero.th.api.kyc.StepStatus;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoForm;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.tradehero.th.api.live.LiveBrokerDTO;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.fragments.live.LiveSignUpStepBaseFragment;
import com.tradehero.th.network.service.LiveServiceWrapper;
import com.tradehero.th.persistence.prefs.LiveBrokerSituationPreference;
import com.tradehero.th.rx.view.adapter.OnItemSelectedEvent;
import com.tradehero.th.rx.view.adapter.OnNothingSelectedEvent;
import com.tradehero.th.rx.view.adapter.OnSelectedEvent;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

abstract public class LiveSignUpStepBaseAyondoFragment extends LiveSignUpStepBaseFragment
{
    @Inject protected LiveBrokerSituationPreference liveBrokerSituationPreference;
    @Inject LiveServiceWrapper liveServiceWrapper;

    @Nullable private ConnectableObservable<KYCAyondoFormOptionsDTO> kycAyondoFormOptionsObservable;
    private Subscription kycAyondoFormOptionsSubscription;

    @NonNull protected Observable<LiveBrokerSituationDTO> createBrokerSituationObservable()
    {
        return super.createBrokerSituationObservable()
                .filter(new Func1<LiveBrokerSituationDTO, Boolean>()
                {
                    @Override public Boolean call(LiveBrokerSituationDTO situationDTO)
                    {
                        return situationDTO.kycForm instanceof KYCAyondoForm;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<LiveBrokerSituationDTO>()
                {
                    @Override public void call(LiveBrokerSituationDTO situationDTO)
                    {
                        List<StepStatus> stepStatuses = situationDTO.kycForm == null ? null : situationDTO.kycForm.getStepStatuses();
                        onNextButtonEnabled(stepStatuses);
                    }
                })
                .observeOn(Schedulers.io());
    }

    @MainThread
    protected void onNextButtonEnabled(List<StepStatus> stepStatuses)
    {
        //We only check against the first status to enable/disable next button for all steps
        //The other statuses are being ignored.
        StepStatus firstStatus = stepStatuses == null || stepStatuses.size() == 0 ? null : stepStatuses.get(0);
        if (btnNext != null)
        {
            // That's right, the first status decides for all Next buttons
            btnNext.setEnabled(firstStatus != null && firstStatus.equals(StepStatus.COMPLETE));
        }
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

    @Override protected final void onNextPressed()
    {
        //Send form to server to create lead
        KYCForm kycForm = liveBrokerSituationPreference.get().kycForm;
        onNextPressed((KYCAyondoForm) kycForm);
    }

    protected void onNextPressed(KYCAyondoForm kycAyondoForm)
    {
        onDestroySubscriptions.add(
                liveServiceWrapper.createOrUpdateLead(kycAyondoForm)
                        .subscribe(new Action1<BrokerApplicationDTO>()
                        {
                            @Override public void call(BrokerApplicationDTO brokerApplicationDTO)
                            {
                                Timber.d("broker application %s: ", brokerApplicationDTO);
                            }
                        }, new Action1<Throwable>()
                        {
                            @Override public void call(Throwable throwable)
                            {
                                Timber.e(throwable, "Error in creating lead");
                            }
                        }));
    }
}
