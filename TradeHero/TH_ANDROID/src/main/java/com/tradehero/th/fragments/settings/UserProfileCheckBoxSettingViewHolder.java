package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.util.Pair;
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
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.ProgressDialogUtil;
import org.jetbrains.annotations.NotNull;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

abstract public class UserProfileCheckBoxSettingViewHolder extends BaseOneCheckboxSettingViewHolder
{
    @NotNull protected final CurrentUserId currentUserId;
    @NotNull protected final UserProfileCacheRx userProfileCache;
    @NotNull protected final ProgressDialogUtil progressDialogUtil;
    @NotNull protected final UserServiceWrapper userServiceWrapper;

    protected ProgressDialog progressDialog;
    protected Subscription userProfileCacheSubscription;
    protected MiddleCallback<UserProfileDTO> middleCallbackUpdateUserProfile;

    //<editor-fold desc="Constructors">
    protected UserProfileCheckBoxSettingViewHolder(
            @NotNull CurrentUserId currentUserId,
            @NotNull UserProfileCacheRx userProfileCache,
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
        fetchUserProfile();
    }

    @Override public void destroyViews()
    {
        detachSubscription(userProfileCacheSubscription);
        userProfileCacheSubscription = null;
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
        detachSubscription(userProfileCacheSubscription);
        userProfileCacheSubscription = userProfileCache.get(currentUserId.toUserBaseKey())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createUserProfileCacheObserver());
    }

    protected Observer<Pair<UserBaseKey, UserProfileDTO>> createUserProfileCacheObserver()
    {
        return new UserProfileCacheObserver();
    }

    protected class UserProfileCacheObserver
            implements Observer<Pair<UserBaseKey, UserProfileDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> pair)
        {
            updateStatus(pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
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
