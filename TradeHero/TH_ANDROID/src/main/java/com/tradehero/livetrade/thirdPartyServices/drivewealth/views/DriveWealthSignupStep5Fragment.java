package com.tradehero.livetrade.thirdPartyServices.drivewealth.views;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.DriveWealthManager;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthSignupFormDTO;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import javax.inject.Inject;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class DriveWealthSignupStep5Fragment extends DashboardFragment {

    @Inject DriveWealthManager mDriveWealthManager;

    @InjectView(R.id.employment) Spinner employment;
    @InjectView(R.id.industry) Spinner industry;
    @InjectView(R.id.company) EditText company;
    @InjectView(R.id.isStockRelatedEmployee) Switch isStockRelatedEmployee;
    @InjectView(R.id.isStockHolder) Switch isStockHolder;
    @InjectView(R.id.isGovOfficer) Switch isGovOfficer;
    @InjectView(R.id.btn_next) Button btnNext;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        setHeadViewMiddleMain("工作信息(5/7)");
        setHeadViewRight0(getString(R.string.cancel));
    }

    @Override
    public void onClickHeadRight0() {
        getActivity().finish();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dw_signup_page5, container, false);
        ButterKnife.inject(this, view);

        DriveWealthSignupFormDTO formDTO = mDriveWealthManager.getSignupFormDTO();
        if (formDTO.employerCompany != null) {
            company.setText(formDTO.employerCompany);
        }

        employment.setSelection(formDTO.employmentStatusIdx);
        industry.setSelection(formDTO.employerBusinessIdx);
        isStockRelatedEmployee.setChecked(formDTO.employerIsBroker);
        isStockHolder.setChecked(formDTO.director);
        isGovOfficer.setChecked(formDTO.politicallyExposed);

        checkNEnableNextButton();

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
        formDTO.employerCompany = company.getText().toString();
        formDTO.employmentStatusIdx = employment.getSelectedItemPosition();
        formDTO.employerBusinessIdx = industry.getSelectedItemPosition();
        formDTO.employerIsBroker = isStockRelatedEmployee.isChecked();
        formDTO.director = isStockHolder.isChecked();
        formDTO.politicallyExposed = isGovOfficer.isChecked();

        pushFragment(DriveWealthSignupStep6Fragment.class, new Bundle());
    }

    @OnTextChanged({R.id.company})
    public void onEditTextChanged(CharSequence text) {
        checkNEnableNextButton();
    }

    private void checkNEnableNextButton() {
        if (company.getText().length() > 0) {
            btnNext.setEnabled(true);
        } else {
            btnNext.setEnabled(false);
        }
    }
}
