package com.androidth.general.fragments.live.ayondo;

import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.androidth.general.api.kyc.StepStatus;
import com.androidth.general.api.kyc.TradingPerQuarter;
import com.androidth.general.api.kyc.ayondo.KYCAyondoForm;
import com.androidth.general.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.androidth.general.api.live.LiveBrokerDTO;
import com.androidth.general.api.live.LiveBrokerSituationDTO;
import com.androidth.general.fragments.base.LollipopArrayAdapter;
import com.androidth.general.rx.EmptyAction1;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.rx.view.adapter.AdapterViewObservable;
import com.androidth.general.rx.view.adapter.OnSelectedEvent;
import com.androidth.general.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnCheckedChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;

public class LiveSignUpStep3AyondoFragment extends LiveSignUpStepBaseAyondoFragment
{
    @BindView(R.id.info_trading_per_quarter) Spinner tradingPerQuarterSpinner;
    @BindView(R.id.worked_finance) CompoundButton workInFinanceButton;
    @BindView(R.id.attended_seminar_ayondo) CompoundButton attendedSeminarAyondoButton;
    @BindView(R.id.have_other_qualification) CompoundButton haveOtherQualificationButton;
    @BindView(R.id.traded_shares_bonds) CompoundButton tradedSharesBondsButton;
    @BindView(R.id.traded_otc_derivative) CompoundButton tradedOtcDerivativeButton;
    @BindView(R.id.traded_etc) CompoundButton tradedEtcButton;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_sign_up_live_ayondo_step_3, container, false);
    }

    @Override protected void onNextButtonEnabled(List<StepStatus> stepStatuses)
    {
        StepStatus thirdStatus = stepStatuses == null || stepStatuses.size() == 0 ? null : stepStatuses.get(2);
        if (btnNext != null)
        {
//            btnNext.setEnabled(thirdStatus != null && thirdStatus.equals(StepStatus.COMPLETE));
            btnNext.setEnabled(true);
        }
    }

    @Override protected List<Subscription> onInitAyondoSubscription(Observable<LiveBrokerDTO> brokerDTOObservable,
            Observable<LiveBrokerSituationDTO> liveBrokerSituationDTOObservable,
            Observable<KYCAyondoFormOptionsDTO> kycAyondoFormOptionsDTOObservable)
    {
        List<Subscription> subscriptions = new ArrayList<>();

        subscriptions.add(Observable.zip(
                liveBrokerSituationDTOObservable
                        .observeOn(AndroidSchedulers.mainThread())
                        .take(1),
                kycAyondoFormOptionsDTOObservable
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(new Action1<KYCAyondoFormOptionsDTO>()
                        {
                            @Override public void call(KYCAyondoFormOptionsDTO kycAyondoFormOptionsDTO)
                            {
                                LollipopArrayAdapter<TradingPerQuarterDTO> tradingPerQuarterAdapter =
                                        new LollipopArrayAdapter<>(getActivity(),
                                                TradingPerQuarterDTO.createList(getResources(),
                                                        kycAyondoFormOptionsDTO.tradingPerQuarterOptions));
                                tradingPerQuarterSpinner.setAdapter(tradingPerQuarterAdapter);
                            }
                        }),
                new Func2<LiveBrokerSituationDTO, KYCAyondoFormOptionsDTO, Object>()
                {
                    @Override
                    public Object call(LiveBrokerSituationDTO situationDTO, KYCAyondoFormOptionsDTO kycFormOptionsDTO)
                    {
                        //noinspection ConstantConditions
                        populateTradingPerQuarter((KYCAyondoForm) situationDTO.kycForm,
                                kycFormOptionsDTO.tradingPerQuarterOptions);
                        populate(((KYCAyondoForm) situationDTO.kycForm));
                        return null;
                    }
                })
                .subscribe(
                        new EmptyAction1<>(),
                        new TimberOnErrorAction1("Failed to populate AyondoStep2 spinners")));

        subscriptions.add(
                Observable.merge(
                        WidgetObservable.input(workInFinanceButton)
                                .map(new Func1<OnCheckedChangeEvent, KYCAyondoForm>()
                                {
                                    @Override public KYCAyondoForm call(OnCheckedChangeEvent onCheckedChangeEvent)
                                    {
                                        return KYCAyondoFormFactory.fromWorkedInFinance1YearEvent(onCheckedChangeEvent);
                                    }
                                }),
                        WidgetObservable.input(attendedSeminarAyondoButton)
                                .map(new Func1<OnCheckedChangeEvent, KYCAyondoForm>()
                                {
                                    @Override public KYCAyondoForm call(OnCheckedChangeEvent onCheckedChangeEvent)
                                    {
                                        return KYCAyondoFormFactory.fromAttendedSeminarAyondoEvent(onCheckedChangeEvent);
                                    }
                                }),
                        WidgetObservable.input(haveOtherQualificationButton)
                                .map(new Func1<OnCheckedChangeEvent, KYCAyondoForm>()
                                {
                                    @Override public KYCAyondoForm call(OnCheckedChangeEvent onCheckedChangeEvent)
                                    {
                                        return KYCAyondoFormFactory.fromHaveOtherQualificationEvent(onCheckedChangeEvent);
                                    }
                                }),
                        Observable.combineLatest(
                                WidgetObservable.input(tradedSharesBondsButton, true),
                                WidgetObservable.input(tradedOtcDerivativeButton, true),
                                WidgetObservable.input(tradedEtcButton, true),
                                new Func3<OnCheckedChangeEvent, OnCheckedChangeEvent, OnCheckedChangeEvent, KYCAyondoForm>()
                                {

                                    @Override public KYCAyondoForm call(OnCheckedChangeEvent onTradedSharesBondsCheckedChangeEvent,
                                            OnCheckedChangeEvent onTradedOtcDerivativeCheckedChangeEvent,
                                            OnCheckedChangeEvent onTradedEtcCheckedChangeEvent)
                                    {

                                        return KYCAyondoFormFactory.fromProductsTradedEvent(onTradedSharesBondsCheckedChangeEvent, onTradedOtcDerivativeCheckedChangeEvent, onTradedEtcCheckedChangeEvent);
                                    }
                                }),
                        AdapterViewObservable.selects(tradingPerQuarterSpinner)
                                .distinctUntilChanged(createSpinnerDistinctByPosition())
                                .map(new Func1<OnSelectedEvent, KYCAyondoForm>()
                                {
                                    @Override public KYCAyondoForm call(OnSelectedEvent onSelectedEvent)
                                    {
                                        return KYCAyondoFormFactory.fromTradingPerQuarterEvent(onSelectedEvent);
                                    }
                                }))
                .withLatestFrom(brokerDTOObservable, new Func2<KYCAyondoForm, LiveBrokerDTO, LiveBrokerSituationDTO>()
                {
                    @Override public LiveBrokerSituationDTO call(KYCAyondoForm update, LiveBrokerDTO brokerDTO)
                    {
                        return new LiveBrokerSituationDTO(brokerDTO, update);
                    }
                })
                .subscribe(
                        new Action1<LiveBrokerSituationDTO>()
                        {
                            @Override public void call(LiveBrokerSituationDTO update)
                            {
                                onNext(update);
                            }
                        },
                        new TimberOnErrorAction1("Failed to listen to compound buttons"))
        );
        return subscriptions;
    }

    @MainThread
    @NonNull protected KYCAyondoForm populate(@NonNull KYCAyondoForm kycForm)
    {
        KYCAyondoForm update = new KYCAyondoForm();
        Boolean workedInFinance = kycForm.isWorkedInFinance1Year();
        if (workedInFinance != null)
        {
            workInFinanceButton.setChecked(workedInFinance);
        }
        else
        {
            update.setWorkedInFinance1Year(workInFinanceButton.isChecked());
        }

        Boolean attendedSeminar = kycForm.isAttendedSeminarAyondo();
        if (attendedSeminar != null)
        {
            attendedSeminarAyondoButton.setChecked(attendedSeminar);
        }
        else
        {
            update.setAttendedSeminarAyondo(attendedSeminarAyondoButton.isChecked());
        }

        Boolean otherQualification = kycForm.isHaveOtherQualification();
        if (otherQualification != null)
        {
            haveOtherQualificationButton.setChecked(otherQualification);
        }
        else
        {
            update.setHaveOtherQualification(haveOtherQualificationButton.isChecked());
        }

        Boolean tradeSharesBonds = kycForm.isTradedSharesBonds();
        tradedSharesBondsButton.setChecked(tradeSharesBonds != null ? tradeSharesBonds : false);

        Boolean tradedOtc = kycForm.isTradedOtcDerivative();
        tradedOtcDerivativeButton.setChecked(tradedOtc != null ? tradedOtc : false);

        Boolean tradedEtc = kycForm.isTradedEtc();
        tradedEtcButton.setChecked(tradedEtc != null ? tradedEtc : false);

        return update;
    }

    @MainThread
    @NonNull protected KYCAyondoForm populateTradingPerQuarter(
            @NonNull KYCAyondoForm kycForm,
            @NonNull List<TradingPerQuarter> tradingPerQuarters)
    {
        KYCAyondoForm update = new KYCAyondoForm();
        TradingPerQuarter savedTrading = kycForm.getTradingPerQuarter()==TradingPerQuarter.EMPTY?
                TradingPerQuarter.ONE_TO_FIVE: kycForm.getTradingPerQuarter();

        Integer indexTrading = populateSpinner(tradingPerQuarterSpinner,
                savedTrading,
                tradingPerQuarters);
        if (savedTrading == null)
        {
            TradingPerQuarter chosenRange;
            if (indexTrading != null)
            {
                chosenRange = tradingPerQuarters.get(indexTrading);
            }
            else
            {
                chosenRange = ((TradingPerQuarterDTO) tradingPerQuarterSpinner.getSelectedItem()).tradingPerQuarter;
            }

            if (chosenRange != null)
            {
                update.setTradingPerQuarter(chosenRange);
            }
        }
        return update;
    }
}
