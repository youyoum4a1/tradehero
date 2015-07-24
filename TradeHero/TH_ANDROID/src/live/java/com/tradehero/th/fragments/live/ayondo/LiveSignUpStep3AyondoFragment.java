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
import butterknife.ButterKnife;
import com.tradehero.th.R;
import com.tradehero.th.api.kyc.TradingPerQuarter;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoForm;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.rx.view.adapter.AdapterViewObservable;
import com.tradehero.th.rx.view.adapter.OnItemSelectedEvent;
import com.tradehero.th.rx.view.adapter.OnSelectedEvent;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnCheckedChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Action1;
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

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        onDestroyViewSubscriptions.add(Observable.combineLatest(
                getBrokerSituationObservable()
                        .doOnNext(new Action1<LiveBrokerSituationDTO>()
                        {
                            @Override public void call(LiveBrokerSituationDTO situationDTO)
                            {
                                //noinspection ConstantConditions
                                populate((KYCAyondoForm) situationDTO.kycForm);
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread()),
                getKYCAyondoFormOptionsObservable()
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
                    public Object call(LiveBrokerSituationDTO liveBrokerSituationDTO, KYCAyondoFormOptionsDTO kycFormOptionsDTO)
                    {
                        //noinspection ConstantConditions
                        populateTradingPerQuarter((KYCAyondoForm) liveBrokerSituationDTO.kycForm, kycFormOptionsDTO.tradingPerQuarterOptions);
                        return null;
                    }
                })
                .subscribe(
                        new EmptyAction1<>(),
                        new TimberOnErrorAction1("Failed to populate AyondoStep2 spinners")));

        onDestroyViewSubscriptions.add(Observable.merge(
                Observable.combineLatest(
                        getBrokerSituationObservable(),
                        WidgetObservable.input(workInFinanceButton),
                        new Func2<LiveBrokerSituationDTO, OnCheckedChangeEvent, LiveBrokerSituationDTO>()
                        {
                            @Override public LiveBrokerSituationDTO call(LiveBrokerSituationDTO situationDTO,
                                    OnCheckedChangeEvent onCheckedChangeEvent)
                            {
                                //noinspection ConstantConditions
                                ((KYCAyondoForm) situationDTO.kycForm).setWorkedInFinance1Year(
                                        onCheckedChangeEvent.value());
                                return situationDTO;
                            }
                        }),
                Observable.combineLatest(
                        getBrokerSituationObservable(),
                        WidgetObservable.input(attendedSeminarAyondoButton),
                        new Func2<LiveBrokerSituationDTO, OnCheckedChangeEvent, LiveBrokerSituationDTO>()
                        {
                            @Override public LiveBrokerSituationDTO call(LiveBrokerSituationDTO situationDTO,
                                    OnCheckedChangeEvent onCheckedChangeEvent)
                            {
                                //noinspection ConstantConditions
                                ((KYCAyondoForm) situationDTO.kycForm).setAttendedSeminarAyondo(onCheckedChangeEvent.value());
                                return situationDTO;
                            }
                        }),
                Observable.combineLatest(
                        getBrokerSituationObservable(),
                        WidgetObservable.input(haveOtherQualificationButton),
                        new Func2<LiveBrokerSituationDTO, OnCheckedChangeEvent, LiveBrokerSituationDTO>()
                        {
                            @Override public LiveBrokerSituationDTO call(LiveBrokerSituationDTO situationDTO,
                                    OnCheckedChangeEvent onCheckedChangeEvent)
                            {
                                //noinspection ConstantConditions
                                ((KYCAyondoForm) situationDTO.kycForm).setHaveOtherQualification(
                                        onCheckedChangeEvent.value());
                                return situationDTO;
                            }
                        }),
                Observable.combineLatest(
                        getBrokerSituationObservable(),
                        WidgetObservable.input(tradedSharesBondsButton),
                        new Func2<LiveBrokerSituationDTO, OnCheckedChangeEvent, LiveBrokerSituationDTO>()
                        {
                            @Override public LiveBrokerSituationDTO call(LiveBrokerSituationDTO situationDTO,
                                    OnCheckedChangeEvent onCheckedChangeEvent)
                            {
                                //noinspection ConstantConditions
                                ((KYCAyondoForm) situationDTO.kycForm).setTradedSharesBonds(onCheckedChangeEvent.value());
                                return situationDTO;
                            }
                        }),
                Observable.combineLatest(
                        getBrokerSituationObservable(),
                        WidgetObservable.input(tradedOtcDerivativeButton),
                        new Func2<LiveBrokerSituationDTO, OnCheckedChangeEvent, LiveBrokerSituationDTO>()
                        {
                            @Override public LiveBrokerSituationDTO call(LiveBrokerSituationDTO situationDTO,
                                    OnCheckedChangeEvent onCheckedChangeEvent)
                            {
                                //noinspection ConstantConditions
                                ((KYCAyondoForm) situationDTO.kycForm).setTradedOtcDerivative(onCheckedChangeEvent.value());
                                return situationDTO;
                            }
                        }),
                Observable.combineLatest(
                        getBrokerSituationObservable(),
                        WidgetObservable.input(tradedEtcButton),
                        new Func2<LiveBrokerSituationDTO, OnCheckedChangeEvent, LiveBrokerSituationDTO>()
                        {
                            @Override public LiveBrokerSituationDTO call(LiveBrokerSituationDTO situationDTO,
                                    OnCheckedChangeEvent onCheckedChangeEvent)
                            {
                                //noinspection ConstantConditions
                                ((KYCAyondoForm) situationDTO.kycForm).setTradedEtc(onCheckedChangeEvent.value());
                                return situationDTO;
                            }
                        }),
                Observable.combineLatest(
                        getBrokerSituationObservable(),
                        AdapterViewObservable.selects(tradingPerQuarterSpinner)
                                .distinctUntilChanged(createSpinnerDistinctByPosition()),
                        new Func2<LiveBrokerSituationDTO, OnSelectedEvent, LiveBrokerSituationDTO>()
                        {
                            @Override
                            public LiveBrokerSituationDTO call(LiveBrokerSituationDTO situationDTO, OnSelectedEvent onSelectedEvent)
                            {
                                if (onSelectedEvent instanceof OnItemSelectedEvent)
                                {
                                    //noinspection ConstantConditions
                                    ((KYCAyondoForm) situationDTO.kycForm).setTradingPerQuarter(
                                            ((TradingPerQuarterDTO) onSelectedEvent.parent.getItemAtPosition(
                                                    ((OnItemSelectedEvent) onSelectedEvent).position)).tradingPerQuarter);
                                }
                                return situationDTO;
                            }
                        }))
                .subscribe(
                        new Action1<LiveBrokerSituationDTO>()
                        {
                            @Override public void call(LiveBrokerSituationDTO situationDTO)
                            {
                                onNext(situationDTO);
                            }
                        },
                        new TimberOnErrorAction1("Failed to listen to compound buttons")));
    }

    protected void setChecked(@NonNull CompoundButton button, @Nullable Boolean checked)
    {
        if (checked != null)
        {
            button.setChecked(checked);
        }
    }

    protected void populate(@NonNull KYCAyondoForm kycForm)
    {
        Boolean workedInFinance = kycForm.isWorkedInFinance1Year();
        if (workedInFinance != null)
        {
            workInFinanceButton.setChecked(workedInFinance);
        }
        else
        {
            kycForm.setWorkedInFinance1Year(workInFinanceButton.isChecked());
        }

        Boolean attendedSeminar = kycForm.isAttendedSeminarAyondo();
        if (attendedSeminar != null)
        {
            attendedSeminarAyondoButton.setChecked(attendedSeminar);
        }
        else
        {
            kycForm.setAttendedSeminarAyondo(attendedSeminarAyondoButton.isChecked());
        }

        Boolean otherQualification = kycForm.isHaveOtherQualification();
        if (otherQualification != null)
        {
            haveOtherQualificationButton.setChecked(otherQualification);
        }
        else
        {
            kycForm.setHaveOtherQualification(haveOtherQualificationButton.isChecked());
        }

        Boolean tradeSharesBonds = kycForm.isTradedSharesBonds();
        if (tradeSharesBonds != null)
        {
            tradedSharesBondsButton.setChecked(tradeSharesBonds);
        }
        else
        {
            kycForm.setTradedSharesBonds(tradedSharesBondsButton.isChecked());
        }

        Boolean tradedOtc = kycForm.isTradedOtcDerivative();
        if (tradedOtc != null)
        {
            tradedOtcDerivativeButton.setChecked(tradedOtc);
        }
        else
        {
            kycForm.setTradedOtcDerivative(tradedOtcDerivativeButton.isChecked());
        }

        Boolean tradedEtc = kycForm.isTradedEtc();
        if (tradedEtc != null)
        {
            tradedEtcButton.setChecked(tradedEtc);
        }
        else
        {
            kycForm.setTradedEtc(tradedEtcButton.isChecked());
        }
    }

    protected void populateTradingPerQuarter(
            @NonNull KYCAyondoForm kycForm,
            @NonNull List<TradingPerQuarter> tradingPerQuarters)
    {
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
                kycForm.setTradingPerQuarter(chosenRange);
            }
        }
    }
}
