package com.tradehero.th.fragments.authentication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.api.users.password.ForgotPasswordDTO;
import com.tradehero.th.api.users.password.ForgotPasswordFormDTO;
import com.tradehero.th.auth.AuthenticationMode;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.UserService;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.widget.SelfValidatedText;
import com.tradehero.th.widget.ServerValidatedEmailText;
import com.tradehero.th.widget.ValidatedPasswordText;
import java.util.Map;
import javax.inject.Inject;

public class EmailSignInFragment extends EmailSignInOrUpFragment
{
    private final static String TAG = EmailSignInFragment.class.getName();

    private SelfValidatedText email;
    private ValidatedPasswordText password;
    private TextView forgotPasswordLink;
    private ProgressDialog mProgressDialog;
    private View forgotDialogView;

    @Inject UserService userService;

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
            email.setText("neeraj@eatechnologies.com");
            password.setText("asd123");
        }

        signButton = (Button) view.findViewById(R.id.btn_login);
        signButton.setOnClickListener(this);

        forgotPasswordLink = (TextView) view.findViewById(R.id.authentication_sign_in_forgot_password);
        forgotPasswordLink.setOnClickListener(this);
    }

    @Override public void onDestroyView()
    {
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

        super.onDestroyView();
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
                .setPositiveButton(R.string.authentication_ok, new DialogInterface.OnClickListener()
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

        mProgressDialog = ProgressDialogUtil.show(
                getActivity(),
                R.string.alert_dialog_please_wait,
                R.string.authentication_connecting_tradehero_only);

        userService.forgotPassword(forgotPasswordFormDTO, createForgotPasswordCallback());
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
