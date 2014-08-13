package com.tradehero.th.fragments.settings;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.preference.PreferenceFragment;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.form.UserFormFactory;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.base.THUser;
import com.tradehero.th.misc.callback.LogInCallback;
import com.tradehero.th.misc.callback.MiddleLogInCallback;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.ProgressDialogUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import timber.log.Timber;

abstract public class SocialConnectSettingViewHolder
    extends BaseOneCheckboxSettingViewHolder
{
    @NotNull protected final CurrentUserId currentUserId;
    @NotNull protected final UserProfileCache userProfileCache;
    @NotNull protected final ProgressDialogUtil progressDialogUtil;
    @NotNull protected final AlertDialogUtil alertDialogUtil;
    @NotNull protected final SocialServiceWrapper socialServiceWrapper;
    @Nullable protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    @Nullable protected MiddleLogInCallback middleSocialConnectLogInCallback;
    @Nullable protected MiddleCallback<UserProfileDTO> middleCallbackConnect;
    @Nullable protected MiddleCallback<UserProfileDTO> middleCallbackDisconnect;
    @Nullable protected ProgressDialog progressDialog;
    @Nullable protected AlertDialog unlinkConfirmDialog;

    //<editor-fold desc="Constructors">
    protected SocialConnectSettingViewHolder(
            @NotNull CurrentUserId currentUserId,
            @NotNull UserProfileCache userProfileCache,
            @NotNull ProgressDialogUtil progressDialogUtil,
            @NotNull AlertDialogUtil alertDialogUtil,
            @NotNull SocialServiceWrapper socialServiceWrapper)
    {
        this.currentUserId = currentUserId;
        this.userProfileCache = userProfileCache;
        this.progressDialogUtil = progressDialogUtil;
        this.alertDialogUtil = alertDialogUtil;
        this.socialServiceWrapper = socialServiceWrapper;
    }
    //</editor-fold>

    @Override public void initViews(@NotNull DashboardPreferenceFragment preferenceFragment)
    {
        super.initViews(preferenceFragment);
        userProfileCacheListener = createUserProfileCacheListener();
        fetchUserProfile();
    }

    @Override public void destroyViews()
    {
        detachUserProfileCacheListener();
        detachMiddleSocialConnectLogInCallback();
        detachMiddleServerConnectCallback();
        detachMiddleServerDisconnectCallback();
        hideProgressDialog();
        hideUnlinkConfirmDialog();
        super.destroyViews();
    }

    protected void detachUserProfileCacheListener()
    {
        userProfileCache.unregister(userProfileCacheListener);
    }

    protected void detachMiddleSocialConnectLogInCallback()
    {
        MiddleLogInCallback callbackCopy = middleSocialConnectLogInCallback;
        if (callbackCopy != null)
        {
            callbackCopy.setInnerCallback(null);
        }
    }

    protected void detachMiddleServerConnectCallback()
    {
        MiddleCallback<UserProfileDTO> middleCallbackConnectCopy = middleCallbackConnect;
        if (middleCallbackConnectCopy != null)
        {
            middleCallbackConnectCopy.setPrimaryCallback(null);
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

    protected void hideProgressDialog()
    {
        ProgressDialog progressDialogCopy = progressDialog;
        if (progressDialogCopy != null)
        {
            progressDialogCopy.hide();
        }
        progressDialog = null;
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

    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileCacheListener()
    {
        return new SocialConnectUserProfileCacheListener();
    }

    protected class SocialConnectUserProfileCacheListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            updateSocialConnectStatus(value);
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
        }
    }

    protected void fetchUserProfile()
    {
        detachUserProfileCacheListener();
        userProfileCache.register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        userProfileCache.getOrFetchAsync(currentUserId.toUserBaseKey());
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
            popConfirmUnlinkDialog();
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
            hideProgressDialog();
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
        middleCallbackConnect = socialServiceWrapper.connect(
                currentUserId.toUserBaseKey(),
                UserFormFactory.create(json),
                createServerConnectCallback());
    }

    protected Callback<UserProfileDTO> createServerConnectCallback()
    {
        return new ServerLinkingCallback();
    }

    protected class ServerLinkingCallback extends THCallback<UserProfileDTO>
    {
        @Override protected void success(@NotNull UserProfileDTO userProfileDTO, THResponse thResponse)
        {
            updateSocialConnectStatus(userProfileDTO);
        }

        @Override protected void failure(THException ex)
        {
            // user unlinked current authentication
            THToast.show(ex);
        }

        @Override protected void finish()
        {
            hideProgressDialog();
        }
    }

    protected Callback<UserProfileDTO> createSocialDisconnectCallback()
    {
        return new ServerUnlinkingCallback();
    }

    protected class ServerUnlinkingCallback extends ServerLinkingCallback
    {
        @Override protected void success(@NotNull UserProfileDTO userProfileDTO, THResponse thResponse)
        {
            super.success(userProfileDTO, thResponse);
            THUser.removeCredential(getSocialNetworkName());
        }
    }

    abstract protected void updateSocialConnectStatus(@NotNull UserProfileDTO updatedUserProfileDTO);
}
