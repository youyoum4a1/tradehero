package com.androidth.general.fragments.authentication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.androidth.general.R;
import com.androidth.general.activities.ActivityHelper;
import com.androidth.general.activities.AuthenticationActivity;
import com.androidth.general.api.form.UserFormDTO;
import com.androidth.general.api.social.SocialNetworkEnum;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.auth.AuthData;
import com.androidth.general.auth.AuthDataUtil;
import com.androidth.general.common.fragment.ActivityResultDTO;
import com.androidth.general.fragments.DashboardNavigator;
import com.androidth.general.fragments.settings.ProfileInfoView;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.network.service.UserServiceWrapper;
import com.androidth.general.rx.EmptyAction1;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.rx.ToastOnErrorAction1;
import com.androidth.general.rx.view.DismissDialogAction0;
import com.androidth.general.utils.DeviceUtil;
import com.androidth.general.utils.metrics.appsflyer.AppsFlyerConstants;
import com.androidth.general.utils.metrics.appsflyer.THAppsFlyer;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import dagger.Lazy;
import rx.Observable;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.internal.util.SubscriptionList;

public class EmailSignUpFragment extends Fragment
{
    private static final String BUNDLE_KEY_DEEP_LINK = EmailSignUpFragment.class.getName() + ".deepLink";

    //TODO Change Analytics
    //@Inject Analytics analytics;
    @Inject Lazy<DashboardNavigator> navigator;
    @Inject UserServiceWrapper userServiceWrapper;

    @Bind(R.id.profile_info) ProfileInfoView profileView;
    @Bind(R.id.authentication_sign_up_email) EditText emailEditText;
    //@Nullable
    @Bind(R.id.btn_signUp) View signUpButton;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.signup_cardview1) CardView cardView1;
    @Bind(R.id.signup_cardview2)CardView cardView2;


    private SubscriptionList onStopSubscriptions;
    @Nullable private ActivityResultDTO receivedActivityResult;
    @Nullable Observer<SocialNetworkEnum> socialNetworkEnumObserver;
    @Nullable Uri deepLink;

    @SuppressWarnings("UnusedDeclaration")
    /*@OnClick(R.id.authentication_back_button) void handleBackButtonClicked()
    {
        navigator.get().popFragment();
    }*/

    public static void putDeepLink(@NonNull Bundle args, @NonNull Uri deepLink)
    {
        args.putString(BUNDLE_KEY_DEEP_LINK, deepLink.toString());
    }

    @Nullable private static Uri getDeepLink(@NonNull Bundle args)
    {
        String link = args.getString(BUNDLE_KEY_DEEP_LINK);
        return link == null ? null : Uri.parse(link);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        HierarchyInjector.inject(this);
        //TODO Change Analytics
        //analytics.tagScreen(AnalyticsConstants.Register_Form);
        //analytics.addEvent(new SimpleEvent(AnalyticsConstants.RegisterFormScreen));
        //analytics.addEvent(new MethodEvent(AnalyticsConstants.SignUp_Tap, AnalyticsConstants.Email));
        deepLink = getDeepLink(getArguments());
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
        ButterKnife.bind(this, view);
        cardView1.setCardElevation(5);
        cardView2.setCardElevation(5);

        signUpButton.setEnabled(true);
        toolbar.setTitle("Let's get started");
        toolbar.findViewById(R.id.arrow_back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigator.get().popFragment();
            }
        });
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        DeviceUtil.showKeyboardDelayed(emailEditText);

        ActivityResultDTO copy = receivedActivityResult;
        /*if (copy != null)
        {
            profileView.onActivityResult(
                    copy.activity,
                    copy.requestCode,
                    copy.resultCode,
                    copy.data);
            receivedActivityResult = null;
        }*/

    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (profileView != null)
        {
            profileView.onActivityResult(getActivity(), requestCode, resultCode, data);
        }
        else
        {
            receivedActivityResult = new ActivityResultDTO(getActivity(), requestCode, resultCode, data);
        }*/
    }

    @Override public void onStart()
    {
        super.onStart();
        onStopSubscriptions = new SubscriptionList();
//        onStopSubscriptions.add(AppObservable.bindSupportFragment(this, profileView.getFieldsValidObservable())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(
//                        areFieldsValid -> signUpButton.setEnabled(areFieldsValid),
//                        new TimberOnErrorAction1("Failed to listen to valid fields")));
        onStopSubscriptions.add(AppObservable.bindSupportFragment(this, getSignUpObservable())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new EmptyAction1<>(),
                        new TimberOnErrorAction1("Failed to listen to sign-up observable")));

    }

    @Override public void onStop()
    {
        onStopSubscriptions.unsubscribe();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onDetach()
    {
        //this.socialNetworkEnumObserver = null;
        super.onDetach();
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
                                .map(userProfileDTO -> Pair.create(authData, userProfileDTO))
                                .doOnUnsubscribe(new DismissDialogAction0(progressDialog));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1<Pair<AuthData, UserProfileDTO>>()
                {
                    @Override public void call(Pair<AuthData, UserProfileDTO> pair)
                    {
                        THAppsFlyer.sendTrackingWithEvent(getActivity(), AppsFlyerConstants.COMPLETE_REGISTRATION, null);
                        AuthDataUtil.saveAccountAndResult(getActivity(), pair.first, pair.second.email);
                        ActivityHelper.launchDashboardWithFinish(
                                getActivity(),
                                deepLink);
                    }
                })
                .doOnError(new ToastOnErrorAction1())
                .retry();
    }
}



