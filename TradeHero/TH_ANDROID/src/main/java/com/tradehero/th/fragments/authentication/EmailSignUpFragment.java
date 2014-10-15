package com.tradehero.th.fragments.authentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.AuthData;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.settings.ProfileInfoView;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.observables.ViewObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observers.EmptyObserver;

/**
 * Register using email.
 */
public class EmailSignUpFragment extends Fragment
{
    @Inject Analytics analytics;
    @Inject DashboardNavigator navigator;
    @Inject UserServiceWrapper userServiceWrapper;
    @Inject AuthDataAction authDataAction;
    @Inject ToastOnErrorAction toastOnErrorAction;

    @InjectView(R.id.profile_info) ProfileInfoView profileView;
    @InjectView(R.id.authentication_sign_up_email) EditText emailEditText;
    @InjectView(R.id.authentication_sign_up_button) View signUpButton;

    private Observable<Pair<AuthData, UserProfileDTO>> signUpObservable;
    private Subscription subscription;
    private ProgressDialog progressDialog;

    @OnClick(R.id.authentication_back_button) void handleBackButtonClicked()
    {
        navigator.popFragment();
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
                .flatMap(new Func1<View, Observable<UserFormDTO>>()
                {
                    @Override public Observable<UserFormDTO> call(View view)
                    {
                        return profileView.obtainUserFormDTO();
                    }
                })
                .doOnNext(new Action1<UserFormDTO>()
                {
                    @Override public void call(UserFormDTO userFormDTO)
                    {
                        progressDialog = ProgressDialog.show(getActivity(), getString(R.string.alert_dialog_please_wait),
                                getString(R.string.authentication_connecting_tradehero_only), true);
                    }
                })
                .flatMap(new Func1<UserFormDTO, Observable<Pair<AuthData, UserProfileDTO>>>()
                {
                    @Override public Observable<Pair<AuthData, UserProfileDTO>> call(UserFormDTO userFormDTO)
                    {
                        final AuthData authData = new AuthData(userFormDTO.email, userFormDTO.password);
                        final Observable<UserProfileDTO> userProfileDTOObservable = userServiceWrapper.signUpWithEmailRx(authData.getTHToken(),
                                userFormDTO);
                        return Observable.zip(Observable.just(authData), userProfileDTOObservable, new Func2<AuthData, UserProfileDTO,
                                Pair<AuthData, UserProfileDTO>>()
                        {
                            @Override public Pair<AuthData, UserProfileDTO> call(AuthData authData, UserProfileDTO userProfileDTO)
                            {
                                return Pair.create(authData, userProfileDTO);
                            }
                        });
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(authDataAction)
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
                .retry()
        ;
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        profileView.onActivityResult(requestCode, resultCode, data);
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
            subscription = signUpObservable.subscribe(new EmptyObserver<Pair<AuthData, UserProfileDTO>>());
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



