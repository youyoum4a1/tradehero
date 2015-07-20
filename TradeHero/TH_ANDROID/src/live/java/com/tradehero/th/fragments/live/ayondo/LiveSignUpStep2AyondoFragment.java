package com.tradehero.th.fragments.live.ayondo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Spinner;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.tradehero.th.R;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoForm;
import com.tradehero.th.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.rx.view.adapter.AdapterViewObservable;
import com.tradehero.th.rx.view.adapter.OnItemSelectedEvent;
import com.tradehero.th.rx.view.adapter.OnNothingSelectedEvent;
import com.tradehero.th.rx.view.adapter.OnSelectedEvent;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnCheckedChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

public class LiveSignUpStep2AyondoFragment extends LiveSignUpStepBaseAyondoFragment
{
    @Bind(R.id.info_annual_income) Spinner annualIncomeSpinner;
    @Bind(R.id.info_net_worth) Spinner netWorthSpinner;
    @Bind(R.id.info_percent_for_investment) Spinner percentageInvestmentSpinner;
    @Bind(R.id.info_employment_status) Spinner employmentStatusSpinner;
    @Bind(R.id.employer_regulated_financial) CheckBox employerRegulatedCheckBox;

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_sign_up_live_ayondo_step_2, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        onDestroyViewSubscriptions.add(
                Observable.combineLatest(
                        getBrokerSituationObservable()
                                .observeOn(AndroidSchedulers.mainThread()),
                        getKYCAyondoFormOptionsObservable()
                                .observeOn(AndroidSchedulers.mainThread()),
                        new Func2<LiveBrokerSituationDTO, KYCAyondoFormOptionsDTO, Object>()
                        {
                            @Override public Object call(LiveBrokerSituationDTO liveBrokerSituationDTO, KYCAyondoFormOptionsDTO kycFormOptionsDTO)
                            {
                                //noinspection ConstantConditions
                                populateSpinner(annualIncomeSpinner,
                                        ((KYCAyondoForm) liveBrokerSituationDTO.kycForm).getAnnualIncomeRange(),
                                        kycFormOptionsDTO.annualIncomeOptions);

                                //noinspection ConstantConditions
                                populateSpinner(netWorthSpinner,
                                        ((KYCAyondoForm) liveBrokerSituationDTO.kycForm).getNetWorthRange(),
                                        kycFormOptionsDTO.netWorthOptions);

                                //noinspection ConstantConditions
                                populateSpinner(percentageInvestmentSpinner,
                                        ((KYCAyondoForm) liveBrokerSituationDTO.kycForm).getPercentNetWorthForInvestmentRange(),
                                        kycFormOptionsDTO.percentNetWorthOptions);

                                //noinspection ConstantConditions
                                populateSpinner(employmentStatusSpinner,
                                        ((KYCAyondoForm) liveBrokerSituationDTO.kycForm).getEmploymentStatus(),
                                        kycFormOptionsDTO.employmentStatusOptions);
                                return null;
                            }
                        })
                        .subscribe(
                                new Action1<Object>()
                                {
                                    @Override public void call(Object o)
                                    {
                                    }
                                },
                                new TimberOnErrorAction("Failed to populate AyondoStep2 spinners")));

        onDestroyViewSubscriptions.add(
                Observable.combineLatest(
                        getBrokerSituationObservable().observeOn(AndroidSchedulers.mainThread()),
                        AdapterViewObservable.selects(annualIncomeSpinner).distinctUntilChanged(createSpinnerDistinct()),
                        new Func2<LiveBrokerSituationDTO, OnSelectedEvent, Object>()
                        {
                            @Override
                            public Object call(
                                    LiveBrokerSituationDTO situationDTO,
                                    OnSelectedEvent onSelectedEvent)
                            {
                                if (onSelectedEvent instanceof OnItemSelectedEvent)
                                {
                                    //noinspection ConstantConditions
                                    ((KYCAyondoForm) situationDTO.kycForm).setAnnualIncomeRange(
                                            ((AnnualIncomeDTO) onSelectedEvent.parent.getItemAtPosition(
                                                    ((OnItemSelectedEvent) onSelectedEvent).position)).annualIncomeRange);
                                    onNext(situationDTO);
                                }
                                return null;
                            }
                        })
                        .subscribe(
                                new Action1<Object>()
                                {
                                    @Override public void call(Object o)
                                    {
                                    }
                                },
                                new TimberOnErrorAction("Failed to listen to annual income selects")));

        onDestroyViewSubscriptions.add(
                Observable.combineLatest(
                        getBrokerSituationObservable().observeOn(AndroidSchedulers.mainThread()),
                        AdapterViewObservable.selects(netWorthSpinner).distinctUntilChanged(createSpinnerDistinct()),
                        new Func2<LiveBrokerSituationDTO, OnSelectedEvent, Object>()
                        {
                            @Override
                            public Object call(
                                    LiveBrokerSituationDTO situationDTO,
                                    OnSelectedEvent onSelectedEvent)
                            {
                                if (onSelectedEvent instanceof OnItemSelectedEvent)
                                {
                                    //noinspection ConstantConditions
                                    ((KYCAyondoForm) situationDTO.kycForm).setNetWorthRange(
                                            ((NetWorthDTO) onSelectedEvent.parent.getItemAtPosition(
                                                    ((OnItemSelectedEvent) onSelectedEvent).position)).netWorthRange);
                                    onNext(situationDTO);
                                }
                                return null;
                            }
                        })
                        .subscribe(
                                new Action1<Object>()
                                {
                                    @Override public void call(Object o)
                                    {
                                    }
                                },
                                new TimberOnErrorAction("Failed to listen to net worth selects")));

        onDestroyViewSubscriptions.add(
                Observable.combineLatest(
                        getBrokerSituationObservable().observeOn(AndroidSchedulers.mainThread()),
                        AdapterViewObservable.selects(percentageInvestmentSpinner).distinctUntilChanged(createSpinnerDistinct()),
                        new Func2<LiveBrokerSituationDTO, OnSelectedEvent, Object>()
                        {
                            @Override
                            public Object call(
                                    LiveBrokerSituationDTO situationDTO,
                                    OnSelectedEvent onSelectedEvent)
                            {
                                if (onSelectedEvent instanceof OnItemSelectedEvent)
                                {
                                    //noinspection ConstantConditions
                                    ((KYCAyondoForm) situationDTO.kycForm).setPercentNetWorthForInvestmentRange(
                                            ((PercentNetWorthDTO) onSelectedEvent.parent.getItemAtPosition(
                                                    ((OnItemSelectedEvent) onSelectedEvent).position)).netWorthForInvestmentRange);
                                    onNext(situationDTO);
                                }
                                return null;
                            }
                        })
                        .subscribe(
                                new Action1<Object>()
                                {
                                    @Override public void call(Object o)
                                    {
                                    }
                                },
                                new TimberOnErrorAction("Failed to listen to percent net worth selects")));

        onDestroyViewSubscriptions.add(
                Observable.combineLatest(
                        getBrokerSituationObservable().observeOn(AndroidSchedulers.mainThread()),
                        AdapterViewObservable.selects(employmentStatusSpinner).distinctUntilChanged(createSpinnerDistinct()),
                        new Func2<LiveBrokerSituationDTO, OnSelectedEvent, Object>()
                        {
                            @Override
                            public Object call(
                                    LiveBrokerSituationDTO situationDTO,
                                    OnSelectedEvent onSelectedEvent)
                            {
                                if (onSelectedEvent instanceof OnItemSelectedEvent)
                                {
                                    //noinspection ConstantConditions
                                    ((KYCAyondoForm) situationDTO.kycForm).setEmploymentStatus(
                                            ((EmploymentStatusDTO) onSelectedEvent.parent.getItemAtPosition(
                                                    ((OnItemSelectedEvent) onSelectedEvent).position)).employmentStatus);
                                    onNext(situationDTO);
                                }
                                return null;
                            }
                        })
                        .subscribe(
                                new Action1<Object>()
                                {
                                    @Override public void call(Object o)
                                    {
                                    }
                                },
                                new TimberOnErrorAction("Failed to listen to employment status selects")));

        onDestroyViewSubscriptions.add(
                Observable.combineLatest(
                        getBrokerSituationObservable().observeOn(AndroidSchedulers.mainThread()),
                        WidgetObservable.input(employerRegulatedCheckBox),
                        new Func2<LiveBrokerSituationDTO, OnCheckedChangeEvent, Object>()
                        {
                            @Override public Object call(
                                    LiveBrokerSituationDTO situationDTO,
                                    OnCheckedChangeEvent onCheckedChangeEvent)
                            {
                                //noinspection ConstantConditions
                                ((KYCAyondoForm) situationDTO.kycForm).setEmployerRegulatedFinancial(onCheckedChangeEvent.value());
                                onNext(situationDTO);
                                return null;
                            }
                        })
                        .subscribe(
                                new Action1<Object>()
                                {
                                    @Override public void call(Object o)
                                    {
                                    }
                                },
                                new TimberOnErrorAction("Failed to listen to employer regulated updates")));
    }

    @NonNull @Override protected Observable<LiveBrokerSituationDTO> createBrokerSituationObservable()
    {
        return super.createBrokerSituationObservable()
                .doOnNext(new Action1<LiveBrokerSituationDTO>()
                {
                    @Override public void call(LiveBrokerSituationDTO situationDTO)
                    {
                        //noinspection ConstantConditions
                        populate((KYCAyondoForm) situationDTO.kycForm);
                    }
                });
    }

    protected void populate(@NonNull KYCAyondoForm kycForm)
    {
        Boolean employerRegulated = kycForm.isEmployerRegulatedFinancial();
        if (employerRegulated != null)
        {
            employerRegulatedCheckBox.setChecked(employerRegulated);
        }
    }

    @NonNull @Override protected Observable<KYCAyondoFormOptionsDTO> createKYCAyondoFormOptionsObservable()
    {
        return super.createKYCAyondoFormOptionsObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<KYCAyondoFormOptionsDTO>()
                {
                    @Override public void call(KYCAyondoFormOptionsDTO kycFormOptionsDTO)
                    {
                        LollipopArrayAdapter<AnnualIncomeDTO> annualIncomeAdapter =
                                new LollipopArrayAdapter<>(getActivity(),
                                        AnnualIncomeDTO.createList(getResources(), kycFormOptionsDTO.annualIncomeOptions));
                        annualIncomeSpinner.setAdapter(annualIncomeAdapter);

                        LollipopArrayAdapter<NetWorthDTO> netWorthAdapter =
                                new LollipopArrayAdapter<>(getActivity(),
                                        NetWorthDTO.createList(getResources(), kycFormOptionsDTO.netWorthOptions));
                        netWorthSpinner.setAdapter(netWorthAdapter);

                        LollipopArrayAdapter<PercentNetWorthDTO> percentageInvestmentAdapter =
                                new LollipopArrayAdapter<>(getActivity(),
                                        PercentNetWorthDTO.createList(getResources(), kycFormOptionsDTO.percentNetWorthOptions));
                        percentageInvestmentSpinner.setAdapter(percentageInvestmentAdapter);

                        LollipopArrayAdapter<EmploymentStatusDTO> employmentStatusAdapter =
                                new LollipopArrayAdapter<>(getActivity(),
                                        EmploymentStatusDTO.createList(getResources(), kycFormOptionsDTO.employmentStatusOptions));
                        employmentStatusSpinner.setAdapter(employmentStatusAdapter);
                    }
                });
    }

    protected <T> void populateSpinner(@NonNull Spinner spinner, @Nullable T value, @NonNull List<T> list)
    {
        if (value != null)
        {
            int index = list.indexOf(value);
            if (index >= 0)
            {
                spinner.setSelection(index);
            }
        }
    }

    protected Func1<OnSelectedEvent, Integer> createSpinnerDistinct()
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
