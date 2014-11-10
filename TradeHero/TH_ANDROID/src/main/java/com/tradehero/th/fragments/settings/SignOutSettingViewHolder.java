package com.tradehero.th.fragments.settings;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.preference.PreferenceFragment;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.AuthenticationProvider;
import com.tradehero.th.auth.SocialAuth;
import com.tradehero.th.network.service.SessionServiceWrapper;
import com.tradehero.th.persistence.prefs.AuthHeader;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.ProgressDialogUtil;
import java.util.Map;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.EmptyObserver;

public class SignOutSettingViewHolder extends OneSettingViewHolder
{
    @NonNull private final ProgressDialogUtil progressDialogUtil;
    @NonNull private final SessionServiceWrapper sessionServiceWrapper;
    @NonNull private final String authHeader;
    @NonNull private final Map<SocialNetworkEnum, AuthenticationProvider> authenticationProviderMap;
    @NonNull private final AccountManager accountManager;
    @Nullable private ProgressDialog progressDialog;
    @Nullable private Subscription logoutSubscription;

    //<editor-fold desc="Constructors">
    @Inject public SignOutSettingViewHolder(
            @NonNull ProgressDialogUtil progressDialogUtil,
            @NonNull SessionServiceWrapper sessionServiceWrapper,
            @NonNull @AuthHeader String authHeader,
            @NonNull @SocialAuth Map<SocialNetworkEnum, AuthenticationProvider> authenticationProviderMap,
            @NonNull AccountManager accountManager)
    {
        this.progressDialogUtil = progressDialogUtil;
        this.sessionServiceWrapper = sessionServiceWrapper;
        this.authHeader = authHeader;
        this.authenticationProviderMap = authenticationProviderMap;
        this.accountManager = accountManager;
    }
    //</editor-fold>

    @Override public void destroyViews()
    {
        dismissProgressDialog();
        super.destroyViews();
    }

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_misc_sign_out;
    }

    @Override protected void handlePrefClicked()
    {
        PreferenceFragment preferenceFragmentCopy = preferenceFragment;
        if (preferenceFragmentCopy != null)
        {
            Context activityContext = preferenceFragmentCopy.getActivity();
            if (activityContext != null)
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activityContext);
                alertDialogBuilder
                        .setTitle(R.string.settings_misc_sign_out_are_you_sure)
                        .setCancelable(true)
                        .setNegativeButton(R.string.settings_misc_sign_out_no,
                                (dialog, id) -> dialog.cancel())
                        .setPositiveButton(R.string.settings_misc_sign_out_yes,
                                (dialogInterface, i) -> effectSignOut());

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }
    }

    protected void effectSignOut()
    {
        PreferenceFragment preferenceFragmentCopy = preferenceFragment;
        Activity activityContext = null;
        if (preferenceFragmentCopy != null)
        {
            activityContext = preferenceFragmentCopy.getActivity();
        }
        if (progressDialog == null)
        {
            if (activityContext != null)
            {
                progressDialog = progressDialogUtil.show(
                        activityContext,
                        R.string.settings_misc_sign_out_alert_title,
                        R.string.settings_misc_sign_out_alert_message);
            }
        }
        else
        {
            progressDialog.show();
        }
        if (progressDialog != null)
        {
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(true);
        }

        unsubscribe(logoutSubscription);
        logoutSubscription = sessionServiceWrapper.logoutRx()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createSignOutObserver());
    }

    private Observer<UserProfileDTO> createSignOutObserver()
    {
        return new SignOutObserver();
    }

    protected class SignOutObserver extends EmptyObserver<UserProfileDTO>
    {
        @Override public void onNext(UserProfileDTO userProfileDTO)
        {
            for (Map.Entry<SocialNetworkEnum, AuthenticationProvider> entry : authenticationProviderMap.entrySet())
            {
                if (authHeader.startsWith(entry.getKey().getAuthHeader()))
                {
                    entry.getValue().logout();
                }
            }

            Account[] accounts = accountManager.getAccountsByType(Constants.Auth.PARAM_ACCOUNT_TYPE);
            if (accounts != null)
            {
                for (Account account : accounts)
                {
                    accountManager.removeAccount(account, null, null);
                }
            }

            dismissProgressDialog();
        }

        @Override public void onError(Throwable e)
        {
            ProgressDialog progressDialogCopy = progressDialog;
            if (progressDialogCopy != null)
            {
                progressDialog.setTitle(R.string.settings_misc_sign_out_failed);
                progressDialog.setMessage("");
            }
            PreferenceFragment preferenceFragmentCopy = preferenceFragment;
            if (preferenceFragmentCopy != null)
            {
                preferenceFragmentCopy.getView().postDelayed(SignOutSettingViewHolder.this::dismissProgressDialog, 3000);
            }
        }
    }

    private void dismissProgressDialog()
    {
        ProgressDialog progressDialogCopy = progressDialog;
        if (progressDialogCopy != null)
        {
            progressDialogCopy.dismiss();
        }
        progressDialog = null;
    }
}
