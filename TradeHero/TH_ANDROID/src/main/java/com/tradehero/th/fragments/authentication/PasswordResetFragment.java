package com.tradehero.th.fragments.authentication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.view.Menu;
import android.view.MenuInflater;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.password.ForgotPasswordDTO;
import com.tradehero.th.api.users.password.ForgotPasswordFormDTO;
import com.tradehero.th.api.users.password.ResetPasswordDTO;
import com.tradehero.th.api.users.password.ResetPasswordFormDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.utils.EmailSignUtils;
import com.tradehero.th.utils.ProgressDialogUtil;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;

/**
 * Reset Password
 * Created by palmer on 14/12/1.
 */
public class PasswordResetFragment extends DashboardFragment implements View.OnClickListener, TextWatcher{

    private EditText accountET;
    private Button resetPwdButton;
    private TextView getVerifyCodeButton;
    private LinearLayout newPasswordLL;
    private RelativeLayout verifyCodeRL;
    private EditText verifyCodeET;
    private EditText newPwdET;

    private ProgressDialog mProgressDialog;
    @Inject UserServiceWrapper userServiceWrapper;
    @Inject ProgressDialogUtil progressDialogUtil;

    private String requestVerifyCodeStr = "";
    private MiddleCallback<Response> sendCodeMiddleCallback;

    protected MiddleCallback<ForgotPasswordDTO> middleCallbackForgotPasswordEmail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(R.string.sign_in_reset_password);
        setHeadViewRight0Visibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkRestBtnAvailable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.authentication_reset_password, container, false);
        newPasswordLL = (LinearLayout)view.findViewById(R.id.linearlayout_reset_password_new_password);
        verifyCodeRL = (RelativeLayout)view.findViewById(R.id.relativelayout_reset_password_verify_code_layout);
        resetPwdButton = (Button) view.findViewById(R.id.button_reset_password);
        resetPwdButton.setOnClickListener(this);
        accountET = (EditText) view.findViewById(R.id.edittext_reset_password_account);
        accountET.addTextChangedListener(this);
        verifyCodeET = (EditText)view.findViewById(R.id.reset_password_verify_code);
        verifyCodeET.addTextChangedListener(this);
        newPwdET = (EditText)view.findViewById(R.id.edittext_reset_password_password);
        newPwdET.addTextChangedListener(this);
        getVerifyCodeButton = (TextView)view.findViewById(R.id.reset_password_verify_code_button);
        getVerifyCodeButton.setOnClickListener(this);
        requestVerifyCodeStr = getString(R.string.login_get_verify_code);
        long limitTime = (System.currentTimeMillis() - EmailSignUpFragment.last_time_request_verify_code) / 1000;
        if (limitTime < EmailSignUpFragment.duration_verify_code && limitTime > 0) {
            getVerifyCodeButton.setClickable(false);
            getVerifyCodeButton.setBackgroundResource(R.drawable.yanzheng_again);
            getVerifyCodeButton.setText(String.valueOf(EmailSignUpFragment.duration_verify_code - limitTime));
            Handler handler = new Handler();
            handler.postDelayed(refreshVerifyCodeRunnable, 1000);
        } else {
            getVerifyCodeButton.setClickable(true);
            getVerifyCodeButton.setText(requestVerifyCodeStr);
            getVerifyCodeButton.setBackgroundResource(R.drawable.yanzheng);
            EmailSignUpFragment.last_time_request_verify_code = -1;
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        detachMiddleCallbackForgotPassword();
        detachSendCodeMiddleCallback();
        super.onDestroyView();
    }

    private void doForgetPasswordEmail(String email) {
        ForgotPasswordFormDTO forgotPasswordFormDTO = new ForgotPasswordFormDTO();
        forgotPasswordFormDTO.userEmail = email;

        mProgressDialog = progressDialogUtil.show(
                getActivity(),
                R.string.alert_dialog_please_wait,
                R.string.authentication_connecting_tradehero_only);

        detachMiddleCallbackForgotPassword();
        middleCallbackForgotPasswordEmail = userServiceWrapper
                .forgotPasswordEmail(forgotPasswordFormDTO, createForgotPasswordEmailCallback());
    }

    private void doForgetPasswordMobile() {
        ResetPasswordFormDTO resetPasswordFormDTO = new ResetPasswordFormDTO();
        resetPasswordFormDTO.newPassword = newPwdET.getText().toString();
        resetPasswordFormDTO.newPasswordConfirmation = newPwdET.getText().toString();
        resetPasswordFormDTO.verifyCode = verifyCodeET.getText().toString();
        resetPasswordFormDTO.phoneNumber = accountET.getText().toString();
        mProgressDialog = progressDialogUtil.show(
                getActivity(),
                R.string.alert_dialog_please_wait,
                R.string.authentication_connecting_tradehero_only);
        userServiceWrapper.resetPasswordMobile(resetPasswordFormDTO, new Callback<ResetPasswordDTO>() {
            @Override
            public void success(ResetPasswordDTO resetPasswordDTO, Response response) {
                if(resetPasswordDTO.resetPassword){
                    if(getActivity()!=null) {
                        THToast.show(getString(R.string.sign_in_reset_password_successfully));
                    }else{
                        THToast.show(getString(R.string.sign_in_reset_password_failed));
                    }
                }
                onFinish();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                THToast.show(retrofitError.getMessage());
                onFinish();
            }

            private void onFinish(){
                if(mProgressDialog!=null && mProgressDialog.isShowing()){
                    mProgressDialog.dismiss();
                }
                if(resetPwdButton!=null){
                    resetPwdButton.setEnabled(true);
                }
            }
        });
    }

    protected void detachMiddleCallbackForgotPassword() {
        if (middleCallbackForgotPasswordEmail != null) {
            middleCallbackForgotPasswordEmail.setPrimaryCallback(null);
        }
        middleCallbackForgotPasswordEmail = null;
    }

    private THCallback<ForgotPasswordDTO> createForgotPasswordEmailCallback() {
        return new THCallback<ForgotPasswordDTO>() {
            @Override
            protected void success(ForgotPasswordDTO forgotPasswordDTO, THResponse thResponse) {
                THToast.show(R.string.authentication_thank_you_message_email);
            }

            @Override
            public void failure(THException ex) {
                THToast.show(ex);
            }

            @Override
            protected void finish() {
                if (resetPwdButton != null) {
                    resetPwdButton.setEnabled(true);
                }
                mProgressDialog.dismiss();
            }
        };
    }

    private Runnable refreshVerifyCodeRunnable = new Runnable() {
        @Override
        public void run() {
            if (getVerifyCodeButton == null) {
                return;
            }
            long limitTime = (System.currentTimeMillis() - EmailSignUpFragment.last_time_request_verify_code) / 1000;
            if (limitTime < EmailSignUpFragment.duration_verify_code && limitTime > 0) {
                getVerifyCodeButton.setClickable(false);
                getVerifyCodeButton.setText(String.valueOf(EmailSignUpFragment.duration_verify_code - limitTime));
                getVerifyCodeButton.setBackgroundResource(R.drawable.yanzheng_again);
                Handler handler = new Handler();
                handler.postDelayed(refreshVerifyCodeRunnable, 1000);
            } else {
                getVerifyCodeButton.setClickable(true);
                getVerifyCodeButton.setText(requestVerifyCodeStr);
                getVerifyCodeButton.setBackgroundResource(R.drawable.yanzheng);
                EmailSignUpFragment.last_time_request_verify_code = -1;
            }
        }
    };

    private void detachSendCodeMiddleCallback() {
        if (sendCodeMiddleCallback != null) {
            sendCodeMiddleCallback.setPrimaryCallback(null);
        }
        sendCodeMiddleCallback = null;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId){
            case R.id.reset_password_verify_code_button:
                requestVerifyCode();
                break;
            case R.id.button_reset_password:
                resetPassword();
                break;

        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        checkRestBtnAvailable();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private void resetPassword(){
        if(accountET==null){
            return;
        }
        if(EmailSignUtils.isPhoneNumber(accountET.getText())){
            resetPwdButton.setEnabled(false);
            doForgetPasswordMobile();
            return;
        }
        if (EmailSignUtils.isValidEmail(accountET.getText())) {
            resetPwdButton.setEnabled(false);
            doForgetPasswordEmail(accountET.getText().toString());
            return;
        }
    }

    private class SendCodeCallback implements Callback<Response> {
        @Override
        public void success(Response response, Response response2) {
            THToast.show(R.string.send_verify_code_success);
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            THToast.show(new THException(retrofitError));
        }
    }

    private void requestVerifyCode() {
        getVerifyCodeButton.setClickable(false);
        getVerifyCodeButton.setText(String.valueOf(EmailSignUpFragment.duration_verify_code));
        getVerifyCodeButton.setBackgroundResource(R.drawable.yanzheng_again);
        EmailSignUpFragment.last_time_request_verify_code = System.currentTimeMillis();
        Handler handler = new Handler();
        handler.postDelayed(refreshVerifyCodeRunnable, 1000);
        detachSendCodeMiddleCallback();
        sendCodeMiddleCallback = userServiceWrapper.sendCode(accountET.getText().toString(), new SendCodeCallback());
    }

    private void checkRestBtnAvailable(){
        if (EmailSignUtils.isValidEmail(accountET.getText()) || EmailSignUtils.isPhoneNumber(accountET.getText())) {
            resetPwdButton.setEnabled(true);
            if(EmailSignUtils.isPhoneNumber(accountET.getText())){
                newPasswordLL.setVisibility(View.VISIBLE);
                verifyCodeRL.setVisibility(View.VISIBLE);
                if(newPwdET.getText().length()<6 || verifyCodeET.getText().length()<4){
                    resetPwdButton.setEnabled(false);
                }else{
                    resetPwdButton.setEnabled(true);
                }
            }else{
                newPasswordLL.setVisibility(View.GONE);
                verifyCodeRL.setVisibility(View.GONE);
            }
        }else{
            resetPwdButton.setEnabled(false);
            newPasswordLL.setVisibility(View.GONE);
            verifyCodeRL.setVisibility(View.GONE);
        }
    }
}
