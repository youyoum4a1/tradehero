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
import com.tradehero.th.api.kyc.AnnualIncomeRange;
import com.tradehero.th.api.kyc.EmploymentStatus;
import com.tradehero.th.api.kyc.NetWorthRange;
import com.tradehero.th.api.kyc.PercentNetWorthForInvestmentRange;
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
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(new Action1<LiveBrokerSituationDTO>()
                                {
                                    @Override public void call(LiveBrokerSituationDTO situationDTO)
                                    {
                                        //noinspection ConstantConditions
                                        populate((KYCAyondoForm) situationDTO.kycForm);
                                    }
                                }),
                        getKYCAyondoFormOptionsObservable()
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(new Action1<KYCAyondoFormOptionsDTO>()
                                {
                                    @Override public void call(KYCAyondoFormOptionsDTO kycAyondoFormOptionsDTO)
                                    {
                                        LollipopArrayAdapter<AnnualIncomeDTO> annualIncomeAdapter =
                                                new LollipopArrayAdapter<>(getActivity(),
                                                        AnnualIncomeDTO.createList(getResources(), kycAyondoFormOptionsDTO.annualIncomeOptions));
                                        annualIncomeSpinner.setAdapter(annualIncomeAdapter);

                                        LollipopArrayAdapter<NetWorthDTO> netWorthAdapter =
                                                new LollipopArrayAdapter<>(getActivity(),
                                                        NetWorthDTO.createList(getResources(), kycAyondoFormOptionsDTO.netWorthOptions));
                                        netWorthSpinner.setAdapter(netWorthAdapter);

                                        LollipopArrayAdapter<PercentNetWorthDTO> percentageInvestmentAdapter =
                                                new LollipopArrayAdapter<>(getActivity(),
                                                        PercentNetWorthDTO.createList(getResources(),
                                                                kycAyondoFormOptionsDTO.percentNetWorthOptions));
                                        percentageInvestmentSpinner.setAdapter(percentageInvestmentAdapter);

                                        LollipopArrayAdapter<EmploymentStatusDTO> employmentStatusAdapter =
                                                new LollipopArrayAdapter<>(getActivity(),
                                                        EmploymentStatusDTO.createList(getResources(),
                                                                kycAyondoFormOptionsDTO.employmentStatusOptions));
                                        employmentStatusSpinner.setAdapter(employmentStatusAdapter);
                                    }
                                }),
                        new Func2<LiveBrokerSituationDTO, KYCAyondoFormOptionsDTO, Object>()
                        {
                            @Override public Object call(LiveBrokerSituationDTO liveBrokerSituationDTO, KYCAyondoFormOptionsDTO kycFormOptionsDTO)
                            {
                                //noinspection ConstantConditions
                                populateAnnualIncome((KYCAyondoForm) liveBrokerSituationDTO.kycForm, kycFormOptionsDTO.annualIncomeOptions);
                                populateNetWorth((KYCAyondoForm) liveBrokerSituationDTO.kycForm, kycFormOptionsDTO.netWorthOptions);
                                populatePercentNetWorth((KYCAyondoForm) liveBrokerSituationDTO.kycForm, kycFormOptionsDTO.percentNetWorthOptions);
                                populateEmploymentStatus((KYCAyondoForm) liveBrokerSituationDTO.kycForm, kycFormOptionsDTO.employmentStatusOptions);
                                return null;
                            }
                        })
                        .subscribe(
                                new EmptyAction1<>(),
                                new TimberOnErrorAction1("Failed to populate AyondoStep2 spinners")));

        onDestroyViewSubscriptions.add(Observable.merge(
                Observable.combineLatest(
                        getBrokerSituationObservable(),
                        AdapterViewObservable.selects(annualIncomeSpinner).distinctUntilChanged(createSpinnerDistinctByPosition()),
                        new Func2<LiveBrokerSituationDTO, OnSelectedEvent, LiveBrokerSituationDTO>()
                        {
                            @Override public LiveBrokerSituationDTO call(LiveBrokerSituationDTO situationDTO, OnSelectedEvent onSelectedEvent)
                            {
                                if (onSelectedEvent instanceof OnItemSelectedEvent)
                                {
                                    //noinspection ConstantConditions
                                    ((KYCAyondoForm) situationDTO.kycForm).setAnnualIncomeRange(
                                            ((AnnualIncomeDTO) onSelectedEvent.parent.getItemAtPosition(
                                                    ((OnItemSelectedEvent) onSelectedEvent).position)).annualIncomeRange);
                                }
                                return situationDTO;
                            }
                        }),
                Observable.combineLatest(
                        getBrokerSituationObservable(),
                        AdapterViewObservable.selects(netWorthSpinner).distinctUntilChanged(createSpinnerDistinctByPosition()),
                        new Func2<LiveBrokerSituationDTO, OnSelectedEvent, LiveBrokerSituationDTO>()
                        {
                            @Override public LiveBrokerSituationDTO call(LiveBrokerSituationDTO situationDTO, OnSelectedEvent onSelectedEvent)
                            {
                                if (onSelectedEvent instanceof OnItemSelectedEvent)
                                {
                                    //noinspection ConstantConditions
                                    ((KYCAyondoForm) situationDTO.kycForm).setNetWorthRange(
                                            ((NetWorthDTO) onSelectedEvent.parent.getItemAtPosition(
                                                    ((OnItemSelectedEvent) onSelectedEvent).position)).netWorthRange);
                                }
                                return situationDTO;
                            }
                        }),
                Observable.combineLatest(
                        getBrokerSituationObservable(),
                        AdapterViewObservable.selects(percentageInvestmentSpinner)
                                .distinctUntilChanged(createSpinnerDistinctByPosition()),
                        new Func2<LiveBrokerSituationDTO, OnSelectedEvent, LiveBrokerSituationDTO>()
                        {
                            @Override public LiveBrokerSituationDTO call(LiveBrokerSituationDTO situationDTO, OnSelectedEvent onSelectedEvent)
                            {
                                if (onSelectedEvent instanceof OnItemSelectedEvent)
                                {
                                    //noinspection ConstantConditions
                                    ((KYCAyondoForm) situationDTO.kycForm).setPercentNetWorthForInvestmentRange(
                                            ((PercentNetWorthDTO) onSelectedEvent.parent.getItemAtPosition(
                                                    ((OnItemSelectedEvent) onSelectedEvent).position)).netWorthForInvestmentRange);
                                }
                                return situationDTO;
                            }
                        }),
                Observable.combineLatest(
                        getBrokerSituationObservable(),
                        AdapterViewObservable.selects(employmentStatusSpinner).distinctUntilChanged(createSpinnerDistinctByPosition()),
                        new Func2<LiveBrokerSituationDTO, OnSelectedEvent, LiveBrokerSituationDTO>()
                        {
                            @Override public LiveBrokerSituationDTO call(LiveBrokerSituationDTO situationDTO, OnSelectedEvent onSelectedEvent)
                            {
                                if (onSelectedEvent instanceof OnItemSelectedEvent)
                                {
                                    //noinspection ConstantConditions
                                    ((KYCAyondoForm) situationDTO.kycForm).setEmploymentStatus(
                                            ((EmploymentStatusDTO) onSelectedEvent.parent.getItemAtPosition(
                                                    ((OnItemSelectedEvent) onSelectedEvent).position)).employmentStatus);
                                }
                                return situationDTO;
                            }
                        }),
                Observable.combineLatest(
                        getBrokerSituationObservable(),
                        WidgetObservable.input(employerRegulatedCheckBox).distinctUntilChanged(
                                new Func1<OnCheckedChangeEvent, Boolean>()
                                {
                                    @Override public Boolean call(OnCheckedChangeEvent onCheckedChangeEvent)
                                    {
                                        return onCheckedChangeEvent.value();
                                    }
                                }),
                        new Func2<LiveBrokerSituationDTO, OnCheckedChangeEvent, LiveBrokerSituationDTO>()
                        {
                            @Override
                            public LiveBrokerSituationDTO call(LiveBrokerSituationDTO situationDTO, OnCheckedChangeEvent onCheckedChangeEvent)
                            {
                                //noinspection ConstantConditions
                                ((KYCAyondoForm) situationDTO.kycForm).setEmployerRegulatedFinancial(
                                        onCheckedChangeEvent.value());
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
                        new TimberOnErrorAction1("Failed to listen to spinner selections")));
    }

    protected void populate(@NonNull KYCAyondoForm kycForm)
    {
        Boolean employerRegulated = kycForm.isEmployerRegulatedFinancial();
        if (employerRegulated != null)
        {
            employerRegulatedCheckBox.setChecked(employerRegulated);
        }
        else
        {
            kycForm.setEmployerRegulatedFinancial(employerRegulatedCheckBox.isChecked());
        }
    }

    protected void populateAnnualIncome(
            @NonNull KYCAyondoForm kycForm,
            @NonNull List<AnnualIncomeRange> incomeRanges)
    {
        AnnualIncomeRange savedAnnualIncomeRange = kycForm.getAnnualIncomeRange();
        Integer indexIncome = populateSpinner(annualIncomeSpinner,
                savedAnnualIncomeRange,
                incomeRanges);
        if (savedAnnualIncomeRange == null)
        {
            AnnualIncomeRange chosenRange;
            if (indexIncome != null)
            {
                chosenRange = incomeRanges.get(indexIncome);
            }
            else
            {
                chosenRange = ((AnnualIncomeDTO) annualIncomeSpinner.getSelectedItem()).annualIncomeRange;
            }

            if (chosenRange != null)
            {
                kycForm.setAnnualIncomeRange(chosenRange);
            }
        }
    }

    protected void populateNetWorth(
            @NonNull KYCAyondoForm kycForm,
            @NonNull List<NetWorthRange> netWorthRanges)
    {
        NetWorthRange savedNetWorthRange = kycForm.getNetWorthRange();
        Integer indexWorth = populateSpinner(netWorthSpinner,
                savedNetWorthRange,
                netWorthRanges);
        if (savedNetWorthRange == null)
        {
            NetWorthRange chosenRange;
            if (indexWorth != null)
            {
                chosenRange = netWorthRanges.get(indexWorth);
            }
            else
            {
                chosenRange = ((NetWorthDTO) netWorthSpinner.getSelectedItem()).netWorthRange;
            }

            if (chosenRange != null)
            {
                kycForm.setNetWorthRange(chosenRange);
            }
        }
    }

    protected void populatePercentNetWorth(
            @NonNull KYCAyondoForm kycForm,
            @NonNull List<PercentNetWorthForInvestmentRange> netWorthRanges)
    {
        PercentNetWorthForInvestmentRange savedNetWorthRange = kycForm.getPercentNetWorthForInvestmentRange();
        Integer indexWorth = populateSpinner(percentageInvestmentSpinner,
                savedNetWorthRange,
                netWorthRanges);
        if (savedNetWorthRange == null)
        {
            PercentNetWorthForInvestmentRange chosenRange;
            if (indexWorth != null)
            {
                chosenRange = netWorthRanges.get(indexWorth);
            }
            else
            {
                chosenRange = ((PercentNetWorthDTO) percentageInvestmentSpinner.getSelectedItem()).netWorthForInvestmentRange;
            }

            if (chosenRange != null)
            {
                kycForm.setPercentNetWorthForInvestmentRange(chosenRange);
            }
        }
    }

    protected void populateEmploymentStatus(
            @NonNull KYCAyondoForm kycForm,
            @NonNull List<EmploymentStatus> employmentStatuses)
    {
        EmploymentStatus savedEmploymentStatus = kycForm.getEmploymentStatus();
        Integer indexStatus = populateSpinner(employmentStatusSpinner,
                savedEmploymentStatus,
                employmentStatuses);
        if (savedEmploymentStatus == null)
        {
            EmploymentStatus chosenStatus;
            if (indexStatus != null)
            {
                chosenStatus = employmentStatuses.get(indexStatus);
            }
            else
            {
                chosenStatus = ((EmploymentStatusDTO) employmentStatusSpinner.getSelectedItem()).employmentStatus;
            }

            if (chosenStatus != null)
            {
                kycForm.setEmploymentStatus(chosenStatus);
            }
        }
    }
}
