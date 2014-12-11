package com.tradehero.th.models.user.follow;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import com.tradehero.th.R;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.utils.AlertDialogUtil;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.EmptyObserver;

public class SimpleFollowUserAssistant extends EmptyObserver<UserProfileDTO>
{
    @Inject protected Lazy<AlertDialogUtil> alertDialogUtilLazy;
    @Inject protected UserServiceWrapper userServiceWrapper;

    @NonNull private final Context context;
    @NonNull protected final UserBaseKey heroId;
    @Nullable private OnUserFollowedListener userFollowedListener;

    //<editor-fold desc="Constructors">
    public SimpleFollowUserAssistant(
            @NonNull Context context,
            @NonNull UserBaseKey heroId,
            @Nullable OnUserFollowedListener userFollowedListener)
    {
        super();
        this.context = context;
        this.heroId = heroId;
        this.userFollowedListener = userFollowedListener;
        HierarchyInjector.inject(context, this);
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

    @NonNull public Observable<UserProfileDTO> launchUnFollowRx()
    {
        showProgress(R.string.manage_heroes_unfollow_progress_message);
        return userServiceWrapper.unfollowRx(heroId)
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo(() -> alertDialogUtilLazy.get().dismissProgressDialog());
    }

    @NonNull public Observable<UserProfileDTO> launchFreeFollowRx()
    {
        showProgress(R.string.following_this_hero);
        return userServiceWrapper.freeFollowRx(heroId)
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo(() -> alertDialogUtilLazy.get().dismissProgressDialog());
    }

    public void launchFreeFollow()
    {
        showProgress(R.string.following_this_hero);
        userServiceWrapper.freeFollowRx(heroId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    @NonNull protected Observable<UserProfileDTO> launchPremiumFollowRx()
    {
        showProgress(R.string.following_this_hero);
        return userServiceWrapper.followRx(heroId)
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo(() -> alertDialogUtilLazy.get().dismissProgressDialog());
    }

    protected void launchPremiumFollow()
    {
        showProgress(R.string.following_this_hero);
        userServiceWrapper.followRx(heroId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    protected void showProgress(@StringRes int contentResId)
    {
        alertDialogUtilLazy.get().showProgressDialog(
                context,
                context.getString(contentResId));
    }

    @Override public void onNext(UserProfileDTO userProfileDTO)
    {
        alertDialogUtilLazy.get().dismissProgressDialog();
        notifyFollowSuccess(heroId, userProfileDTO);
    }

    @Override public void onError(Throwable e)
    {
        alertDialogUtilLazy.get().dismissProgressDialog();
        notifyFollowFailed(heroId, e);
    }

    protected void notifyFollowSuccess(
            @NonNull UserBaseKey userToFollow,
            @NonNull UserProfileDTO currentUserProfile)
    {
        OnUserFollowedListener userFollowedListenerCopy = userFollowedListener;
        if (userFollowedListenerCopy != null)
        {
            userFollowedListenerCopy.onUserFollowSuccess(userToFollow, currentUserProfile);
        }
    }

    protected void notifyFollowFailed(
            @NonNull UserBaseKey userToFollow,
            @NonNull Throwable error)
    {
        OnUserFollowedListener userFollowedListenerCopy = userFollowedListener;
        if (userFollowedListenerCopy != null)
        {
            userFollowedListenerCopy.onUserFollowFailed(userToFollow, error);
        }
    }

    public static interface OnUserFollowedListener
    {
        void onUserFollowSuccess(@NonNull UserBaseKey userFollowed, @NonNull UserProfileDTO currentUserProfileDTO);
        void onUserFollowFailed(@NonNull UserBaseKey userFollowed, @NonNull Throwable error);
    }
}
