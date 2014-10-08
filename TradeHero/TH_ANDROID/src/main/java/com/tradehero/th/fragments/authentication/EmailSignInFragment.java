package com.tradehero.th.fragments.authentication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.users.password.ForgotPasswordDTO;
import com.tradehero.th.api.users.password.ForgotPasswordFormDTO;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.widget.SelfValidatedText;
import com.tradehero.th.widget.ServerValidatedEmailText;
import com.tradehero.th.widget.ValidatedPasswordText;
import com.tradehero.th.widget.ValidationListener;
import com.tradehero.th.widget.ValidationMessage;
import java.util.Map;
import java.util.concurrent.CancellationException;
import javax.inject.Inject;
import rx.Observable;
import rx.android.observables.ViewObservable;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

public class EmailSignInFragment extends EmailSignInOrUpFragment
{
    private PublishSubject<AuthData> authDataSubject = PublishSubject.create();

    private ProgressDialog mProgressDialog;
    private View forgotDialogView;

    @Inject UserServiceWrapper userServiceWrapper;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject Analytics analytics;
    @Inject DashboardNavigator navigator;

    @InjectView(R.id.authentication_sign_in_email) SelfValidatedText email;
    @InjectView(R.id.et_pwd_login) ValidatedPasswordText password;
    @InjectView(R.id.btn_login) View loginButton;
    private Observable<ValidationMessage> validationObservable;

    @OnClick(R.id.authentication_back_button) void handleBackButtonClicked()
    {
        navigator.popFragment();
    }

    @OnClick(R.id.authentication_sign_in_forgot_password) void showForgotPasswordUI()
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

    protected MiddleCallback<ForgotPasswordDTO> middleCallbackForgotPassword;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        HierarchyInjector.inject(this);
        analytics.tagScreen(AnalyticsConstants.Login_Form);
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.LoginFormScreen));
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        DeviceUtil.showKeyboardDelayed(email);
    }

    @Override public int getDefaultViewId()
    {
        return R.layout.authentication_email_sign_in;
    }

    @Override protected void initSetup(View view)
    {
        if (!Constants.RELEASE)
        {
            email.setText(getString(R.string.test_email));
            password.setText(getString(R.string.test_password));
        }

        final PublishSubject<ValidationMessage> emailValidationSubject = PublishSubject.create();
        final PublishSubject<ValidationMessage> passwordValidationSubject = PublishSubject.create();

        email.setValidationListener(new ValidationListener()
        {
            @Override public void notifyValidation(ValidationMessage status)
            {
                emailValidationSubject.onNext(status);
            }
        });

        password.setValidationListener(new ValidationListener()
        {
            @Override public void notifyValidation(ValidationMessage status)
            {
                passwordValidationSubject.onNext(status);
            }
        });

        validationObservable = Observable.merge(emailValidationSubject, passwordValidationSubject)
                .last()
                .first(new Func1<ValidationMessage, Boolean>()
                {
                    @Override public Boolean call(ValidationMessage validationMessage)
                    {
                        return validationMessage.getStatus();
                    }
                })
                .asObservable();

        ViewObservable.clicks(loginButton, false)
                .map(new Func1<View, AuthData>()
                {
                    @Override public AuthData call(View view)
                    {
                        return new AuthData(email.getText().toString(), password.getText().toString());
                    }
                })
                .subscribe(authDataSubject);
    }

    @Override public void onDestroyView()
    {
        detachMiddleCallbackForgotPassword();
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        authDataSubject.onError(new CancellationException(getString(R.string.error_canceled)));
        super.onDestroy();
    }

    protected void detachMiddleCallbackForgotPassword()
    {
        if (middleCallbackForgotPassword != null)
        {
            middleCallbackForgotPassword.setPrimaryCallback(null);
        }
        middleCallbackForgotPassword = null;
    }

    @Override public boolean areFieldsValid()
    {
        return email.isValid() && password.isValid();
    }

    @Override protected Map<String, Object> getUserFormMap()
    {
        Map<String, Object> map = super.getUserFormMap();
        map.put(UserFormDTO.KEY_EMAIL, email.getText().toString());
        map.put(UserFormDTO.KEY_PASSWORD, password.getText().toString());
        return map;
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

    public Observable<AuthData> obtainAuthData()
    {
        return authDataSubject.asObservable();
    }
}
