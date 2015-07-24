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
import com.tradehero.th.api.live.LiveBrokerDTO;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.rx.view.adapter.AdapterViewObservable;
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
                            @Override public Object call(LiveBrokerSituationDTO situationDTO, KYCAyondoFormOptionsDTO kycFormOptionsDTO)
                            {
                                KYCAyondoForm update = new KYCAyondoForm();
                                //noinspection ConstantConditions
                                update.pickFrom(
                                        populateAnnualIncome((KYCAyondoForm) situationDTO.kycForm, kycFormOptionsDTO.annualIncomeOptions));
                                update.pickFrom(populateNetWorth((KYCAyondoForm) situationDTO.kycForm, kycFormOptionsDTO.netWorthOptions));
                                update.pickFrom(populatePercentNetWorth((KYCAyondoForm) situationDTO.kycForm,
                                        kycFormOptionsDTO.percentNetWorthOptions));
                                update.pickFrom(populateEmploymentStatus((KYCAyondoForm) situationDTO.kycForm,
                                        kycFormOptionsDTO.employmentStatusOptions));
                                onNext(new LiveBrokerSituationDTO(situationDTO.broker, update));
                                return null;
                            }
                        })
                        .subscribe(
                                new EmptyAction1<>(),
                                new TimberOnErrorAction1("Failed to populate AyondoStep2 spinners")));

        onDestroyViewSubscriptions.add(Observable.combineLatest(
                createBrokerObservable(),
                Observable.merge(
                        AdapterViewObservable.selects(annualIncomeSpinner).distinctUntilChanged(createSpinnerDistinctByPosition())
                                .map(new Func1<OnSelectedEvent, KYCAyondoForm>()
                                {
                                    @Override public KYCAyondoForm call(OnSelectedEvent onSelectedEvent)
                                    {
                                        return KYCAyondoFormFactory.fromAnnualIncomeRangeEvent(onSelectedEvent);
                                    }
                                }),
                        AdapterViewObservable.selects(netWorthSpinner).distinctUntilChanged(createSpinnerDistinctByPosition())
                                .map(new Func1<OnSelectedEvent, KYCAyondoForm>()
                                {
                                    @Override public KYCAyondoForm call(OnSelectedEvent onSelectedEvent)
                                    {
                                        return KYCAyondoFormFactory.fromNetWorthRangeEvent(onSelectedEvent);
                                    }
                                }),
                        AdapterViewObservable.selects(percentageInvestmentSpinner)
                                .distinctUntilChanged(createSpinnerDistinctByPosition())
                                .map(new Func1<OnSelectedEvent, KYCAyondoForm>()
                                {
                                    @Override public KYCAyondoForm call(OnSelectedEvent onSelectedEvent)
                                    {
                                        return KYCAyondoFormFactory.fromPercentNetWorthRangeEvent(onSelectedEvent);
                                    }
                                }),
                        AdapterViewObservable.selects(employmentStatusSpinner).distinctUntilChanged(createSpinnerDistinctByPosition())
                                .map(new Func1<OnSelectedEvent, KYCAyondoForm>()
                                {
                                    @Override public KYCAyondoForm call(OnSelectedEvent onSelectedEvent)
                                    {
                                        return KYCAyondoFormFactory.fromEmploymentStatusEvent(onSelectedEvent);
                                    }
                                }),
                        WidgetObservable.input(employerRegulatedCheckBox).distinctUntilChanged(
                                new Func1<OnCheckedChangeEvent, Boolean>()
                                {
                                    @Override public Boolean call(OnCheckedChangeEvent onCheckedChangeEvent)
                                    {
                                        return onCheckedChangeEvent.value();
                                    }
                                })
                                .map(new Func1<OnCheckedChangeEvent, KYCAyondoForm>()
                                {
                                    @Override public KYCAyondoForm call(OnCheckedChangeEvent onCheckedChangeEvent)
                                    {
                                        return KYCAyondoFormFactory.fromEmployerRegulatedEvent(onCheckedChangeEvent);
                                    }
                                })),
                new Func2<LiveBrokerDTO, KYCAyondoForm, LiveBrokerSituationDTO>()
                {
                    @Override public LiveBrokerSituationDTO call(LiveBrokerDTO liveBrokerDTO, KYCAyondoForm kycAyondoForm)
                    {
                        return new LiveBrokerSituationDTO(liveBrokerDTO, kycAyondoForm);
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
                        new TimberOnErrorAction1("Failed to listen to spinner selections")));
    }

    @NonNull protected KYCAyondoForm populate(@NonNull KYCAyondoForm kycForm)
    {
        KYCAyondoForm update = new KYCAyondoForm();
        Boolean employerRegulated = kycForm.isEmployerRegulatedFinancial();
        if (employerRegulated != null)
        {
            employerRegulatedCheckBox.setChecked(employerRegulated);
        }
        else
        {
            update.setEmployerRegulatedFinancial(employerRegulatedCheckBox.isChecked());
        }
        return update;
    }

    @NonNull protected KYCAyondoForm populateAnnualIncome(
            @NonNull KYCAyondoForm kycForm,
            @NonNull List<AnnualIncomeRange> incomeRanges)
    {
        KYCAyondoForm update = new KYCAyondoForm();
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

            update.setAnnualIncomeRange(chosenRange);
        }
        return update;
    }

    @NonNull protected KYCAyondoForm populateNetWorth(
            @NonNull KYCAyondoForm kycForm,
            @NonNull List<NetWorthRange> netWorthRanges)
    {
        KYCAyondoForm update = new KYCAyondoForm();
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

            update.setNetWorthRange(chosenRange);
        }
        return update;
    }

    @NonNull protected KYCAyondoForm populatePercentNetWorth(
            @NonNull KYCAyondoForm kycForm,
            @NonNull List<PercentNetWorthForInvestmentRange> netWorthRanges)
    {
        KYCAyondoForm update = new KYCAyondoForm();
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

            update.setPercentNetWorthForInvestmentRange(chosenRange);
        }
        return update;
    }

    @NonNull protected KYCAyondoForm populateEmploymentStatus(
            @NonNull KYCAyondoForm kycForm,
            @NonNull List<EmploymentStatus> employmentStatuses)
    {
        KYCAyondoForm update = new KYCAyondoForm();
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

            update.setEmploymentStatus(chosenStatus);
        }
        return update;
    }
}
