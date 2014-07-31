package com.tradehero.th.models.user;

import android.content.Context;
import com.tradehero.th.R;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.utils.AlertDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SimplePremiumFollowUserAssistant implements Callback<UserProfileDTO>
{
    @Inject protected Lazy<AlertDialogUtil> alertDialogUtilLazy;
    @Inject protected UserServiceWrapper userServiceWrapper;
    @Inject protected THBillingInteractor billingInteractor;
    @Inject protected Lazy<CurrentActivityHolder> currentActivityHolderLazy;
    @Nullable private OnUserFollowedListener userFollowedListener;
    @NotNull protected final UserBaseKey userToFollow;
    @Nullable protected Integer requestCode;

    //<editor-fold desc="Constructors">
    public SimplePremiumFollowUserAssistant(
            @NotNull UserBaseKey userToFollow,
            @Nullable OnUserFollowedListener userFollowedListener)
    {
        super();
        this.userToFollow = userToFollow;
        this.userFollowedListener = userFollowedListener;
    }
    //</editor-fold>

    public void setUserFollowedListener(@Nullable OnUserFollowedListener userFollowedListener)
    {
        this.userFollowedListener = userFollowedListener;
    }

    public void launchUnFollow()
    {
        userServiceWrapper.unfollow(userToFollow, this);
    }

    protected void launchFollow()
    {
        Context currentContext = currentActivityHolderLazy.get().getCurrentContext();
        if (currentContext != null)
        {
            alertDialogUtilLazy.get().showProgressDialog(
                    currentContext,
                    currentContext.getString(R.string.following_this_hero));
        }
        userServiceWrapper.follow(userToFollow, this);
    }

    @Override public void success(UserProfileDTO userProfileDTO, Response response)
    {
        alertDialogUtilLazy.get().dismissProgressDialog();
        notifyFollowSuccess(userToFollow, userProfileDTO);
    }

    @Override public void failure(RetrofitError error)
    {
        alertDialogUtilLazy.get().dismissProgressDialog();
        notifyFollowFailed(userToFollow, error);
    }

    protected void notifyFollowSuccess(
            @NotNull UserBaseKey userToFollow,
            @NotNull UserProfileDTO currentUserProfile)
    {
        haveInteractorForget();
        OnUserFollowedListener userFollowedListenerCopy = userFollowedListener;
        if (userFollowedListenerCopy != null)
        {
            userFollowedListenerCopy.onUserFollowSuccess(userToFollow, currentUserProfile);
        }
    }

    protected void notifyFollowFailed(
            @NotNull UserBaseKey userToFollow,
            @NotNull Throwable error)
    {
        haveInteractorForget();
        OnUserFollowedListener userFollowedListenerCopy = userFollowedListener;
        if (userFollowedListenerCopy != null)
        {
            userFollowedListenerCopy.onUserFollowFailed(userToFollow, error);
        }
    }

    protected void haveInteractorForget()
    {
        if (requestCode != null)
        {
            billingInteractor.forgetRequestCode(requestCode);
        }
        requestCode = null;
    }

    public static interface OnUserFollowedListener
    {
        void onUserFollowSuccess(UserBaseKey userFollowed, UserProfileDTO currentUserProfileDTO);

        void onUserFollowFailed(UserBaseKey userFollowed, Throwable error);
    }
}
