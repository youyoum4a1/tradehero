package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.payment.UpdatePayPalEmailDTO;
import com.tradehero.th.api.users.payment.UpdatePayPalEmailFormDTO;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.ToastOnErrorAction1;
import com.tradehero.th.rx.view.DismissDialogAction0;
import com.tradehero.th.widget.validation.ValidatedText;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import timber.log.Timber;

public class SettingsPayPalFragment extends BaseFragment
{
    @Bind(R.id.settings_paypal_email_text) protected ValidatedText paypalEmailText;
    @Bind(R.id.settings_paypal_update_button) protected Button submitButton;

    @Inject UserServiceWrapper userServiceWrapper;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject CurrentUserId currentUserId;
    //TODO Change Analytics
    //@Inject Analytics analytics;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_settings_paypal, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        // HACK: force this email to focus instead of the TabHost stealing focus..
        paypalEmailText.setOnTouchListener(new FocusableOnTouchListener());
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchUserProfile();
    }

    @Override public void onResume()
    {
        super.onResume();
        //TODO Change Analytics
        //analytics.addEvent(new SimpleEvent(AnalyticsConstants.Settings_PayPal));
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarTitle(R.string.settings_paypal_header);
        super.onCreateOptionsMenu(menu, inflater);
    }
    //</editor-fold>

    @Override public void onDestroyView()
    {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    protected void fetchUserProfile()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                userProfileCache.get(currentUserId.toUserBaseKey())
                        .map(new PairGetSecond<UserBaseKey, UserProfileDTO>()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<UserProfileDTO>()
                        {
                            @Override public void call(UserProfileDTO profile)
                            {
                                SettingsPayPalFragment.this.onUserProfileReceived(profile);
                            }
                        },
                        new Action1<Throwable>()
                        {
                            @Override public void call(Throwable error)
                            {
                                SettingsPayPalFragment.this.onUserProfileError(error);
                            }
                        }));
    }

    protected void onUserProfileReceived(@NonNull UserProfileDTO profile)
    {
        paypalEmailText.setText(profile.paypalEmailAddress);
    }

    protected void onUserProfileError(@NonNull Throwable e)
    {
        if (!isDetached())
        {
            THToast.show(getString(R.string.error_fetch_your_user_profile));
            Timber.e("Error fetching the user profile", e);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.settings_paypal_update_button)
    public void onSubmitClicked(@SuppressWarnings("UnusedParameters") View view)
    {
        final ProgressDialog progressDialog = ProgressDialog.show(
                getActivity(),
                getString(R.string.alert_dialog_please_wait),
                getString(R.string.authentication_connecting_tradehero_only),
                true);
        UpdatePayPalEmailFormDTO emailDTO = new UpdatePayPalEmailFormDTO();
        emailDTO.newPayPalEmailAddress = paypalEmailText.getText().toString();
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                userServiceWrapper.updatePayPalEmailRx(currentUserId.toUserBaseKey(), emailDTO))
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo(new DismissDialogAction0(progressDialog))
                .doOnUnsubscribe(new DismissDialogAction0(progressDialog))
                .subscribe(
                        new Action1<UpdatePayPalEmailDTO>()
                        {
                            @Override public void call(UpdatePayPalEmailDTO emailDto)
                            {
                                SettingsPayPalFragment.this.onPayPalUpdated(emailDto);
                            }
                        },
                        new ToastOnErrorAction1()));
    }

    protected void onPayPalUpdated(@SuppressWarnings("UnusedParameters") @NonNull UpdatePayPalEmailDTO args)
    {
        if (!isDetached())
        {
            THToast.show(getString(R.string.settings_paypal_successful_update));
            navigator.get().popFragment();
        }
    }
}