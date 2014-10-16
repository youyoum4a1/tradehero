package com.tradehero.th.fragments.authentication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.LoginSignUpFormDTO;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.password.ForgotPasswordDTO;
import com.tradehero.th.api.users.password.ForgotPasswordFormDTO;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.SessionServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.widget.SelfValidatedText;
import com.tradehero.th.widget.ServerValidatedEmailText;
import com.tradehero.th.widget.ValidatedPasswordText;
import com.tradehero.th.widget.ValidatedText;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;
import rx.Subscription;
import rx.android.observables.ViewObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observers.EmptyObserver;
import rx.schedulers.Schedulers;

public class EmailSignInFragment extends Fragment
{
    private ProgressDialog mProgressDialog;
    private View forgotDialogView;

    @Inject UserServiceWrapper userServiceWrapper;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject Analytics analytics;
    @Inject DashboardNavigator navigator;
    @Inject Provider<LoginSignUpFormDTO.Builder2> loginSignUpFormDTOProvider;
    @Inject SessionServiceWrapper sessionServiceWrapper;
    @Inject ToastOnErrorAction toastOnErrorAction;
    @Inject Provider<AuthDataAction> authDataActionProvider;

    @InjectView(R.id.authentication_sign_in_email) SelfValidatedText email;
    @InjectView(R.id.et_pwd_login) ValidatedPasswordText password;
    @InjectView(R.id.btn_login) View loginButton;
    Subscription validationSubscription;
    private ProgressDialog progressDialog;
    private Observable<Pair<AuthData, UserProfileDTO>> signInObservable;
    private Subscription signInSubscription;

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.authentication_back_button) void handleBackButtonClicked()
    {
        navigator.popFragment();
    }

    @SuppressWarnings("UnusedDeclaration")
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

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.authentication_email_sign_in, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        initSetup(view);
        DeviceUtil.showKeyboardDelayed(email);
    }

    protected void initSetup(View view)
    {
        if (!Constants.RELEASE)
        {
            email.setText(getString(R.string.test_email));
            password.setText(getString(R.string.test_password));
            loginButton.setEnabled(true);
        }

        validationSubscription = Observable.combineLatest(
                ViewObservable.text(email),
                ViewObservable.text(password),
                new Func2<ValidatedText, ValidatedText, Pair<Boolean, Boolean>>()
                {
                    @Override public Pair<Boolean, Boolean> call(ValidatedText email, ValidatedText password)
                    {
                        email.forceValidate();
                        password.forceValidate();
                        return Pair.create(email.isValid(), password.isValid());
                    }
                })
                .subscribe(new EmptyObserver<Pair<Boolean, Boolean>>()
                {
                    @Override public void onNext(Pair<Boolean, Boolean> args)
                    {
                        loginButton.setEnabled(args.first && args.second);
                        super.onNext(args);
                    }
                });

        signInObservable = ViewObservable.clicks(loginButton, false)
                .map(new Func1<View, AuthData>()
                {
                    @Override public AuthData call(View view)
                    {
                        DeviceUtil.dismissKeyboard(view);
                        return new AuthData(email.getText().toString(), password.getText().toString());
                    }
                })
                .doOnNext(new Action1<AuthData>()
                {
                    @Override public void call(AuthData AuthData)
                    {
                        progressDialog = ProgressDialog.show(getActivity(), getString(R.string.alert_dialog_please_wait),
                                getString(R.string.authentication_connecting_tradehero_only), true);
                    }
                })
                .map(new Func1<AuthData, LoginSignUpFormDTO>()
                {
                    @Override public LoginSignUpFormDTO call(AuthData authData)
                    {
                        return loginSignUpFormDTOProvider.get()
                                .authData(authData)
                                .build();
                    }
                })
                .flatMap(new Func1<LoginSignUpFormDTO, Observable<Pair<AuthData, UserProfileDTO>>>()
                {
                    @Override public Observable<Pair<AuthData, UserProfileDTO>> call(final LoginSignUpFormDTO loginSignUpFormDTO)
                    {
                        AuthData authData = loginSignUpFormDTO.authData;
                        Observable<UserProfileDTO> userLoginDTOObservable = sessionServiceWrapper.signupAndLoginRx(
                                authData.getTHToken(), loginSignUpFormDTO)
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .map(new Func1<UserLoginDTO, UserProfileDTO>()
                                {
                                    @Override public UserProfileDTO call(UserLoginDTO userLoginDTO)
                                    {
                                        return userLoginDTO.profileDTO;
                                    }
                                });

                        return Observable.zip(Observable.just(authData), userLoginDTOObservable,
                                new Func2<AuthData, UserProfileDTO, Pair<AuthData, UserProfileDTO>>()
                                {
                                    @Override public Pair<AuthData, UserProfileDTO> call(AuthData authData, UserProfileDTO userLoginDTO)
                                    {
                                        return Pair.create(authData, userLoginDTO);
                                    }
                                })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(authDataActionProvider.get())
                .doOnNext(new OpenDashboardAction(getActivity()))
                .doOnError(toastOnErrorAction)
                .doOnUnsubscribe(new Action0()
                {
                    @Override public void call()
                    {
                        if (progressDialog != null)
                        {
                            progressDialog.dismiss();
                        }
                    }
                })
                .retry();
    }

    @Override public void onResume()
    {
        super.onResume();
        if (signInSubscription == null || signInSubscription.isUnsubscribed())
        {
            signInSubscription = signInObservable.subscribe(new EmptyObserver<Pair<AuthData, UserProfileDTO>>());
        }
    }

    @Override public void onPause()
    {
        if (signInSubscription != null && !signInSubscription.isUnsubscribed())
        {
            signInSubscription.unsubscribe();
        }

        super.onPause();
    }

    @Override public void onDestroyView()
    {
        validationSubscription.unsubscribe();
        validationSubscription = null;
        detachMiddleCallbackForgotPassword();
        ButterKnife.reset(this);
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
}
