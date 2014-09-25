package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.misc.callback.THCallback;
import com.tradehero.th.misc.callback.THResponse;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.ProgressDialogUtil;
import org.jetbrains.annotations.NotNull;

abstract public class UserProfileCheckBoxSettingViewHolder extends BaseOneCheckboxSettingViewHolder
{
    @NotNull protected final CurrentUserId currentUserId;
    @NotNull protected final UserProfileCache userProfileCache;
    @NotNull protected final ProgressDialogUtil progressDialogUtil;
    @NotNull protected final UserServiceWrapper userServiceWrapper;

    protected ProgressDialog progressDialog;
    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> userProfileCacheListener;
    protected MiddleCallback<UserProfileDTO> middleCallbackUpdateUserProfile;

    //<editor-fold desc="Constructors">
    protected UserProfileCheckBoxSettingViewHolder(
            @NotNull CurrentUserId currentUserId,
            @NotNull UserProfileCache userProfileCache,
            @NotNull ProgressDialogUtil progressDialogUtil,
            @NotNull UserServiceWrapper userServiceWrapper)
    {
        this.currentUserId = currentUserId;
        this.userProfileCache = userProfileCache;
        this.progressDialogUtil = progressDialogUtil;
        this.userServiceWrapper = userServiceWrapper;
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
        userProfileCacheListener = null;
        detachUserProfileCache();
        detachMiddleCallback();
        dismissProgress();
        super.destroyViews();
    }

    protected void dismissProgress()
    {
        ProgressDialog progressDialogCopy = progressDialog;
        if (progressDialogCopy != null)
        {
            progressDialogCopy.dismiss();
        }
        progressDialog = null;
    }

    protected void detachUserProfileCache()
    {
        userProfileCache.unregister(userProfileCacheListener);
    }

    protected void detachMiddleCallback()
    {
        MiddleCallback<UserProfileDTO> middleCallbackCopy = middleCallbackUpdateUserProfile;
        if (middleCallbackCopy != null)
        {
            middleCallbackCopy.setPrimaryCallback(null);
        }
        middleCallbackUpdateUserProfile = null;
    }

    protected void fetchUserProfile()
    {
        detachUserProfileCache();
        userProfileCache.register(currentUserId.toUserBaseKey(), userProfileCacheListener);
        userProfileCache.getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createUserProfileCacheListener()
    {
        return new UserProfileCacheListener();
    }

    protected class UserProfileCacheListener
            implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            updateStatus(value);
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_your_user_profile);
        }
    }

    abstract protected void updateStatus(@NotNull UserProfileDTO userProfileDTO);

    protected class UserProfileUpdateCallback extends THCallback<UserProfileDTO>
    {
        @Override protected void success(UserProfileDTO userProfileDTO, THResponse thResponse)
        {
            updateStatus(userProfileDTO);
        }

        @Override protected void failure(THException ex)
        {
            THToast.show(ex);
        }

        @Override protected void finish()
        {
            dismissProgress();
        }
    }
}
