package com.tradehero.th.fragments.authentication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.localytics.android.LocalyticsSession;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.api.users.password.ForgotPasswordDTO;
import com.tradehero.th.api.users.password.ForgotPasswordFormDTO;
import com.tradehero.th.auth.AuthenticationMode;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import com.tradehero.th.widget.SelfValidatedText;
import com.tradehero.th.widget.ServerValidatedEmailText;
import com.tradehero.th.widget.ValidatedPasswordText;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class EmailSignInFragment extends EmailSignInOrUpFragment
{
    private SelfValidatedText email;
    private ValidatedPasswordText password;
    private TextView forgotPasswordLink;
    private ProgressDialog mProgressDialog;
    private View forgotDialogView;
    private ImageView backButton;

    @Inject UserServiceWrapper userServiceWrapper;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject Lazy<LocalyticsSession> localyticsSession;

    protected MiddleCallback<ForgotPasswordDTO> middleCallbackForgotPassword;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
        List custom_dimensions = new ArrayList();
        custom_dimensions.add(Constants.TAP_STREAM_TYPE.name());
        localyticsSession.get().open(custom_dimensions);
        localyticsSession.get().tagScreen(LocalyticsConstants.Login_Form);
        localyticsSession.get().tagEvent(LocalyticsConstants.LoginFormScreen);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        DeviceUtil.showKeyboardDelayed(email);
    }

    @Override public int getDefaultViewId ()
    {
        return R.layout.authentication_email_sign_in;
    }

    @Override protected void initSetup(View view)
    {
        email = (SelfValidatedText) view.findViewById(R.id.authentication_sign_in_email);
        email.addListener(this);

        password = (ValidatedPasswordText) view.findViewById(R.id.et_pwd_login);
        password.addListener(this);

        // HACK
        if (!Constants.RELEASE)
        {
            email.setText(getString(R.string.test_email));
            password.setText(getString(R.string.test_password));
        }

        signButton = (Button) view.findViewById(R.id.btn_login);
        signButton.setOnClickListener(this);

        forgotPasswordLink = (TextView) view.findViewById(R.id.authentication_sign_in_forgot_password);
        forgotPasswordLink.setOnClickListener(this);

        backButton = (ImageView) view.findViewById(R.id.authentication_by_sign_in_back_button);
        backButton.setOnClickListener(onClickListener);
    }

    @Override public void onDestroyView()
    {
        detachMiddleCallbackForgotPassword();
        if (this.email != null)
        {
            this.email.removeAllListeners();
        }
        this.email = null;

        if (this.password != null)
        {
            this.password.removeAllListeners();
        }
        this.password = null;

        if (this.signButton != null)
        {
            this.signButton.setOnClickListener(null);
        }
        this.signButton = null;

        if (this.forgotPasswordLink != null)
        {
            this.forgotPasswordLink.setOnClickListener(null);
        }
        this.forgotPasswordLink = null;
        if (backButton != null)
        {
            backButton.setOnClickListener(null);
            backButton = null;
        }
        List custom_dimensions = new ArrayList();
        custom_dimensions.add(Constants.TAP_STREAM_TYPE.name());
        localyticsSession.get().close(custom_dimensions);
        localyticsSession.get().upload();
        super.onDestroyView();
    }

    protected void detachMiddleCallbackForgotPassword()
    {
        if (middleCallbackForgotPassword != null)
        {
            middleCallbackForgotPassword.setPrimaryCallback(null);
        }
        middleCallbackForgotPassword = null;
    }

    @Override public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_login:
                //clear old user info
                //THUser.clearCurrentUser();
                handleSignInOrUpButtonClicked(view);
                break;

            case R.id.authentication_sign_in_forgot_password:
                showForgotPasswordUI();
                break;

            default:
                break;
        }
    }

    @Override protected void forceValidateFields ()
    {
        email.forceValidate();
        password.forceValidate();
    }

    @Override public boolean areFieldsValid ()
    {
        return email.isValid() && password.isValid();
    }

    @Override protected Map<String, Object> getUserFormMap ()
    {
        Map<String, Object> map = super.getUserFormMap();
        map.put(UserFormFactory.KEY_EMAIL, email.getText().toString());
        map.put(UserFormFactory.KEY_PASSWORD, password.getText().toString());
        return map;
    }

    private void showForgotPasswordUI()
    {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        forgotDialogView = inflater.inflate(R.layout.forgot_password_dialog, null);

        String message = getActivity().getString(R.string.authentication_ask_for_email);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage(message)
                .setView(forgotDialogView)
                .setNegativeButton(R.string.authentication_cancel, new DialogInterface.OnClickListener()
                {
                    @Override public void onClick(DialogInterface dialogInterface, int which)
                    {
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int which)
                    {

                        ServerValidatedEmailText serverValidatedEmailText =
                                (ServerValidatedEmailText) forgotDialogView.findViewById(R.id.authentication_forgot_password_validated_email);
                        if (serverValidatedEmailText == null)
                        {
                            return;
                        }
                        serverValidatedEmailText.forceValidate();

                        if (!serverValidatedEmailText.isValid())
                        {
                            THToast.show(R.string.forgot_email_incorrect_input_email);
                        }
                        else
                        {
                            String email = serverValidatedEmailText.getText().toString();
                            doForgotPassword(email);
                            dialogInterface.dismiss();
                        }
                    }
                }).create().show();
    }

    private void doForgotPassword(String email)
    {
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

    private THCallback<ForgotPasswordDTO> createForgotPasswordCallback()
    {
        return new THCallback<ForgotPasswordDTO>()
        {
            @Override protected void success(ForgotPasswordDTO forgotPasswordDTO, THResponse thResponse)
            {
                THToast.show(R.string.authentication_thank_you_message_email);
            }

            @Override public void failure(THException ex)
            {
                THToast.show(ex);
            }

            @Override protected void finish()
            {
                mProgressDialog.dismiss();
            }
        };
    }

    @Override public AuthenticationMode getAuthenticationMode()
    {
        return AuthenticationMode.SignIn;
    }
}
