package com.tradehero.th.fragments.settings;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.support.v4.preference.PreferenceFragment;
import com.tradehero.th2.R;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.base.THUser;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.callback.MiddleLogInCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.user.auth.MainCredentialsPreference;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.ProgressDialogUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import timber.log.Timber;

abstract public class SocialConnectSettingViewHolder
    extends UserProfileCheckBoxSettingViewHolder
{
    @NotNull protected final AlertDialogUtil alertDialogUtil;
    @NotNull protected final SocialServiceWrapper socialServiceWrapper;
    @NotNull protected MainCredentialsPreference mainCredentialsPreference;
    @Nullable protected MiddleLogInCallback middleSocialConnectLogInCallback;
    @Nullable protected MiddleCallback<UserProfileDTO> middleCallbackUpdateUserProfile;
    @Nullable protected MiddleCallback<UserProfileDTO> middleCallbackDisconnect;
    @Nullable protected AlertDialog unlinkConfirmDialog;

    //<editor-fold desc="Constructors">
    protected SocialConnectSettingViewHolder(
            @NotNull CurrentUserId currentUserId,
            @NotNull UserProfileCache userProfileCache,
            @NotNull ProgressDialogUtil progressDialogUtil,
            @NotNull UserServiceWrapper userServiceWrapper,
            @NotNull AlertDialogUtil alertDialogUtil,
            @NotNull SocialServiceWrapper socialServiceWrapper,
            @NotNull MainCredentialsPreference mainCredentialsPreference)
    {
        super(currentUserId, userProfileCache, progressDialogUtil, userServiceWrapper);
        this.alertDialogUtil = alertDialogUtil;
        this.socialServiceWrapper = socialServiceWrapper;
        this.mainCredentialsPreference = mainCredentialsPreference;
    }
    //</editor-fold>

    @Override public void initViews(@NotNull DashboardPreferenceFragment preferenceFragment)
    {
        super.initViews(preferenceFragment);
        Preference clickablePrefCopy = clickablePref;
        if (clickablePrefCopy != null)
        {
            clickablePrefCopy.setOrder(preferenceFragment.getResources().getInteger(getOrderIntResId()));
        }
    }

    protected abstract int getOrderIntResId();

    @Override public void destroyViews()
    {
        detachMiddleSocialConnectLogInCallback();
        detachMiddleServerDisconnectCallback();
        hideUnlinkConfirmDialog();
        super.destroyViews();
    }

    protected void detachMiddleSocialConnectLogInCallback()
    {
        MiddleLogInCallback callbackCopy = middleSocialConnectLogInCallback;
        if (callbackCopy != null)
        {
            callbackCopy.setInnerCallback(null);
        }
    }

    protected void detachMiddleServerDisconnectCallback()
    {
        MiddleCallback<UserProfileDTO> middleCallbackDisconnectCopy = middleCallbackDisconnect;
        if (middleCallbackDisconnectCopy != null)
        {
            middleCallbackDisconnectCopy.setPrimaryCallback(null);
        }
    }

    protected void hideUnlinkConfirmDialog()
    {
        AlertDialog unlinkConfirmDialogCopy = unlinkConfirmDialog;
        if (unlinkConfirmDialogCopy != null)
        {
            unlinkConfirmDialogCopy.dismiss();
        }
        unlinkConfirmDialog = null;
    }

    protected boolean changeStatus(boolean enable)
    {
        Context activityContext = null;
        if (preferenceFragment != null)
        {
            activityContext = preferenceFragment.getActivity();
        }
        Timber.d("Sharing is asked to change");
        if (activityContext != null && enable)
        {
            progressDialog = progressDialogUtil.show(
                    activityContext,
                    getLinkingDialogTitle(),
                    getLinkingDialogMessage());
        }
        else if (activityContext != null)
        {
            if (isMainLogin())
            {
                dismissProgress();
                alertDialogUtil.popWithNegativeButton(
                        activityContext,
                        R.string.app_name,
                        R.string.authentication_unlink_fail_message,
                        R.string.ok);
            }
            else
            {
                popConfirmUnlinkDialog();
            }
        }
        return false;
    }

    protected void popConfirmUnlinkDialog()
    {
        Context activityContext = null;
        if (preferenceFragment != null)
        {
            activityContext = preferenceFragment.getActivity();
        }
        if (activityContext != null)
        {
            hideUnlinkConfirmDialog();
            unlinkConfirmDialog = alertDialogUtil.popWithOkCancelButton(
                    activityContext,
                    activityContext.getString(R.string.authentication_unlink_confirm_dialog_title, getSocialNetworkName()),
                    activityContext.getString(R.string.authentication_unlink_confirm_dialog_message, getSocialNetworkName()),
                    R.string.authentication_unlink_confirm_dialog_button_ok,
                    R.string.cancel,
                    new DialogInterface.OnClickListener()
                    {
                        @Override public void onClick(DialogInterface dialogInterface, int i)
                        {
                            dialogInterface.dismiss();
                            effectUnlink();
                        }
                    });
        }
    }

    protected void effectUnlink()
    {
        Context activityContext = null;
        if (preferenceFragment != null)
        {
            activityContext = preferenceFragment.getActivity();
        }
        if (activityContext != null)
        {
            progressDialog = progressDialogUtil.show(
                    activityContext,
                    getUnlinkingProgressDialogTitle(),
                    getUnlinkingProgressDialogMessage());
        }
    }

    @Nullable abstract protected String getSocialNetworkName();
    abstract protected int getLinkingDialogTitle();
    abstract protected int getLinkingDialogMessage();
    abstract protected int getUnlinkingProgressDialogTitle();
    abstract protected int getUnlinkingProgressDialogMessage();

    @NotNull protected MiddleLogInCallback createMiddleSocialConnectLogInCallback()
    {
        return new MiddleLogInCallback(new SocialConnectLogInCallback());
    }

    protected class SocialConnectLogInCallback extends LogInCallback
    {
        @Override public void done(UserLoginDTO user, THException ex)
        {
            dismissProgress();
        }

        @Override public void onStart()
        {
        }

        @Override public boolean onSocialAuthDone(JSONCredentials json)
        {
            reportConnectToServer(json);
            ProgressDialog progressDialogCopy = progressDialog;
            PreferenceFragment preferenceFragmentCopy = preferenceFragment;
            if (progressDialogCopy != null && preferenceFragmentCopy != null)
            {
                progressDialogCopy.setMessage(
                        String.format(
                                preferenceFragmentCopy.getString(R.string.authentication_connecting_tradehero),
                                getSocialNetworkName()));
            }
            return false;
        }
    }

    protected void reportConnectToServer(JSONCredentials json)
    {
        detachMiddleSocialConnectLogInCallback();
        middleCallbackUpdateUserProfile = socialServiceWrapper.connect(
                currentUserId.toUserBaseKey(),
                UserFormFactory.create(json),
                createServerConnectCallback());
    }

    protected Callback<UserProfileDTO> createServerConnectCallback()
    {
        return new UserProfileUpdateCallback();
    }

    protected Callback<UserProfileDTO> createSocialDisconnectCallback()
    {
        return new ServerUnlinkingCallback();
    }

    protected class ServerUnlinkingCallback extends UserProfileUpdateCallback
    {
        @Override protected void success(@NotNull UserProfileDTO userProfileDTO, THResponse thResponse)
        {
            super.success(userProfileDTO, thResponse);
            THUser.removeCredential(getSocialNetworkName());
        }
    }

    @Override protected void updateStatus(@NotNull UserProfileDTO userProfileDTO)
    {
        CheckBoxPreference clickablePrefCopy = clickablePref;
        if (clickablePrefCopy != null)
        {
            boolean mainLogin = isMainLogin();
            clickablePrefCopy.setEnabled(!mainLogin);
            if (mainLogin)
            {
                clickablePrefCopy.setSummary(R.string.authentication_setting_is_current_login);
            }
            else
            {
                clickablePrefCopy.setSummary(null);
            }
        }
    }

    abstract protected boolean isMainLogin();
}
