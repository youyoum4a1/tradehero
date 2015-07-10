package com.tradehero.th.fragments.live.ayondo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.tradehero.th.R;
import com.tradehero.th.api.kyc.KYCFormOptionsDTO;
import com.tradehero.th.api.live.LiveBrokerSituationDTO;
import com.tradehero.th.api.live.ayondo.KYCAyondoFormOptionsDTO;
import com.tradehero.th.fragments.live.LiveSignUpStepBaseFragment;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;

public class LiveSignUpStep2AyondoFragment extends LiveSignUpStepBaseAyondoFragment
{
    @Bind(R.id.info_annual_income) Spinner annualIncome;
    @Bind(R.id.info_net_worth) Spinner netWorth;
    @Bind(R.id.info_percent_for_investment) Spinner percentageInvestment;
    @Bind(R.id.info_employment_status) Spinner employmentStatus;

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
                                LollipopArrayAdapter annualIncomeAdapter =
                                        new LollipopArrayAdapter(getActivity(), kycFormOptionsDTO.annualIncomeOptions);
                                annualIncome.setAdapter(annualIncomeAdapter);

                                LollipopArrayAdapter netWorthAdapter =
                                        new LollipopArrayAdapter(getActivity(), kycFormOptionsDTO.netWorthOptions);
                                netWorth.setAdapter(netWorthAdapter);

                                LollipopArrayAdapter percentageInvestmentAdapter =
                                        new LollipopArrayAdapter(getActivity(), kycFormOptionsDTO.percentNetWorthOptions);
                                percentageInvestment.setAdapter(percentageInvestmentAdapter);

                                LollipopArrayAdapter employmentStatusAdapter =
                                        new LollipopArrayAdapter(getActivity(), kycFormOptionsDTO.employmentStatusIncomeOptions);
                                employmentStatus.setAdapter(employmentStatusAdapter);

                                return null;
                            }
                        }
                ).subscribe(new Action1<Object>()
                {
                    @Override public void call(Object o)
                    {

                    }
                })
        );
    }
}
