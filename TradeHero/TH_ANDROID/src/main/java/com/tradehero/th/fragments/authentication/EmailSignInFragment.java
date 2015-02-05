package com.tradehero.th.fragments.authentication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.password.ForgotPasswordDTO;
import com.tradehero.th.api.users.password.ForgotPasswordFormDTO;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.SessionServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.utils.AlertDialogRxUtil;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.ProgressDialogUtil;
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
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.ViewObservable;
import rx.android.widget.WidgetObservable;
import rx.functions.Action1;
import rx.functions.Actions;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class EmailSignInFragment extends Fragment
{
    private ProgressDialog mProgressDialog;
    private View forgotDialogView;

    @Inject UserServiceWrapper userServiceWrapper;
    @Inject Analytics analytics;
    @Inject Lazy<DashboardNavigator> navigator;
    @Inject Provider<LoginSignUpFormDTO.Builder2> loginSignUpFormDTOProvider;
    @Inject SessionServiceWrapper sessionServiceWrapper;
    @Inject ToastOnErrorAction toastOnErrorAction;
    @Inject Provider<AuthDataAccountAction> authDataActionProvider;
    @Inject THAppsFlyer thAppsFlyer;

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
                        new Action1<Pair<DialogInterface, Integer>>()
                        {
                            @Override public void call(Pair<DialogInterface, Integer> pair)
                            {
                                if (pair.second.equals(DialogInterface.BUTTON_POSITIVE))
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
                (email1, password1) -> {
                    email.forceValidate();
                    password.forceValidate();
                    return Pair.create(email.isValid(), password.isValid());
                })
                .subscribe(
                        args -> loginButton.setEnabled(args.first && args.second),
                        e -> Timber.e(e, "Error in validation"));

        signInObservable = ViewObservable.clicks(loginButton, false)
                .map(view1 -> {
                    DeviceUtil.dismissKeyboard(view1.view());
                    return new AuthData(email.getText().toString(), password.getText().toString());
                })
                .doOnNext(AuthData -> progressDialog = ProgressDialog.show(getActivity(), getString(R.string.alert_dialog_please_wait),
                        getString(R.string.authentication_connecting_tradehero_only), true))
                .map(authData -> loginSignUpFormDTOProvider.get()
                        .authData(authData)
                        .build())
                .flatMap(loginSignUpFormDTO -> {
                    AuthData authData = loginSignUpFormDTO.authData;
                    Observable<UserProfileDTO> userLoginDTOObservable = sessionServiceWrapper.signupAndLoginRx(
                            authData.getTHToken(), loginSignUpFormDTO)
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .map(userLoginDTO -> userLoginDTO.profileDTO);

                    return Observable.zip(Observable.just(authData), userLoginDTOObservable, Pair::create)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread());
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnNext(pair -> thAppsFlyer.sendTrackingWithEvent(AppsFlyerConstants.REGISTRATION_EMAIL))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(authDataActionProvider.get())
                .doOnNext(new OpenDashboardAction(getActivity()))
                .doOnError(toastOnErrorAction)
                .doOnUnsubscribe(() -> {
                    if (progressDialog != null)
                    {
                        progressDialog.dismiss();
                    }
                })
                .retry();
    }

    @Override public void onResume()
    {
        super.onResume();
        if (signInSubscription == null || signInSubscription.isUnsubscribed())
        {
            signInSubscription = signInObservable.subscribe(
                    Actions.empty(),
                    Actions.empty());
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

        mProgressDialog = ProgressDialogUtil.show(
                getActivity(),
                R.string.alert_dialog_please_wait,
                R.string.authentication_connecting_tradehero_only);

        unsubscribeForgotPassword();
        forgotPasswordSubscription = userServiceWrapper.forgotPasswordRx(forgotPasswordFormDTO)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::onReceivedForgotPassword,
                        this::onForgotPasswordFailed);
    }

    public void onReceivedForgotPassword(@NonNull ForgotPasswordDTO args)
    {
        mProgressDialog.dismiss();
        THToast.show(R.string.authentication_thank_you_message_email);
    }

    public void onForgotPasswordFailed(Throwable e)
    {
        mProgressDialog.dismiss();
        THToast.show(new THException(e));
    }
}
