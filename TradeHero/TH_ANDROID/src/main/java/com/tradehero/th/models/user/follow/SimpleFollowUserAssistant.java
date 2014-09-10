package com.tradehero.th.models.user.follow;

import android.app.Activity;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.utils.AlertDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SimpleFollowUserAssistant implements Callback<UserProfileDTO>
{
    @Inject protected Lazy<AlertDialogUtil> alertDialogUtilLazy;
    @Inject protected UserServiceWrapper userServiceWrapper;

    @NotNull private final Activity activity;
    @NotNull protected final UserBaseKey heroId;
    @Nullable private OnUserFollowedListener userFollowedListener;

    //<editor-fold desc="Constructors">
    public SimpleFollowUserAssistant(
            @NotNull Activity activity,
            @NotNull UserBaseKey heroId,
            @Nullable OnUserFollowedListener userFollowedListener)
    {
        super();
        this.activity = activity;
        this.heroId = heroId;
        this.userFollowedListener = userFollowedListener;
        HierarchyInjector.inject(activity, this);
    }
    //</editor-fold>

    public void onDestroy()
    {
        setUserFollowedListener(null);
    }

    public void setUserFollowedListener(@Nullable OnUserFollowedListener userFollowedListener)
    {
        this.userFollowedListener = userFollowedListener;
    }

    public void launchUnFollow()
    {
        showProgress(R.string.manage_heroes_unfollow_progress_message);
        userServiceWrapper.unfollow(heroId, this);
    }

    public void launchFreeFollow()
    {
        showProgress(R.string.following_this_hero);
        userServiceWrapper.freeFollow(heroId, this);
    }

    protected void launchPremiumFollow()
    {
        showProgress(R.string.following_this_hero);
        userServiceWrapper.follow(heroId, this);
    }

    protected void showProgress(int contentResId)
    {
        alertDialogUtilLazy.get().showProgressDialog(
                activity,
                activity.getString(contentResId));
    }

    @Override public void success(UserProfileDTO userProfileDTO, Response response)
    {
        alertDialogUtilLazy.get().dismissProgressDialog();
        notifyFollowSuccess(heroId, userProfileDTO);
    }

    @Override public void failure(RetrofitError error)
    {
        alertDialogUtilLazy.get().dismissProgressDialog();
        notifyFollowFailed(heroId, error);
    }

    protected void notifyFollowSuccess(
            @NotNull UserBaseKey userToFollow,
            @NotNull UserProfileDTO currentUserProfile)
    {
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
        OnUserFollowedListener userFollowedListenerCopy = userFollowedListener;
        if (userFollowedListenerCopy != null)
        {
            userFollowedListenerCopy.onUserFollowFailed(userToFollow, error);
        }
    }

    public static interface OnUserFollowedListener
    {
        void onUserFollowSuccess(@NotNull UserBaseKey userFollowed, @NotNull UserProfileDTO currentUserProfileDTO);
        void onUserFollowFailed(@NotNull UserBaseKey userFollowed, @NotNull Throwable error);
    }
}
