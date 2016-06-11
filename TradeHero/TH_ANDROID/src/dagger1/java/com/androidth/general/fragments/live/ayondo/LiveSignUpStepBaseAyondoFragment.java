package com.androidth.general.fragments.live.ayondo;

import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.androidth.general.R;
import com.androidth.general.api.kyc.BrokerApplicationDTO;
import com.androidth.general.api.kyc.KYCFormOptionsDTO;
import com.androidth.general.api.kyc.KYCFormOptionsId;
import com.androidth.general.api.kyc.StepStatus;
import com.androidth.general.api.kyc.ayondo.KYCAyondoForm;
import com.androidth.general.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.androidth.general.api.live.LiveBrokerDTO;
import com.androidth.general.api.live.LiveBrokerId;
import com.androidth.general.api.live.LiveBrokerSituationDTO;
import com.androidth.general.common.rx.PairGetSecond;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.fragments.live.LiveSignUpStepBaseFragment;
import com.androidth.general.network.service.LiveServiceWrapper;
import com.androidth.general.persistence.prefs.LiveBrokerSituationPreference;
import com.androidth.general.rx.view.adapter.OnItemSelectedEvent;
import com.androidth.general.rx.view.adapter.OnNothingSelectedEvent;
import com.androidth.general.rx.view.adapter.OnSelectedEvent;
import com.fernandocejas.frodo.annotation.RxLogObservable;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import timber.log.Timber;

abstract public class LiveSignUpStepBaseAyondoFragment extends LiveSignUpStepBaseFragment
{
    @Inject protected LiveBrokerSituationPreference liveBrokerSituationPreference;
    @Inject LiveServiceWrapper liveServiceWrapper;

    @Nullable private ConnectableObservable<KYCAyondoFormOptionsDTO> kycAyondoFormOptionsObservable;
    private Subscription kycAyondoFormOptionsSubscription;

    @NonNull @RxLogObservable protected Observable<LiveBrokerSituationDTO> createBrokerSituationObservable()
    {
        return super.createBrokerSituationObservable()
//                .filter(new Func1<LiveBrokerSituationDTO, Boolean>()
//                {
//                    @Override public Boolean call(LiveBrokerSituationDTO situationDTO)
//                    {
//                        return situationDTO.kycForm instanceof KYCAyondoForm;
//                    }
//                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<LiveBrokerSituationDTO>()
                {
                    @Override public void call(LiveBrokerSituationDTO situationDTO)
                    {
                        if (situationDTO.kycForm != null && situationDTO.kycForm instanceof KYCAyondoForm)
                        {
                            onNextButtonEnabled(((KYCAyondoForm) situationDTO.kycForm));
                        }
                    }
                })
                .observeOn(Schedulers.io());
    }

    @MainThread
    protected void onNextButtonEnabled(KYCAyondoForm kycForm)
    {
        List<StepStatus> stepStatuses = kycForm.getStepStatuses();
        onNextButtonEnabled(stepStatuses);
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

    @Override
    public void onResume() {
        super.onResume();
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
        onDestroySubscriptions.add(Observable.merge(
                (btnNext != null ? ViewObservable.clicks(btnNext) : Observable.<OnClickEvent>empty())
                        .doOnNext(new Action1<OnClickEvent>()
                        {
                            @Override public void call(OnClickEvent onClickEvent)
                            {
                                prevNextSubject.onNext(true);
                            }
                        }),
                (btnPrev != null ? ViewObservable.clicks(btnPrev) : Observable.<OnClickEvent>empty())
                        .doOnNext(new Action1<OnClickEvent>()
                        {
                            @Override public void call(OnClickEvent onClickEvent)
                            {
                                prevNextSubject.onNext(false);
                            }
                        }))
                .withLatestFrom(liveBrokerSituationDTOObservable, new Func2<OnClickEvent, LiveBrokerSituationDTO, KYCAyondoForm>()
                {
                    @Override public KYCAyondoForm call(OnClickEvent onClickEvent, LiveBrokerSituationDTO liveBrokerSituationDTO)
                    {
                        return (KYCAyondoForm) liveBrokerSituationDTO.kycForm;
                    }
                })
                .flatMap(new Func1<KYCAyondoForm, Observable<BrokerApplicationDTO>>()
                {
                    @Override public Observable<BrokerApplicationDTO> call(KYCAyondoForm kycAyondoForm)
                    {
                        return liveServiceWrapper.createOrUpdateLead(kycAyondoForm)
                                .onErrorResumeNext(
                            new Func1<Throwable, Observable<? extends BrokerApplicationDTO>>()
                            {
                                @Override public Observable<? extends BrokerApplicationDTO> call(Throwable throwable)
                                {
                                    if (throwable.getCause() instanceof IOException)
                                    {
                                        THToast.show(R.string.error_no_internet_connection);
                                    }

                                    return Observable.empty();
                                }
                            });
                    }
                })
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

        kycAyondoFormOptionsObservable = createKYCAyondoFormOptionsObservable(kycFormOptionsDTOObservable).publish();

//        kycAyondoFormOptionsObservable.connect();
        Log.v("ayondoStep1", "KYCayondoformoptions "+kycAyondoFormOptionsObservable);
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

    @NonNull @RxLogObservable
    protected Observable<KYCAyondoFormOptionsDTO> createKYCAyondoFormOptionsObservable(Observable<KYCFormOptionsDTO> kycFormOptionsDTOObservable)
    {
        return kycFormOptionsDTOObservable
//                .filter(new Func1<KYCFormOptionsDTO, Boolean>()
//                {
//                    @Override public Boolean call(KYCFormOptionsDTO kycFormOptionsDTO)
//                    {
//                        return kycFormOptionsDTO instanceof KYCAyondoFormOptionsDTO;
//                    }
//                })
                .cast(KYCAyondoFormOptionsDTO.class);


//        return super.createKYCFormOptionsObservable(kycFormOptionsDTOObservable)
//                .filter(new Func1<KYCFormOptionsDTO, Boolean>()
//                {
//                    @Override public Boolean call(KYCFormOptionsDTO kycFormOptionsDTO)
//                    {
//                        return kycFormOptionsDTO instanceof KYCAyondoFormOptionsDTO;
//                    }
//                })
//                .cast(KYCAyondoFormOptionsDTO.class);
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
