package com.tradehero.livetrade.thirdPartyServices.drivewealth.views;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.tradehero.livetrade.thirdPartyServices.drivewealth.DriveWealthManager;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.DriveWealthServicesWrapper;
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
public class DriveWealthSignupStep3Fragment extends DashboardFragment {

    @Inject DriveWealthManager mDriveWealthManager;

    @InjectView(R.id.email)
    EditText email;
    @InjectView(R.id.nickname)
    EditText nickname;
    @InjectView(R.id.password1)
    EditText password1;
    @InjectView(R.id.password2)
    EditText password2;
    @InjectView(R.id.btn_next)
    Button btnNext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dw_signup_page3, container, false);
        ButterKnife.inject(this, view);

        DriveWealthSignupFormDTO formDTO = mDriveWealthManager.getSignupFormDTO();
        if (formDTO.email != null) {
            email.setText(formDTO.email);
        }

        if (formDTO.userName != null) {
            nickname.setText(formDTO.userName);
        }

        if (formDTO.password != null) {
            password1.setText(formDTO.password);
            password2.setText(formDTO.password);
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
        formDTO.email = email.getText().toString();
        formDTO.userName = nickname.getText().toString();
        formDTO.password = password1.getText().toString();

        pushFragment(DriveWealthSignupStep4Fragment.class, new Bundle());
    }

    @OnTextChanged({R.id.email, R.id.nickname, R.id.password1, R.id.password2})
    public void onEditTextChanged(CharSequence text) {
        checkNEnableNextButton();
    }

    private void checkNEnableNextButton() {
        if (email.getText().length() > 0 && nickname.getText().length() > 0 &&
                password1.getText().length() > 0 && password2.getText().length() > 0) {
            btnNext.setEnabled(true);
        } else {
            btnNext.setEnabled(false);
        }
    }
}
