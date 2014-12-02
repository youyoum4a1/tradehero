package com.tradehero.th.fragments.authentication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.password.ForgotPasswordDTO;
import com.tradehero.th.api.users.password.ForgotPasswordFormDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.utils.EmailSignUtils;
import com.tradehero.th.utils.ProgressDialogUtil;

import javax.inject.Inject;

/**
 * Reset Password
 * Created by palmer on 14/12/1.
 */
public class PasswordResetFragment extends DashboardFragment {

    private EditText accountET;
    private Button resetPwdButton;

    private ProgressDialog mProgressDialog;
    @Inject UserServiceWrapper userServiceWrapper;
    @Inject ProgressDialogUtil progressDialogUtil;

    protected MiddleCallback<ForgotPasswordDTO> middleCallbackForgotPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(R.string.sign_in_reset_password);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (EmailSignUtils.isValidEmail(accountET.getText())) {
            resetPwdButton.setEnabled(true);
        } else {
            resetPwdButton.setEnabled(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.authentication_reset_password, container, false);
        resetPwdButton = (Button) view.findViewById(R.id.button_reset_password);
        resetPwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (accountET != null && EmailSignUtils.isValidEmail(accountET.getText())) {
                    doForgotPassword(accountET.getText().toString());
                    resetPwdButton.setEnabled(false);
                }
            }
        });
        accountET = (EditText) view.findViewById(R.id.edittext_reset_password_account);
        accountET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (EmailSignUtils.isValidEmail(editable)) {
                    resetPwdButton.setEnabled(true);
                } else {
                    resetPwdButton.setEnabled(false);
                }
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        detachMiddleCallbackForgotPassword();
        super.onDestroyView();
    }

    private void doForgotPassword(String email) {
        ForgotPasswordFormDTO forgotPasswordFormDTO = new ForgotPasswordFormDTO();
        forgotPasswordFormDTO.userEmail = email;

        mProgressDialog = progressDialogUtil.show(
                getActivity(),
                R.string.alert_dialog_please_wait,
                R.string.authentication_connecting_tradehero_only);

        detachMiddleCallbackForgotPassword();
        middleCallbackForgotPassword = userServiceWrapper
                .forgotPassword(forgotPasswordFormDTO, createForgotPasswordCallback());
    }

    protected void detachMiddleCallbackForgotPassword() {
        if (middleCallbackForgotPassword != null) {
            middleCallbackForgotPassword.setPrimaryCallback(null);
        }
        middleCallbackForgotPassword = null;
    }

    private THCallback<ForgotPasswordDTO> createForgotPasswordCallback() {
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

}
