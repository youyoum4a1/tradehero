package com.tradehero.livetrade.thirdPartyServices.drivewealth.views;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.tradehero.livetrade.thirdPartyServices.drivewealth.DriveWealthManager;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthSignupFormDTO;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class DriveWealthSignupStep4Fragment extends DashboardFragment {

    @Inject DriveWealthManager mDriveWealthManager;

    @InjectView(R.id.lastName)
    EditText lastName;
    @InjectView(R.id.firstName)
    EditText firstName;
    @InjectView(R.id.lastNameEnglish)
    EditText lastNameEnglish;
    @InjectView(R.id.firstNameEnglish)
    EditText firstNameEnglish;
    @InjectView(R.id.idNumber)
    EditText idNumber;
    @InjectView(R.id.address)
    EditText address;
    @InjectView(R.id.btn_next)
    Button btnNext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dw_signup_page4, container, false);
        ButterKnife.inject(this, view);

        DriveWealthSignupFormDTO formDTO = mDriveWealthManager.getSignupFormDTO();
        if (formDTO.firstName != null) {
            firstName.setText(formDTO.firstName);
        }

        if (formDTO.lastName != null) {
            lastName.setText(formDTO.lastName);
        }

        if (formDTO.firstNameInEng != null) {
            firstNameEnglish.setText(formDTO.firstNameInEng);
        }

        if (formDTO.lastNameInEng != null) {
            lastNameEnglish.setText(formDTO.lastNameInEng);
        }

        if (formDTO.idNO != null) {
            idNumber.setText(formDTO.idNO);
        }

        if (formDTO.address != null) {
            address.setText(formDTO.address);
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
        formDTO.firstName = firstName.getText().toString();
        formDTO.lastName = lastName.getText().toString();
        formDTO.firstNameInEng = firstNameEnglish.getText().toString();
        formDTO.lastNameInEng = lastNameEnglish.getText().toString();
        formDTO.idNO = idNumber.getText().toString();
        formDTO.address = address.getText().toString();

        pushFragment(DriveWealthSignupStep5Fragment.class, new Bundle());
    }

    @OnTextChanged({R.id.lastName, R.id.firstName, R.id.lastNameEnglish, R.id.firstNameEnglish, R.id.idNumber, R.id.address})
    public void onEditTextChanged(CharSequence text) {
        checkNEnableNextButton();
    }

    private void checkNEnableNextButton() {
        if (lastName.getText().length() > 0 && firstName.getText().length() > 0 &&
                lastNameEnglish.getText().length() > 0 && firstNameEnglish.getText().length() > 0 &&
                idNumber.getText().length() > 0 && address.getText().length() > 0) {
            btnNext.setEnabled(true);
        } else {
            btnNext.setEnabled(false);
        }
    }
}
