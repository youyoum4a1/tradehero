package com.tradehero.th.fragments.settings;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.preference.PreferenceFragment;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.AuthenticationProvider;
import com.tradehero.th.auth.SocialAuth;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.SessionServiceWrapper;
import com.tradehero.th.persistence.prefs.AuthHeader;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.utils.AlertDialogRxUtil;
import com.tradehero.th.utils.Constants;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Actions;
import timber.log.Timber;

public class SignOutSettingViewHolder extends OneSettingViewHolder
{
    @NonNull private final SessionServiceWrapper sessionServiceWrapper;
    @NonNull private final String authHeader;
    @NonNull private final Map<SocialNetworkEnum, AuthenticationProvider> authenticationProviderMap;
    @NonNull private final AccountManager accountManager;

    //<editor-fold desc="Constructors">
    @Inject public SignOutSettingViewHolder(
            @NonNull SessionServiceWrapper sessionServiceWrapper,
            @NonNull @AuthHeader String authHeader,
            @NonNull @SocialAuth Map<SocialNetworkEnum, AuthenticationProvider> authenticationProviderMap,
            @NonNull AccountManager accountManager)
    {
        this.sessionServiceWrapper = sessionServiceWrapper;
        this.authHeader = authHeader;
        this.authenticationProviderMap = authenticationProviderMap;
        this.accountManager = accountManager;
    }
    //</editor-fold>

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
                AlertDialogRxUtil.buildDefault(activityContext)
                        .setTitle(R.string.settings_misc_sign_out_are_you_sure)
                        .setCancelable(true)
                        .setNegativeButton(R.string.settings_misc_sign_out_no)
                        .setPositiveButton(R.string.settings_misc_sign_out_yes)
                        .build()
                        .subscribe(
                                new Action1<OnDialogClickEvent>()
                                {
                                    @Override public void call(OnDialogClickEvent event)
                                    {
                                        if (event.isPositive())
                                        {
                                            effectSignOut();
                                        }
                                    }
                                },
                                Actions.empty());
            }
        }
    }

    protected void effectSignOut()
    {
        ProgressDialog progressDialog = null;
        PreferenceFragment preferenceFragmentCopy = preferenceFragment;
        if (preferenceFragmentCopy != null)
        {
            Activity activityContext = preferenceFragmentCopy.getActivity();
            if (activityContext != null)
            {
                progressDialog = ProgressDialog.show(
                        activityContext,
                        activityContext.getString(R.string.settings_misc_sign_out_alert_title),
                        activityContext.getString(R.string.settings_misc_sign_out_alert_message),
                        true);
                progressDialog.setCancelable(true);
                progressDialog.setCanceledOnTouchOutside(true);
            }
        }

        final ProgressDialog finalProgressDialog = progressDialog;
        subscriptions.add(sessionServiceWrapper.logoutRx()
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo(new Action0()
                {
                    @Override public void call()
                    {
                        if (finalProgressDialog != null)
                        {
                            finalProgressDialog.dismiss();
                        }
                    }
                })
                .subscribe(
                        profile -> onSignedOut(profile, finalProgressDialog),
                        e -> onSignOutError(e, finalProgressDialog)));
    }

    protected void onSignedOut(@SuppressWarnings("UnusedParameters") UserProfileDTO userProfileDTO, @Nullable ProgressDialog progressDialog)
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

        if (progressDialog != null)
        {
            progressDialog.dismiss();
        }
    }

    protected void onSignOutError(Throwable e, @Nullable ProgressDialog progressDialog)
    {
        Timber.e(e, "Failed to sign out");
        if (progressDialog != null)
        {
            progressDialog.setTitle(R.string.settings_misc_sign_out_failed);
            progressDialog.setMessage("");
            Observable.just(0)
                    .delay(3000, TimeUnit.MILLISECONDS)
                    .doOnCompleted(progressDialog::dismiss)
                    .subscribe(Actions.empty(), Actions.empty());
        }
        else
        {
            THToast.show(new THException(e));
        }
    }
}
