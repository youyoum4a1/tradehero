package com.tradehero.livetrade.thirdPartyServices.drivewealth.views;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.tradehero.livetrade.thirdPartyServices.drivewealth.DriveWealthManager;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthSignupFormDTO;
import com.tradehero.th.R;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class DriveWealthSignupStep5Fragment extends DriveWealthSignupBaseFragment {

    @Inject
    DriveWealthManager mDriveWealthManager;
    @InjectView(R.id.employment)
    Spinner employment;
    @InjectView(R.id.industry)
    Spinner industry;
    @InjectView(R.id.company)
    EditText company;
    @InjectView(R.id.isStockRelatedEmployeeNote)
    TextView isStockRelatedEmployeeNote;
    @InjectView(R.id.isStockRelatedEmployee)
    Switch isStockRelatedEmployee;
    @InjectView(R.id.isStockHolder)
    Switch isStockHolder;
    @InjectView(R.id.stockCompanyNameInput)
    EditText stockCompanyNameInput;
    @InjectView(R.id.stockCompanyName)
    RelativeLayout stockCompanyName;
    @InjectView(R.id.stockCompanyCodeInput)
    EditText stockCompanyCodeInput;
    @InjectView(R.id.stockCompanyCode)
    RelativeLayout stockCompanyCode;
    @InjectView(R.id.isGovOfficer)
    Switch isGovOfficer;
    @InjectView(R.id.immediateFamilyNameInput)
    EditText immediateFamilyNameInput;
    @InjectView(R.id.immediateFamilyName)
    RelativeLayout immediateFamilyName;
    @InjectView(R.id.btn_next)
    Button btnNext;


    @Override
    public String getTitle() {
        return "工作信息(5/7)";
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

        if (formDTO.employerIsBroker) {
            isStockRelatedEmployeeNote.setVisibility(View.VISIBLE);
        } else {
            isStockRelatedEmployeeNote.setVisibility(View.GONE);
        }

        if (formDTO.director) {
            stockCompanyName.setVisibility(View.VISIBLE);
            stockCompanyCode.setVisibility(View.VISIBLE);
        } else {
            stockCompanyName.setVisibility(View.GONE);
            stockCompanyCode.setVisibility(View.GONE);
        }

        if (formDTO.politicallyExposed) {
            immediateFamilyName.setVisibility(View.VISIBLE);
        } else {
            immediateFamilyName.setVisibility(View.GONE);
        }

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
        formDTO.directorOf = stockCompanyNameInput.getText().toString() + ", " + stockCompanyCodeInput.getText().toString();
        formDTO.politicallyExposed = isGovOfficer.isChecked();
        formDTO.politicallyExposedNames = immediateFamilyNameInput.getText().toString();

        pushFragment(DriveWealthSignupStep6Fragment.class, new Bundle());
    }

    @OnCheckedChanged({R.id.isStockRelatedEmployee, R.id.isStockHolder, R.id.isGovOfficer})
    public void onSwitchCheckChanged(CompoundButton view, boolean checked) {
        switch(view.getId()) {
            case R.id.isStockRelatedEmployee:
                if (checked) {
                    isStockRelatedEmployeeNote.setVisibility(View.VISIBLE);
                } else {
                    isStockRelatedEmployeeNote.setVisibility(View.GONE);
                }
                break;
            case R.id.isStockHolder:
                if (checked) {
                    stockCompanyName.setVisibility(View.VISIBLE);
                    stockCompanyCode.setVisibility(View.VISIBLE);
                } else {
                    stockCompanyName.setVisibility(View.GONE);
                    stockCompanyCode.setVisibility(View.GONE);
                }
                break;
            case R.id.isGovOfficer:
                if (checked) {
                    immediateFamilyName.setVisibility(View.VISIBLE);
                } else {
                    immediateFamilyName.setVisibility(View.GONE);
                }
                break;
        }

        checkNEnableNextButton();
    }

    @OnTextChanged({R.id.company})
    public void onEditTextChanged(CharSequence text) {
        checkNEnableNextButton();
    }

    private void checkNEnableNextButton() {
        if ((stockCompanyName.getVisibility() == View.VISIBLE && stockCompanyNameInput.getText().length() <= 0) ||
                (stockCompanyCode.getVisibility() == View.VISIBLE && stockCompanyCodeInput.getText().length() <= 0) ||
                (immediateFamilyName.getVisibility() == View.VISIBLE && immediateFamilyNameInput.getText().length() <= 0) ||
                company.getText().length() <= 0) {
            btnNext.setEnabled(false);
        } else {
            btnNext.setEnabled(true);
        }
    }
}
