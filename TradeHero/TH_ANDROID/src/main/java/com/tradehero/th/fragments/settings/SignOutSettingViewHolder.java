package com.tradehero.th.fragments.settings;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.preference.PreferenceFragment;
import com.tradehero.th.R;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.auth.AuthenticationProvider;
import com.tradehero.th.auth.SocialAuth;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.SessionServiceWrapper;
import com.tradehero.th.persistence.prefs.AuthHeader;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.ProgressDialogUtil;
import java.util.Map;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SignOutSettingViewHolder extends OneSettingViewHolder
{
    @NotNull private final ProgressDialogUtil progressDialogUtil;
    @NotNull private final SessionServiceWrapper sessionServiceWrapper;
    @NotNull private final String authHeader;
    @NotNull private final Map<SocialNetworkEnum, AuthenticationProvider> authenticationProviderMap;
    @NotNull private final AccountManager accountManager;
    @Nullable private ProgressDialog progressDialog;
    @Nullable private MiddleCallback<UserProfileDTO> logoutCallback;

    //<editor-fold desc="Constructors">
    @Inject public SignOutSettingViewHolder(
            @NotNull ProgressDialogUtil progressDialogUtil,
            @NotNull SessionServiceWrapper sessionServiceWrapper,
            @NotNull @AuthHeader String authHeader,
            @NotNull @SocialAuth Map<SocialNetworkEnum, AuthenticationProvider> authenticationProviderMap,
            @NotNull AccountManager accountManager)
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
                                new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        dialog.cancel();
                                    }
                                })
                        .setPositiveButton(R.string.settings_misc_sign_out_yes,
                                new DialogInterface.OnClickListener()
                                {
                                    @Override public void onClick(DialogInterface dialogInterface, int i)
                                    {
                                        effectSignOut();
                                    }
                                });

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

        detachLogoutCallback();
        logoutCallback = sessionServiceWrapper.logout(createSignOutCallback(preferenceFragment.getActivity()));
    }

    protected void detachLogoutCallback()
    {
        MiddleCallback<UserProfileDTO> logoutCallbackCopy = logoutCallback;
        if (logoutCallbackCopy != null)
        {
            logoutCallbackCopy.setPrimaryCallback(null);
        }
        logoutCallback = null;
    }

    private Callback<UserProfileDTO> createSignOutCallback(final Activity activity)
    {
        return new SignOutCallback(activity);
    }

    protected class SignOutCallback implements Callback<UserProfileDTO>
    {
        @NotNull private final Activity activity;

        //<editor-fold desc="Constructors">
        public SignOutCallback(@NotNull Activity activity)
        {
            this.activity = activity;
        }
        //</editor-fold>

        @Override
        public void success(UserProfileDTO o, Response response)
        {
            for (Map.Entry<SocialNetworkEnum, AuthenticationProvider> entry: authenticationProviderMap.entrySet())
            {
                if (authHeader.startsWith(entry.getKey().getAuthHeader()))
                {
                    entry.getValue().logout();
                }
            }

            Account[] accounts = accountManager.getAccountsByType(Constants.Auth.PARAM_ACCOUNT_TYPE);
            if (accounts != null)
            {
                for (Account account: accounts)
                {
                    accountManager.removeAccount(account, null, null);
                }
            }

            dismissProgressDialog();
        }

        @Override public void failure(RetrofitError error)
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
                preferenceFragmentCopy.getView().postDelayed(new Runnable()
                {
                    @Override public void run()
                    {
                        dismissProgressDialog();
                    }
                }, 3000);
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
