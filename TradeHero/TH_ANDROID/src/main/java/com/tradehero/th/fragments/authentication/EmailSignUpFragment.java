package com.tradehero.th.fragments.authentication;

import android.app.Activity;
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
import butterknife.Optional;
import com.tradehero.common.fragment.ActivityResultDTO;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.activities.AuthenticationActivity;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.auth.AuthDataUtil;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.settings.ProfileInfoView;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.TimberOnErrorAction;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.rx.view.DismissDialogAction0;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.appsflyer.AppsFlyerConstants;
import com.tradehero.th.utils.metrics.appsflyer.THAppsFlyer;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.internal.util.SubscriptionList;
import timber.log.Timber;

/**
 * Register using email.
 */
public class EmailSignUpFragment extends Fragment
{
    @Inject Analytics analytics;
    @Inject Lazy<DashboardNavigator> navigator;
    @Inject UserServiceWrapper userServiceWrapper;
    @Inject THAppsFlyer thAppsFlyer;

    @InjectView(R.id.profile_info) ProfileInfoView profileView;
    @InjectView(R.id.authentication_sign_up_email) EditText emailEditText;
    @InjectView(R.id.authentication_sign_up_button) View signUpButton;
    @InjectView(R.id.social_network_button_list) View socialButtonList;

    private SubscriptionList onStopSubscriptions;
    @Nullable private ActivityResultDTO receivedActivityResult;
    @Nullable Observer<SocialNetworkEnum> socialNetworkEnumObserver;

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.authentication_back_button) void handleBackButtonClicked()
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

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        this.socialNetworkEnumObserver = ((AuthenticationActivity) activity).getSelectedSocialNetworkObserver();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.authentication_email_sign_up, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        signUpButton.setEnabled(false);

        DeviceUtil.showKeyboardDelayed(emailEditText);

        ActivityResultDTO copy = receivedActivityResult;
        if (copy != null)
        {
            profileView.onActivityResult(copy.requestCode,
                    copy.resultCode,
                    copy.data);
            receivedActivityResult = null;
        }

        try
        {
            view.setBackgroundResource(R.drawable.login_bg_4);
        } catch (Throwable e)
        {
            Timber.e(e, "Failed to set guide background");
            view.setBackgroundColor(getResources().getColor(R.color.authentication_guide_bg_color));
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

    @Override public void onStart()
    {
        super.onStart();
        onStopSubscriptions = new SubscriptionList();
        onStopSubscriptions.add(AppObservable.bindFragment(this, profileView.getFieldsValidObservable())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Boolean>()
                        {
                            @Override public void call(Boolean areFieldsValid)
                            {
                                signUpButton.setEnabled(areFieldsValid);
                            }
                        },
                        new TimberOnErrorAction("Failed to listen to valid fields")));
        onStopSubscriptions.add(AppObservable.bindFragment(this, getSignUpObservable())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new EmptyAction1<Pair<AuthData, UserProfileDTO>>(),
                        new TimberOnErrorAction("Failed to listen to sign-up observable")));
    }

    @Override public void onStop()
    {
        onStopSubscriptions.unsubscribe();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDetach()
    {
        this.socialNetworkEnumObserver = null;
        super.onDetach();
    }

    @SuppressWarnings({"unused"}) @OnClick({
            R.id.btn_linkedin_signin,
            R.id.btn_facebook_signin,
            R.id.btn_twitter_signin,
            R.id.btn_qq_signin,
            R.id.btn_weibo_signin,
    }) @Optional
    protected void onSignInButtonClicked(View view)
    {
        if (socialNetworkEnumObserver != null)
        {
            socialNetworkEnumObserver.onNext(((AuthenticationImageButton) view).getType());
        }
    }

    protected Observable<Pair<AuthData, UserProfileDTO>> getSignUpObservable()
    {
        return ViewObservable.clicks(signUpButton, false)
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<OnClickEvent, Observable<? extends UserFormDTO>>()
                {
                    @Override public Observable<? extends UserFormDTO> call(OnClickEvent view1)
                    {
                        return profileView.obtainUserFormDTO();
                    }
                })
                .flatMap(new Func1<UserFormDTO, Observable<? extends Pair<AuthData, UserProfileDTO>>>()
                {
                    @Override public Observable<? extends Pair<AuthData, UserProfileDTO>> call(UserFormDTO userFormDTO)
                    {
                        ProgressDialog progressDialog = ProgressDialog.show(EmailSignUpFragment.this.getActivity(),
                                EmailSignUpFragment.this.getString(R.string.alert_dialog_please_wait),
                                EmailSignUpFragment.this.getString(R.string.authentication_connecting_tradehero_only), true);

                        final AuthData authData = new AuthData(userFormDTO.email, userFormDTO.password);
                        return userServiceWrapper.signUpWithEmailRx(authData, userFormDTO)
                                .map(new Func1<UserProfileDTO, Pair<AuthData, UserProfileDTO>>()
                                {
                                    @Override public Pair<AuthData, UserProfileDTO> call(UserProfileDTO userProfileDTO)
                                    {
                                        return Pair.create(authData, userProfileDTO);
                                    }
                                })
                                .doOnUnsubscribe(new DismissDialogAction0(progressDialog));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Pair<AuthData, UserProfileDTO>>()
                {
                    @Override public void call(Pair<AuthData, UserProfileDTO> pair)
                    {
                        thAppsFlyer.sendTrackingWithEvent(AppsFlyerConstants.REGISTRATION_EMAIL);
                        AuthDataUtil.saveAccountAndResult(getActivity(), pair.first, pair.second.email);
                        ActivityHelper.launchDashboard(getActivity());
                    }
                })
                .doOnError(new ToastOnErrorAction())
                .retry();
    }
}



