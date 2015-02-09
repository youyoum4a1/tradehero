package com.tradehero.th.fragments.authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.fragment.ActivityResultDTO;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.settings.ProfileInfoView;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.appsflyer.AppsFlyerConstants;
import com.tradehero.th.utils.metrics.appsflyer.THAppsFlyer;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.view.ViewObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Actions;

/**
 * Register using email.
 */
public class EmailSignUpFragment extends Fragment
{
    @Inject Analytics analytics;
    @Inject Lazy<DashboardNavigator> navigator;
    @Inject UserServiceWrapper userServiceWrapper;
    @Inject AuthDataAccountAction authDataAccountAction;
    @Inject THAppsFlyer thAppsFlyer;

    @InjectView(R.id.profile_info) ProfileInfoView profileView;
    @InjectView(R.id.authentication_sign_up_email) EditText emailEditText;
    @InjectView(R.id.authentication_sign_up_button) View signUpButton;

    private Observable<Pair<AuthData, UserProfileDTO>> signUpObservable;
    private Subscription subscription;
    private ProgressDialog progressDialog;
    @Nullable private ActivityResultDTO receivedActivityResult;

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.authentication_back_button)
    void handleBackButtonClicked()
    {
        navigator.get().popFragment();
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        HierarchyInjector.inject(this);
        analytics.tagScreen(AnalyticsConstants.Register_Form);
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.RegisterFormScreen));
        analytics.addEvent(new MethodEvent(AnalyticsConstants.SignUp_Tap, AnalyticsConstants.Email));
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.authentication_email_sign_up, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        DeviceUtil.showKeyboardDelayed(emailEditText);

        signUpObservable = ViewObservable.clicks(signUpButton, false)
                .flatMap(view1 -> profileView.obtainUserFormDTO())
                .doOnNext(userFormDTO -> progressDialog = ProgressDialog.show(getActivity(), getString(R.string.alert_dialog_please_wait),
                        getString(R.string.authentication_connecting_tradehero_only), true))
                .flatMap(userFormDTO -> {
                    final AuthData authData = new AuthData(userFormDTO.email, userFormDTO.password);
                    final Observable<UserProfileDTO> profileDTOObservable = userServiceWrapper.signUpWithEmailRx(authData.getTHToken(), userFormDTO);
                    return Observable.zip(Observable.just(authData), profileDTOObservable, Pair::create);
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnNext(pair -> thAppsFlyer.sendTrackingWithEvent(AppsFlyerConstants.REGISTRATION_EMAIL))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(authDataAccountAction)
                .doOnNext(new OpenDashboardAction(getActivity()))
                .doOnError(new ToastOnErrorAction())
                .doOnUnsubscribe(() -> {
                    if (progressDialog != null)
                    {
                        progressDialog.dismiss();
                    }
                })
                .retry()
        ;

        ActivityResultDTO copy = receivedActivityResult;
        if (copy != null)
        {
            profileView.onActivityResult(copy.requestCode,
                    copy.resultCode,
                    copy.data);
            receivedActivityResult = null;
        }
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (profileView != null)
        {
            profileView.onActivityResult(requestCode, resultCode, data);
        }
        else
        {
            receivedActivityResult = new ActivityResultDTO(requestCode, resultCode, data);
        }
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onResume()
    {
        super.onResume();
        if (subscription == null || subscription.isUnsubscribed())
        {
            subscription = signUpObservable.subscribe(Actions.empty(), Actions.empty());
        }
    }

    @Override public void onPause()
    {
        if (subscription != null && !subscription.isUnsubscribed())
        {
            subscription.unsubscribe();
        }

        super.onPause();
    }
}



