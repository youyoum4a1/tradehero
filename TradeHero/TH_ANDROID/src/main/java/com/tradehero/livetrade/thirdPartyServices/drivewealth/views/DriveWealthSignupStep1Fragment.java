package com.tradehero.livetrade.thirdPartyServices.drivewealth.views;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.DriveWealthManager;
import com.tradehero.livetrade.thirdPartyServices.drivewealth.data.DriveWealthSignupFormDTO;
import com.tradehero.th.BuildConfig;
import com.tradehero.th.R;
import com.tradehero.th.api.users.password.PhoneNumberVerifyDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.utils.DeviceUtil;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * @author <a href="mailto:sam@tradehero.mobi"> Sam Yu </a>
 */
public class DriveWealthSignupStep1Fragment extends DriveWealthSignupBaseFragment {

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

    private ProgressDialog mProgressDialog;

    public static long last_time_request_verify_code = -1;
    public final static long duration_verify_code = 60;
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
                getVerifyCodeButton.setText("获取验证码");
                getVerifyCodeButton.setBackgroundResource(R.drawable.yanzheng);
                last_time_request_verify_code = -1;
            }
        }
    };

    @Override
    public String getTitle() {
        return "手机验证(1/7)";
    }

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

    @OnClick(R.id.txt_term_of_service_signin)
    public void onTermClicked() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://drivewealth.com/zh-hans/customer-account-agreement/"));
        startActivity(browserIntent);
    }

    @OnClick(R.id.btn_next)
    public void onNextClick() {
        DeviceUtil.dismissKeyboard(getActivity());
        if (BuildConfig.DEBUG) {
            pushFragment(DriveWealthSignupStep2Fragment.class, new Bundle());
            return;
        }
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
        } else {
            mProgressDialog.dismiss();
        }
        mProgressDialog.setMessage(getString(R.string.verifying_phonenum));
        mProgressDialog.show();

        userServiceWrapper.verifyPhoneNum(phoneNumber.getText().toString(),
                verifyCode.getText().toString(),
                new Callback<PhoneNumberVerifyDTO>() {
                    @Override
                    public void success(PhoneNumberVerifyDTO phoneNumberVerifyDTO, Response response) {
                        if (phoneNumberVerifyDTO.success) {
                            userServiceWrapper.checkPhoneNumberAccountStatus(
                                    phoneNumber.getText().toString(), new Callback<PhoneNumberVerifyDTO>() {
                                        @Override
                                        public void success(PhoneNumberVerifyDTO phoneNumberVerifyDTO, Response response) {
                                            mProgressDialog.dismiss();
                                            if (phoneNumberVerifyDTO.code == PhoneNumberVerifyDTO.CODE_NO_SUCH_ACCOUNT) {
                                                DriveWealthSignupFormDTO formDTO = mDriveWealthManager.getSignupFormDTO();
                                                formDTO.phoneNumber = phoneNumber.getText().toString();
                                                formDTO.phoneVerificationToken = verifyCode.getText().toString();

                                                pushFragment(DriveWealthSignupStep2Fragment.class, new Bundle());
                                            } else {
                                                THDialog.showCenterDialog(getActivity(), "", "此手机号已经开户\n请直接登录。",
                                                        null, getString(R.string.ok),
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                if (which == DialogInterface.BUTTON_POSITIVE) {
                                                                    getActivity().finish();
                                                                }
                                                            }
                                                        });
                                            }
                                        }

                                        @Override
                                        public void failure(RetrofitError error) {
                                            THToast.show(error.getLocalizedMessage());
                                            mProgressDialog.dismiss();

                                        }
                                    });
                        } else {
                            THToast.show(phoneNumberVerifyDTO.reason);
                            mProgressDialog.dismiss();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        THToast.show(error.getLocalizedMessage());
                        mProgressDialog.dismiss();
                    }
                });
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

    @OnClick(R.id.signup_status_check)
    public void checkPhoneNumStatus() {
        Bundle bundle = new Bundle();
        bundle.putString(DashboardFragment.BUNDLE_KEY_TITLE, getString(R.string.signup_status_check));
        pushFragment(DriveWealthCheckSignupStatusFragment.class, bundle);
    }
}
