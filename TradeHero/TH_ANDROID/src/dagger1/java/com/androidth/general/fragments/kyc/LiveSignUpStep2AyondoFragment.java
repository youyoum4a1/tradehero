package com.androidth.general.fragments.kyc;

import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.androidth.general.R;
import com.androidth.general.api.kyc.AnnualIncomeRange;
import com.androidth.general.api.kyc.EmploymentStatus;
import com.androidth.general.api.kyc.EmptyKYCForm;
import com.androidth.general.api.kyc.NetWorthRange;
import com.androidth.general.api.kyc.PercentNetWorthForInvestmentRange;
import com.androidth.general.api.kyc.StepStatus;
import com.androidth.general.api.kyc.ayondo.KYCAyondoForm;
import com.androidth.general.api.kyc.ayondo.KYCAyondoFormOptionsDTO;
import com.androidth.general.api.live.LiveBrokerDTO;
import com.androidth.general.api.live.LiveBrokerSituationDTO;
import com.androidth.general.fragments.base.LollipopArrayAdapter;
import com.androidth.general.fragments.kyc.dto.AnnualIncomeDTO;
import com.androidth.general.fragments.kyc.dto.EmploymentStatusDTO;
import com.androidth.general.fragments.kyc.dto.NetWorthDTO;
import com.androidth.general.fragments.kyc.dto.PercentNetWorthDTO;
import com.androidth.general.rx.EmptyAction1;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.rx.view.adapter.AdapterViewObservable;
import com.androidth.general.rx.view.adapter.OnSelectedEvent;
import com.androidth.general.utils.broadcast.GAnalyticsProvider;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnCheckedChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import timber.log.Timber;

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

    @Override
    public void onResume() {
        super.onResume();
        GAnalyticsProvider.sendGAScreenEvent(getActivity(), GAnalyticsProvider.COMP_KYC_2);
    }

    @Override protected void onNextButtonEnabled(List<StepStatus> stepStatuses)
    {
        StepStatus secondStatus = stepStatuses == null || stepStatuses.size() == 0 ? null : stepStatuses.get(1);
        if (btnNext != null)
        {
//            btnNext.setEnabled(secondStatus != null && secondStatus.equals(StepStatus.COMPLETE));
            updateDB(true, 2);//Just for now
            btnNext.setEnabled(true);//jeff todo
        }
    }

    @Override protected List<Subscription> onInitAyondoSubscription(Observable<LiveBrokerDTO> brokerDTOObservable,
            Observable<LiveBrokerSituationDTO> liveBrokerSituationDTOObservable,
            Observable<KYCAyondoFormOptionsDTO> kycAyondoFormOptionsDTOObservable)
    {
        List<Subscription> subscriptions = new ArrayList<>();

        subscriptions.add(
                Observable.combineLatest(
                        liveBrokerSituationDTOObservable
                                .take(1)
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(new Action1<LiveBrokerSituationDTO>()
                                {
                                    @Override public void call(LiveBrokerSituationDTO situationDTO)
                                    {
                                        if(situationDTO.kycForm instanceof EmptyKYCForm){
                                            KYCAyondoForm defaultForm = new KYCAyondoForm();
                                            situationDTO = new LiveBrokerSituationDTO(situationDTO.broker, defaultForm);
                                        }
                                        populate((KYCAyondoForm) situationDTO.kycForm);
                                    }
                                })
                        .doOnError(new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Timber.d("Step 2 livebroker error: "+throwable.getMessage());
                            }
                        }),
                        kycAyondoFormOptionsDTOObservable
                                .take(1)
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnNext(new Action1<KYCAyondoFormOptionsDTO>()
                                {
                                    @Override public void call(KYCAyondoFormOptionsDTO kycAyondoFormOptionsDTO)
                                    {
                                        LollipopArrayAdapter<AnnualIncomeDTO> annualIncomeAdapter =
                                                new LollipopArrayAdapter<>(getActivity(),
                                                        AnnualIncomeDTO.createList(getResources(), kycAyondoFormOptionsDTO.annualIncomeOptions));
                                        annualIncomeSpinner.setAdapter(annualIncomeAdapter);
                                        annualIncomeAdapter.notifyDataSetChanged();

                                        LollipopArrayAdapter<NetWorthDTO> netWorthAdapter =
                                                new LollipopArrayAdapter<>(getActivity(),
                                                        NetWorthDTO.createList(getResources(), kycAyondoFormOptionsDTO.netWorthOptions));
                                        netWorthSpinner.setAdapter(netWorthAdapter);
                                        netWorthAdapter.notifyDataSetChanged();

                                        LollipopArrayAdapter<PercentNetWorthDTO> percentageInvestmentAdapter =
                                                new LollipopArrayAdapter<>(getActivity(),
                                                        PercentNetWorthDTO.createList(getResources(),
                                                                kycAyondoFormOptionsDTO.percentNetWorthOptions));
                                        percentageInvestmentSpinner.setAdapter(percentageInvestmentAdapter);
                                        percentageInvestmentAdapter.notifyDataSetChanged();

                                        LollipopArrayAdapter<EmploymentStatusDTO> employmentStatusAdapter =
                                                new LollipopArrayAdapter<>(getActivity(),
                                                        EmploymentStatusDTO.createList(getResources(),
                                                                kycAyondoFormOptionsDTO.employmentStatusOptions));
                                        employmentStatusSpinner.setAdapter(employmentStatusAdapter);
                                        employmentStatusAdapter.notifyDataSetChanged();
                                    }
                                })
                        .doOnError(new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Timber.d("Step2 kyc error: "+throwable.getMessage());
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

        subscriptions.add(Observable.merge(
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
                        })).withLatestFrom(brokerDTOObservable, new Func2<KYCAyondoForm, LiveBrokerDTO, LiveBrokerSituationDTO>()
        {
            @Override public LiveBrokerSituationDTO call(KYCAyondoForm kycAyondoForm, LiveBrokerDTO liveBrokerDTO)
            {
                return new LiveBrokerSituationDTO(liveBrokerDTO, kycAyondoForm);
            }
        }).subscribe(
                new Action1<LiveBrokerSituationDTO>()
                {
                    @Override public void call(LiveBrokerSituationDTO update)
                    {
                        onNext(update);
                    }
                },
                new TimberOnErrorAction1("Failed to listen to spinner selections")));

        return subscriptions;
    }

    @MainThread
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
        annualIncomeSpinner.setSelection(3);
        netWorthSpinner.setSelection(3);
        percentageInvestmentSpinner.setSelection(3);
        return update;
    }

    @MainThread
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

    @MainThread
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

    @MainThread
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

    @MainThread
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
