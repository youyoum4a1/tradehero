package com.tradehero.th.models.user;

import android.content.Context;
import com.tradehero.th.R;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.persistence.social.HeroListCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class SimplePremiumFollowUserAssistant implements Callback<UserProfileDTO>
{
    @Inject protected Lazy<AlertDialogUtil> alertDialogUtilLazy;
    @Inject protected UserServiceWrapper userServiceWrapper;
    @Inject protected Lazy<CurrentActivityHolder> currentActivityHolderLazy;
    @Inject protected Lazy<HeroListCache> heroListCacheLazy;
    @Inject protected UserProfileCache userProfileCache;
    @Inject protected CurrentUserId currentUserId;
    @Nullable private OnUserFollowedListener userFollowedListener;
    @NotNull protected final UserBaseKey userToFollow;

    //<editor-fold desc="Constructors">
    public SimplePremiumFollowUserAssistant(
            @NotNull UserBaseKey userToFollow,
            @Nullable OnUserFollowedListener userFollowedListener)
    {
        super();
        this.userToFollow = userToFollow;
        this.userFollowedListener = userFollowedListener;
        DaggerUtils.inject(this);
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
        heroListCacheLazy.get().invalidate(userProfileDTO.getBaseKey());
        updateUserProfileCache(userProfileDTO);
        notifyFollowSuccess(userToFollow, userProfileDTO);
    }

    private void updateUserProfileCache(UserProfileDTO userProfileDTO)
    {
        if (userProfileCache != null && currentUserId != null)
        {
            UserBaseKey userBaseKey = currentUserId.toUserBaseKey();
            userProfileCache.put(userBaseKey, userProfileDTO);
        }
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
        void onUserFollowSuccess(UserBaseKey userFollowed, UserProfileDTO currentUserProfileDTO);

        void onUserFollowFailed(UserBaseKey userFollowed, Throwable error);
    }
}
