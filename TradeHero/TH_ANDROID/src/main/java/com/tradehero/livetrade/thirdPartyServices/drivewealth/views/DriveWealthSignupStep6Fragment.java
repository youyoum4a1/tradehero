package com.tradehero.livetrade.thirdPartyServices.drivewealth.views;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.tradehero.livetrade.thirdPartyServices.drivewealth.DriveWealthManager;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthSignupFormDTO;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class DriveWealthSignupStep6Fragment extends DashboardFragment {

    @Inject
    DriveWealthManager mDriveWealthManager;
    @InjectView(R.id.investmentObjectives)
    Spinner investmentObjectives;
    @InjectView(R.id.investmentExperience)
    Spinner investmentExperience;
    @InjectView(R.id.annualIncome)
    Spinner annualIncome;
    @InjectView(R.id.networthLiquid)
    Spinner networthLiquid;
    @InjectView(R.id.networthTotal)
    Spinner networthTotal;
    @InjectView(R.id.riskTolerance)
    Spinner riskTolerance;
    @InjectView(R.id.timeHorizon)
    Spinner timeHorizon;
    @InjectView(R.id.liqudityNeeds)
    Spinner liqudityNeeds;
    @InjectView(R.id.btn_next)
    Button btnNext;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        setHeadViewMiddleMain("投资习惯(6/7)");
        setHeadViewRight0(getString(R.string.cancel));
    }

    @Override
    public void onClickHeadRight0() {
        getActivity().finish();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dw_signup_page6, container, false);
        ButterKnife.inject(this, view);

        DriveWealthSignupFormDTO formDTO = mDriveWealthManager.getSignupFormDTO();

        investmentObjectives.setSelection(formDTO.investmentObjectivesIdx);
        investmentExperience.setSelection(formDTO.investmentExperienceIdx);
        annualIncome.setSelection(formDTO.annualIncomeIdx);
        networthLiquid.setSelection(formDTO.networthLiquidIdx);
        networthTotal.setSelection(formDTO.networthTotalIdx);
        riskTolerance.setSelection(formDTO.riskToleranceIdx);
        timeHorizon.setSelection(formDTO.timeHorizonIdx);
        liqudityNeeds.setSelection(formDTO.liquidityNeedsIdx);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.btn_next)
    public void onNextClick() {
        DriveWealthSignupFormDTO formDTO = mDriveWealthManager.getSignupFormDTO();
        formDTO.investmentObjectivesIdx = investmentObjectives.getSelectedItemPosition();
        formDTO.investmentExperienceIdx = investmentExperience.getSelectedItemPosition();
        formDTO.annualIncomeIdx = annualIncome.getSelectedItemPosition();
        formDTO.networthLiquidIdx = networthLiquid.getSelectedItemPosition();
        formDTO.networthTotalIdx = networthTotal.getSelectedItemPosition();
        formDTO.riskToleranceIdx = riskTolerance.getSelectedItemPosition();
        formDTO.timeHorizonIdx = timeHorizon.getSelectedItemPosition();
        formDTO.liquidityNeedsIdx = liqudityNeeds.getSelectedItemPosition();

        pushFragment(DriveWealthSignupStep7Fragment.class, new Bundle());
    }
}
