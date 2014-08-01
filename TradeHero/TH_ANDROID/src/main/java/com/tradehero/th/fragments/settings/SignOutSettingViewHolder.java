package com.tradehero.th.fragments.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import com.tradehero.th.R;
import com.tradehero.th.activities.ActivityHelper;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.THUser;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.SessionServiceWrapper;
import com.tradehero.th.utils.ProgressDialogUtil;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class SignOutSettingViewHolder extends OneSettingViewHolder
{
    @NotNull private final ProgressDialogUtil progressDialogUtil;
    @NotNull private final CurrentUserId currentUserId;
    @NotNull private final SessionServiceWrapper sessionServiceWrapper;
    private ProgressDialog progressDialog;
    private MiddleCallback<UserProfileDTO> logoutCallback;

    //<editor-fold desc="Constructors">
    @Inject public SignOutSettingViewHolder(
            @NotNull ProgressDialogUtil progressDialogUtil,
            @NotNull CurrentUserId currentUserId,
            @NotNull SessionServiceWrapper sessionServiceWrapper)
    {
        this.progressDialogUtil = progressDialogUtil;
        this.currentUserId = currentUserId;
        this.sessionServiceWrapper = sessionServiceWrapper;
    }
    //</editor-fold>

    @Override protected int getStringKeyResId()
    {
        return R.string.key_settings_misc_sign_out;
    }

    @Override protected void handlePrefClicked()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(preferenceFragment.getActivity());
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

    protected void effectSignOut()
    {
        if (progressDialog == null)
        {
            progressDialog = progressDialogUtil.show(preferenceFragment.getActivity(),
                    R.string.settings_misc_sign_out_alert_title,
                    R.string.settings_misc_sign_out_alert_message);
        }
        else
        {
            progressDialog.show();
        }
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(true);

        Timber.d("Before signout current user base key %s", currentUserId.toUserBaseKey());
        detachLogoutCallback();
        logoutCallback = sessionServiceWrapper.logout(createSignOutCallback(preferenceFragment.getActivity()));
    }

    protected void detachLogoutCallback()
    {
        if (logoutCallback != null)
        {
            logoutCallback.setPrimaryCallback(null);
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
            THUser.clearCurrentUser();
            progressDialog.dismiss();
            // TODO move these lines into MiddleCallbackLogout?
            ActivityHelper.launchAuthentication(activity);
        }

        @Override public void failure(RetrofitError error)
        {
            progressDialog.setTitle(R.string.settings_misc_sign_out_failed);
            progressDialog.setMessage("");
            preferenceFragment.getView().postDelayed(new Runnable()
            {
                @Override public void run()
                {
                    progressDialog.dismiss();
                }
            }, 3000);
        }
    }
}
