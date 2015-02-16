package com.tradehero.th.fragments.authentication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.users.LoginSignUpFormDTO;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.password.ForgotPasswordDTO;
import com.tradehero.th.api.users.password.ForgotPasswordFormDTO;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.network.service.SessionServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.rx.view.DismissDialogAction0;
import com.tradehero.th.rx.view.DismissDialogAction1;
import com.tradehero.th.utils.AlertDialogRxUtil;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.appsflyer.AppsFlyerConstants;
import com.tradehero.th.utils.metrics.appsflyer.THAppsFlyer;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.widget.SelfValidatedText;
import com.tradehero.th.widget.ServerValidatedEmailText;
import com.tradehero.th.widget.ValidatedPasswordText;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Notification;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.android.widget.OnTextChangeEvent;
import rx.android.widget.WidgetObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class EmailSignInFragment extends Fragment
{
    private View forgotDialogView;

    @Inject UserServiceWrapper userServiceWrapper;
    @Inject Analytics analytics;
    @Inject Lazy<DashboardNavigator> navigator;
    @Inject Provider<LoginSignUpFormDTO.Builder2> loginSignUpFormDTOProvider;
    @Inject SessionServiceWrapper sessionServiceWrapper;
    @Inject Provider<AuthDataAccountAction> authDataActionProvider;
    @Inject THAppsFlyer thAppsFlyer;

    @InjectView(R.id.authentication_sign_in_email) SelfValidatedText email;
    @InjectView(R.id.et_pwd_login) ValidatedPasswordText password;
    @InjectView(R.id.btn_login) View loginButton;
    Subscription validationSubscription;
    private Observable<Pair<AuthData, UserProfileDTO>> signInObservable;
    private Subscription signInSubscription;

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.authentication_back_button) void handleBackButtonClicked()
    {
        navigator.get().popFragment();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.authentication_sign_in_forgot_password) void showForgotPasswordUI()
    {
        forgotDialogView = LayoutInflater.from(getActivity()).inflate(R.layout.forgot_password_dialog, null);
        AlertDialogRxUtil.buildDefault(getActivity())
                .setTitle(R.string.authentication_ask_for_email)
                .setPositiveButton(R.string.ok)
                .setNegativeButton(R.string.authentication_cancel)
                .setView(forgotDialogView)
                .build()
                .subscribe(
                        new Action1<OnDialogClickEvent>()
                        {
                            @Override public void call(OnDialogClickEvent event)
                            {
                                if (event.isPositive())
                                {
                                    effectAskForgotEmail(forgotDialogView);
                                }
                            }
                        }
                );
    }

    protected void effectAskForgotEmail(@NonNull View forgotDialogView)
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
            String email1 = serverValidatedEmailText.getText().toString();
            doForgotPassword(email1);
        }
    }

    @Nullable protected Subscription forgotPasswordSubscription;

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
                WidgetObservable.text(email),
                WidgetObservable.text(password),
                new Func2<OnTextChangeEvent, OnTextChangeEvent, Pair<Boolean, Boolean>>()
                {
                    @Override public Pair<Boolean, Boolean> call(OnTextChangeEvent email1, OnTextChangeEvent password1)
                    {
                        email.forceValidate();
                        password.forceValidate();
                        return Pair.create(email.isValid(), password.isValid());
                    }
                })
                .subscribe(
                        new Action1<Pair<Boolean, Boolean>>()
                        {
                            @Override public void call(Pair<Boolean, Boolean> args)
                            {
                                loginButton.setEnabled(args.first && args.second);
                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable e)
                            {
                                Timber.e(e, "Error in validation");
                            }
                        });

        signInObservable = ViewObservable.clicks(loginButton, false)
                .flatMap(new Func1<OnClickEvent, Observable<? extends Pair<AuthData, UserProfileDTO>>>()
                {
                    @Override public Observable<? extends Pair<AuthData, UserProfileDTO>> call(OnClickEvent event)
                    {
                        return EmailSignInFragment.this.handleClick(event);
                    }
                })
                .retry();
    }

    @NonNull protected Observable<Pair<AuthData, UserProfileDTO>> handleClick(@NonNull OnClickEvent event)
    {
        DeviceUtil.dismissKeyboard(event.view());
        AuthData authData = new AuthData(email.getText().toString(), password.getText().toString());
        LoginSignUpFormDTO signUpFormDTO = loginSignUpFormDTOProvider.get()
                .authData(authData)
                .build();
        return signInProper(signUpFormDTO);
    }

    @NonNull protected Observable<Pair<AuthData, UserProfileDTO>> signInProper(LoginSignUpFormDTO loginSignUpFormDTO)
    {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), getString(R.string.alert_dialog_please_wait),
                getString(R.string.authentication_connecting_tradehero_only), true);
        AuthData authData = loginSignUpFormDTO.authData;
        Observable<UserProfileDTO> userLoginDTOObservable = sessionServiceWrapper.signupAndLoginRx(
                authData.getTHToken(), loginSignUpFormDTO)
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
                    @Override public Pair<AuthData, UserProfileDTO> call(AuthData t1, UserProfileDTO t2)
                    {
                        return Pair.create(t1, t2);
                    }
                })
                .subscribeOn(Schedulers.io())
                .doOnNext(new Action1<Pair<AuthData, UserProfileDTO>>()
                {
                    @Override public void call(Pair<AuthData, UserProfileDTO> pair)
                    {
                        thAppsFlyer.sendTrackingWithEvent(AppsFlyerConstants.REGISTRATION_EMAIL);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(authDataActionProvider.get())
                .doOnNext(new OpenDashboardAction(getActivity()))
                .doOnError(new ToastOnErrorAction())
                .doOnUnsubscribe(new DismissDialogAction0(progressDialog));
    }

    @Override public void onResume()
    {
        super.onResume();
        if (signInSubscription == null || signInSubscription.isUnsubscribed())
        {
            signInSubscription = signInObservable.subscribe(
                    new EmptyAction1<Pair<AuthData, UserProfileDTO>>(),
                    new EmptyAction1<Throwable>());
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
        unsubscribeForgotPassword();
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    protected void unsubscribeForgotPassword()
    {
        Subscription copy = forgotPasswordSubscription;
        if (copy != null)
        {
            copy.unsubscribe();
        }
        forgotPasswordSubscription = null;
    }

    private void doForgotPassword(String email)
    {
        ForgotPasswordFormDTO forgotPasswordFormDTO = new ForgotPasswordFormDTO();
        forgotPasswordFormDTO.userEmail = email;

        final ProgressDialog mProgressDialog = ProgressDialog.show(
                getActivity(),
                getString(R.string.alert_dialog_please_wait),
                getString(R.string.authentication_connecting_tradehero_only),
                true);

        unsubscribeForgotPassword();
        forgotPasswordSubscription = userServiceWrapper.forgotPasswordRx(forgotPasswordFormDTO)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnEach(new DismissDialogAction1<Notification<? super ForgotPasswordDTO>>(mProgressDialog))
                .subscribe(
                        new Action1<ForgotPasswordDTO>()
                        {
                            @Override public void call(ForgotPasswordDTO forgotPasswordDTO)
                            {
                                EmailSignInFragment.this.onReceivedForgotPassword(forgotPasswordDTO);
                            }
                        },
                        new ToastOnErrorAction());
    }

    public void onReceivedForgotPassword(@NonNull ForgotPasswordDTO args)
    {
        THToast.show(R.string.authentication_thank_you_message_email);
    }
}
