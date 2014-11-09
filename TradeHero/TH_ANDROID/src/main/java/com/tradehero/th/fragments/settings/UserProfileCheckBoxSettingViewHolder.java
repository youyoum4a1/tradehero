package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.ProgressDialogUtil;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.EmptyObserver;

abstract public class UserProfileCheckBoxSettingViewHolder extends BaseOneCheckboxSettingViewHolder
{
    @NonNull protected final CurrentUserId currentUserId;
    @NonNull protected final UserProfileCacheRx userProfileCache;
    @NonNull protected final ProgressDialogUtil progressDialogUtil;
    @NonNull protected final UserServiceWrapper userServiceWrapper;

    protected ProgressDialog progressDialog;
    protected Subscription userProfileCacheSubscription;
    @Deprecated
    protected MiddleCallback<UserProfileDTO> middleCallbackUpdateUserProfile;

    //<editor-fold desc="Constructors">
    protected UserProfileCheckBoxSettingViewHolder(
            @NonNull CurrentUserId currentUserId,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull ProgressDialogUtil progressDialogUtil,
            @NonNull UserServiceWrapper userServiceWrapper)
    {
        this.currentUserId = currentUserId;
        this.userProfileCache = userProfileCache;
        this.progressDialogUtil = progressDialogUtil;
        this.userServiceWrapper = userServiceWrapper;
    }
    //</editor-fold>

    @Override public void initViews(@NonNull DashboardPreferenceFragment preferenceFragment)
    {
        super.initViews(preferenceFragment);
        fetchUserProfile();
    }

    @Override public void destroyViews()
    {
        unsubscribe(userProfileCacheSubscription);
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

    @Deprecated
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
        unsubscribe(userProfileCacheSubscription);
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

    abstract protected void updateStatus(@NonNull UserProfileDTO userProfileDTO);

    protected class UserProfileUpdateObserver extends EmptyObserver<UserProfileDTO>
    {
        @Override public void onNext(UserProfileDTO args)
        {
            dismissProgress();
            updateStatus(args);
        }

        @Override public void onError(Throwable e)
        {
            dismissProgress();
            THToast.show(new THException(e));
        }
    }
}
