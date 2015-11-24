package com.tradehero.livetrade.thirdPartyServices.drivewealth.views;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tradehero.common.utils.THToast;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.DriveWealthManager;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthSignupFormDTO;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.UserServiceWrapper;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class DriveWealthSignupStep1Fragment extends DashboardFragment {

    @Inject DriveWealthManager mDriveWealthManager;
    @Inject UserServiceWrapper userServiceWrapper;
    @InjectView(R.id.phone_number)
    EditText phoneNumber;
    @InjectView(R.id.verify_code)
    EditText verifyCode;
    @InjectView(R.id.get_verify_code_button)
    TextView getVerifyCodeButton;
    @InjectView(R.id.btn_next)
    Button btnNext;
    @InjectView(R.id.txt_term_of_service_signin)
    TextView txtTermOfServiceSignin;
    @InjectView(R.id.signup_status_check)
    TextView signupStatusCheck;

    public static long last_time_request_verify_code = -1;
    public final static long duration_verify_code = 60;
    private String requestVerifyCodeStr = "";
    private Runnable refreshVerifyCodeRunnable = new Runnable() {
        @Override
        public void run() {
            if (getVerifyCodeButton == null) {
                return;
            }
            long limitTime = (System.currentTimeMillis() - last_time_request_verify_code) / 1000;
            if (limitTime < duration_verify_code && limitTime > 0) {
                getVerifyCodeButton.setClickable(false);
                getVerifyCodeButton.setText(String.valueOf(duration_verify_code - limitTime));
                getVerifyCodeButton.setBackgroundResource(R.drawable.yanzheng_again);
                Handler handler = new Handler();
                handler.postDelayed(refreshVerifyCodeRunnable, 1000);
            } else {
                getVerifyCodeButton.setClickable(true);
                getVerifyCodeButton.setText(requestVerifyCodeStr);
                getVerifyCodeButton.setBackgroundResource(R.drawable.yanzheng);
                last_time_request_verify_code = -1;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dw_signup_page1, container, false);
        ButterKnife.inject(this, view);

        DriveWealthSignupFormDTO formDTO = mDriveWealthManager.getSignupFormDTO();
        if (formDTO.phoneNumber != null) {
            phoneNumber.setText(formDTO.phoneNumber);
        }

        if (formDTO.phoneVerificationToken != null) {
            verifyCode.setText((formDTO.phoneVerificationToken));
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
        formDTO.phoneNumber = phoneNumber.getText().toString();
        formDTO.phoneVerificationToken = verifyCode.getText().toString();

        pushFragment(DriveWealthSignupStep2Fragment.class, new Bundle());
    }

    @OnClick(R.id.get_verify_code_button)
    public void onGetVerifyCodeClick() {
        getVerifyCodeButton.setClickable(false);
        getVerifyCodeButton.setText(String.valueOf(duration_verify_code));
        getVerifyCodeButton.setBackgroundResource(R.drawable.yanzheng_again);
        last_time_request_verify_code = System.currentTimeMillis();
        Handler handler = new Handler();
        handler.postDelayed(refreshVerifyCodeRunnable, 1000);

        userServiceWrapper.sendCode(phoneNumber.getText().toString(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                // Do nothing.
            }

            @Override
            public void failure(RetrofitError error) {
                THToast.show(new THException(error));
                last_time_request_verify_code = -1;
            }
        });
    }

    @OnTextChanged({R.id.phone_number, R.id.verify_code})
    public void onEditTextChanged(CharSequence text) {
        checkNEnableNextButton();
    }

    private void checkNEnableNextButton() {
        if (phoneNumber.getText().length() > 0 && verifyCode.getText().length() > 0) {
            btnNext.setEnabled(true);
        } else {
            btnNext.setEnabled(false);
        }
    }
}
