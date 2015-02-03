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
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.utils.THToast;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.payment.UpdateAlipayAccountDTO;
import com.tradehero.th.api.users.payment.UpdateAlipayAccountFormDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import com.tradehero.th.widget.ServerValidatedEmailText;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.app.AppObservable;
import timber.log.Timber;

public class SettingsAlipayFragment extends DashboardFragment
{
    @InjectView(R.id.settings_alipay_email_text) protected ServerValidatedEmailText alipayAccountText;
    @InjectView(R.id.settings_alipay_id_text) protected ServerValidatedEmailText alipayAccountIDText;
    @InjectView(R.id.settings_alipay_realname_text) protected ServerValidatedEmailText alipayAccountRealNameText;
    private ProgressDialog progressDialog;

    @Nullable private Subscription userProfileCacheSubscription;
    @Nullable private Subscription updateAlipayAccountSubscription;

    @Inject UserServiceWrapper userServiceWrapper;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject CurrentUserId currentUserId;
    @Inject Analytics analytics;
    @Inject ProgressDialogUtil progressDialogUtil;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_settings_alipay, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        // HACK: force this email to focus instead of the TabHost stealing focus..
        alipayAccountText.setOnTouchListener(new FocusableOnTouchListener());
        alipayAccountIDText.setOnTouchListener(new FocusableOnTouchListener());
        alipayAccountRealNameText.setOnTouchListener(new FocusableOnTouchListener());
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchUserProfile();
    }

    @Override public void onResume()
    {
        super.onResume();

        analytics.addEvent(new SimpleEvent(AnalyticsConstants.Settings_Alipay));
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarTitle(R.string.settings_alipay_header);
        super.onCreateOptionsMenu(menu, inflater);
    }
    //</editor-fold>

    @Override public void onStop()
    {
        super.onStop();
        unsubscribe(userProfileCacheSubscription);
        userProfileCacheSubscription = null;
        unsubscribe(updateAlipayAccountSubscription);
        updateAlipayAccountSubscription = null;
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    protected void fetchUserProfile()
    {
        unsubscribe(userProfileCacheSubscription);
        userProfileCacheSubscription = AppObservable.bindFragment(
                this,
                userProfileCache.get(currentUserId.toUserBaseKey())
                        .map(new PairGetSecond<>()))
                .subscribe(
                        this::onUserProfileReceived,
                        this::onUserProfileError);
    }

    protected void onUserProfileReceived(@NonNull UserProfileDTO profileDTO)
    {
        alipayAccountText.setText(profileDTO.alipayAccount);
        alipayAccountIDText.setText(profileDTO.alipayIdentityNumber);
        alipayAccountRealNameText.setText(profileDTO.alipayRealName);
    }

    @SuppressWarnings("UnusedParameters")
    protected void onUserProfileError(@NonNull Throwable e)
    {
        THToast.show(getString(R.string.error_fetch_your_user_profile));
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.settings_alipay_update_button)
    public void onSubmitClicked(@SuppressWarnings("UnusedParameters") View view)
    {
        progressDialog = progressDialogUtil.show(
                getActivity(),
                R.string.alert_dialog_please_wait,
                R.string.authentication_connecting_tradehero_only);
        UpdateAlipayAccountFormDTO accountDTO = new UpdateAlipayAccountFormDTO();
        accountDTO.newAlipayAccount = alipayAccountText.getText().toString();
        accountDTO.userIdentityNumber = alipayAccountIDText.getText().toString();
        accountDTO.userRealName = alipayAccountRealNameText.getText().toString();
        unsubscribe(updateAlipayAccountSubscription);
        updateAlipayAccountSubscription = AppObservable.bindFragment(
                this,
                userServiceWrapper.updateAlipayAccountRx(
                        currentUserId.toUserBaseKey(), accountDTO))
                .subscribe(
                        this::onAlipayUpdateReceived,
                        this::onAlipayUpdateError);
    }

    protected void onAlipayUpdateReceived(@NonNull UpdateAlipayAccountDTO args)
    {
        if (!isDetached())
        {
            THToast.show(getString(R.string.settings_alipay_successful_update));
            progressDialog.hide();
            navigator.get().popFragment();
        }
    }

    protected void onAlipayUpdateError(@NonNull Throwable e)
    {
        Timber.e(e, "Failed to update the Alipay account");
        if (!isDetached())
        {
            THToast.show(new THException(e));
            progressDialog.hide();
        }
    }
}