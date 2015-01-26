package com.tradehero.th.fragments.settings;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.utils.ProgressDialogUtil;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

abstract public class UserProfileCheckBoxSettingViewHolder extends BaseOneCheckboxSettingViewHolder
{
    @NonNull protected final CurrentUserId currentUserId;
    @NonNull protected final UserProfileCacheRx userProfileCache;
    @NonNull protected final ProgressDialogUtil progressDialogUtil;
    @NonNull protected final UserServiceWrapper userServiceWrapper;

    protected ProgressDialog progressDialog;
    protected Subscription userProfileCacheSubscription;

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

    protected void fetchUserProfile()
    {
        unsubscribe(userProfileCacheSubscription);
        userProfileCacheSubscription = userProfileCache.get(currentUserId.toUserBaseKey())
                .map(pair -> pair.second)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::updateStatus,
                        e -> THToast.show(R.string.error_fetch_your_user_profile));
    }

    abstract protected void updateStatus(@NonNull UserProfileDTO userProfileDTO);

    public void onProfileUpdated(@NonNull UserProfileDTO args)
    {
        dismissProgress();
        updateStatus(args);
    }

    public void onProfileUpdateFailed(@NonNull Throwable e)
    {
        dismissProgress();
        THToast.show(new THException(e));
    }
}
