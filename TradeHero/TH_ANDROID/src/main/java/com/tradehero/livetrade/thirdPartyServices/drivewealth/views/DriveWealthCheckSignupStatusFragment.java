package com.tradehero.livetrade.thirdPartyServices.drivewealth.views;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.DriveWealthManager;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthSignupFormDTO;
import com.tradehero.th.R;
import com.tradehero.th.api.users.password.PhoneNumberVerifyDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.UserServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DriveWealthCheckSignupStatusFragment extends DashboardFragment implements View.OnClickListener {

    @Inject
    DriveWealthManager mDriveWealthManager;
    @Inject
    Lazy<UserServiceWrapper> userServiceWrapper;
    private EditText mPhoneNumberText;
    private EditText mVerifyCodeText;
    private TextView mGetVerifyCodeButton;
    private Button mQueryBtn;
    private ProgressDialog mProgressDialog;
    private TextView mErrorMsgText;

    public static long last_time_request_verify_code = -1;
    public final static long duration_verify_code = 60;
    private String requestVerifyCodeStr = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dw_check_signup_status_page, container, false);
        initView(view);

        DriveWealthSignupFormDTO formDTO = mDriveWealthManager.getSignupFormDTO();
        if (formDTO.phoneNumber != null) {
            mPhoneNumberText.setText(formDTO.phoneNumber);
        }

        if (formDTO.phoneVerificationToken != null) {
            mVerifyCodeText.setText((formDTO.phoneVerificationToken));
        }

        checkNEnableNextButton();

        return view;
    }

    private void initView(View view) {
        mPhoneNumberText = (EditText)view.findViewById(R.id.phone_number);
        mPhoneNumberText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkNEnableNextButton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mVerifyCodeText = (EditText)view.findViewById(R.id.verify_code);
        mVerifyCodeText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkNEnableNextButton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mGetVerifyCodeButton = (TextView)view.findViewById(R.id.get_verify_code_button);
        mGetVerifyCodeButton.setOnClickListener(this);
        mErrorMsgText = (TextView) view.findViewById(R.id.error_msg);
        mQueryBtn = (Button) view.findViewById(R.id.btn_next);
        mQueryBtn.setOnClickListener(this);
    }

    private void checkNEnableNextButton() {
        if (mPhoneNumberText.getText().length() > 0 && mVerifyCodeText.getText().length() > 0) {
            mQueryBtn.setEnabled(true);
        } else {
            mQueryBtn.setEnabled(false);
        }
    }

    private Runnable refreshVerifyCodeRunnable = new Runnable() {
        @Override
        public void run() {
            if (mGetVerifyCodeButton == null) {
                return;
            }
            long limitTime = (System.currentTimeMillis() - last_time_request_verify_code) / 1000;
            if (limitTime < duration_verify_code && limitTime > 0) {
                mGetVerifyCodeButton.setClickable(false);
                mGetVerifyCodeButton.setText(String.valueOf(duration_verify_code - limitTime));
                mGetVerifyCodeButton.setBackgroundResource(R.drawable.yanzheng_again);
                Handler handler = new Handler();
                handler.postDelayed(refreshVerifyCodeRunnable, 1000);
            } else {
                mGetVerifyCodeButton.setClickable(true);
                mGetVerifyCodeButton.setText(requestVerifyCodeStr);
                mGetVerifyCodeButton.setBackgroundResource(R.drawable.yanzheng);
                last_time_request_verify_code = -1;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                onNextClick();
                break;
            case R.id.get_verify_code_button:
                onGetVerifyCodeClick();
                break;
        }
    }

    public void onNextClick() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
        } else {
            mProgressDialog.dismiss();
        }
        mProgressDialog.setMessage(getString(R.string.verifying_phonenum));
        mProgressDialog.show();

        userServiceWrapper.get().verifyPhoneNum(mPhoneNumberText.getText().toString(),
                mVerifyCodeText.getText().toString(), new Callback<PhoneNumberVerifyDTO>() {
                    @Override
                    public void success(PhoneNumberVerifyDTO phoneNumberVerifyDTO, Response response) {
                        mProgressDialog.dismiss();
                        if (phoneNumberVerifyDTO.success) {
                            mErrorMsgText.setVisibility(View.GONE);
                            mProgressDialog.setMessage(getString(R.string.checking_phonenum_account_status));
                            mProgressDialog.show();
                            userServiceWrapper.get().checkPhoneNumberAccountStatus(
                                    mPhoneNumberText.getText().toString(), new Callback<PhoneNumberVerifyDTO>() {
                                        @Override
                                        public void success(PhoneNumberVerifyDTO phoneNumberVerifyDTO, Response response) {
                                            mProgressDialog.dismiss();
                                            if (phoneNumberVerifyDTO.success) {
                                                THDialog.showCenterDialog(getActivity(), "", phoneNumberVerifyDTO.reason, getString(R.string.cancel),
                                                        getString(R.string.login_open_account_right_now), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if (which == DialogInterface.BUTTON_POSITIVE) {
                                                            THToast.show("新功能正在开发中");
                                                        }
                                                    }
                                                });
                                            } else {
                                                THDialog.showCenterDialog(getActivity(), "", phoneNumberVerifyDTO.reason, getString(R.string.cancel),
                                                        getString(R.string.open_account_right_now), new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if (which == DialogInterface.BUTTON_POSITIVE) {
                                                            gotoDashboard(DriveWealthSignupStep1Fragment.class);
                                                        }
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            mProgressDialog.dismiss();

                                        }
                                    });
                        } else {
                            mErrorMsgText.setText(phoneNumberVerifyDTO.reason);
                            mErrorMsgText.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        mProgressDialog.dismiss();

                    }
                });
    }

    public void onGetVerifyCodeClick() {
        mGetVerifyCodeButton.setClickable(false);
        mGetVerifyCodeButton.setText(String.valueOf(duration_verify_code));
        mGetVerifyCodeButton.setBackgroundResource(R.drawable.yanzheng_again);
        last_time_request_verify_code = System.currentTimeMillis();
        Handler handler = new Handler();
        handler.postDelayed(refreshVerifyCodeRunnable, 1000);

        userServiceWrapper.get().sendCode(mPhoneNumberText.getText().toString(), new Callback<Response>() {
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
}
