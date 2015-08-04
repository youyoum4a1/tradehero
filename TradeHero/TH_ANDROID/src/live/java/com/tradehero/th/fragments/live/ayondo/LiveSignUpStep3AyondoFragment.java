package com.tradehero.th.fragments.live.ayondo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Spinner;
import butterknife.Bind;
import com.tradehero.th.R;
import com.tradehero.th.api.kyc.TradingPerQuarter;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoForm;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.tradehero.th.api.live.LiveBrokerDTO;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.rx.view.adapter.AdapterViewObservable;
import com.tradehero.th.rx.view.adapter.OnSelectedEvent;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnCheckedChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

public class LiveSignUpStep3AyondoFragment extends LiveSignUpStepBaseAyondoFragment
{
    @Bind(R.id.info_trading_per_quarter) Spinner tradingPerQuarterSpinner;
    @Bind(R.id.worked_finance) CompoundButton workInFinanceButton;
    @Bind(R.id.attended_seminar_ayondo) CompoundButton attendedSeminarAyondoButton;
    @Bind(R.id.have_other_qualification) CompoundButton haveOtherQualificationButton;
    @Bind(R.id.traded_shares_bonds) CompoundButton tradedSharesBondsButton;
    @Bind(R.id.traded_otc_derivative) CompoundButton tradedOtcDerivativeButton;
    @Bind(R.id.traded_etc) CompoundButton tradedEtcButton;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_sign_up_live_ayondo_step_3, container, false);
    }

    @Override protected List<Subscription> onInitAyondoSubscription(Observable<LiveBrokerDTO> brokerDTOObservable,
            Observable<LiveBrokerSituationDTO> liveBrokerSituationDTOObservable,
            Observable<KYCAyondoFormOptionsDTO> kycAyondoFormOptionsDTOObservable)
    {
        List<Subscription> subscriptions = new ArrayList<>();

        subscriptions.add(Observable.combineLatest(
                liveBrokerSituationDTOObservable
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnNext(new Action1<LiveBrokerSituationDTO>()
                        {
                            @Override public void call(LiveBrokerSituationDTO situationDTO)
                            {
                                //noinspection ConstantConditions
                                onNext(new LiveBrokerSituationDTO(
                                        situationDTO.broker,
                                        populate((KYCAyondoForm) situationDTO.kycForm)));
                            }
                        }),
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
                        onNext(new LiveBrokerSituationDTO(situationDTO.broker,
                                populateTradingPerQuarter((KYCAyondoForm) situationDTO.kycForm,
                                        kycFormOptionsDTO.tradingPerQuarterOptions)));
                        return null;
                    }
                })
                .subscribe(
                        new EmptyAction1<>(),
                        new TimberOnErrorAction1("Failed to populate AyondoStep2 spinners")));

        subscriptions.add(Observable.combineLatest(
                brokerDTOObservable,
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
                        WidgetObservable.input(tradedSharesBondsButton)
                                .map(new Func1<OnCheckedChangeEvent, KYCAyondoForm>()
                                {
                                    @Override public KYCAyondoForm call(OnCheckedChangeEvent onCheckedChangeEvent)
                                    {
                                        return KYCAyondoFormFactory.fromTradedSharesBondsEvent(onCheckedChangeEvent);
                                    }
                                }),
                        WidgetObservable.input(tradedOtcDerivativeButton)
                                .map(new Func1<OnCheckedChangeEvent, KYCAyondoForm>()
                                {
                                    @Override public KYCAyondoForm call(OnCheckedChangeEvent onCheckedChangeEvent)
                                    {
                                        return KYCAyondoFormFactory.fromTradedOtcDerivativesEvent(onCheckedChangeEvent);
                                    }
                                }),
                        WidgetObservable.input(tradedEtcButton)
                                .map(new Func1<OnCheckedChangeEvent, KYCAyondoForm>()
                                {
                                    @Override public KYCAyondoForm call(OnCheckedChangeEvent onCheckedChangeEvent)
                                    {
                                        return KYCAyondoFormFactory.fromTradedExchangeDerivativesEvent(onCheckedChangeEvent);
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
                                })),
                new Func2<LiveBrokerDTO, KYCAyondoForm, LiveBrokerSituationDTO>()
                {
                    @Override public LiveBrokerSituationDTO call(LiveBrokerDTO brokerDTO, KYCAyondoForm update)
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
                        new TimberOnErrorAction1("Failed to listen to compound buttons")));
        return subscriptions;
    }

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
        if (tradeSharesBonds != null)
        {
            tradedSharesBondsButton.setChecked(tradeSharesBonds);
        }
        else
        {
            update.setTradedSharesBonds(tradedSharesBondsButton.isChecked());
        }

        Boolean tradedOtc = kycForm.isTradedOtcDerivative();
        if (tradedOtc != null)
        {
            tradedOtcDerivativeButton.setChecked(tradedOtc);
        }
        else
        {
            update.setTradedOtcDerivative(tradedOtcDerivativeButton.isChecked());
        }

        Boolean tradedEtc = kycForm.isTradedEtc();
        if (tradedEtc != null)
        {
            tradedEtcButton.setChecked(tradedEtc);
        }
        else
        {
            update.setTradedEtc(tradedEtcButton.isChecked());
        }

        return update;
    }

    @NonNull protected KYCAyondoForm populateTradingPerQuarter(
            @NonNull KYCAyondoForm kycForm,
            @NonNull List<TradingPerQuarter> tradingPerQuarters)
    {
        KYCAyondoForm update = new KYCAyondoForm();
        TradingPerQuarter savedTrading = kycForm.getTradingPerQuarter();
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
