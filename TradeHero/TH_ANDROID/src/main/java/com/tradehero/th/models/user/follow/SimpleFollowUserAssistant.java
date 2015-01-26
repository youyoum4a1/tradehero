package com.tradehero.th.models.user.follow;

import android.content.Context;
import android.support.annotation.NonNull;
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

public class SimpleFollowUserAssistant
{
    @Inject protected Lazy<AlertDialogUtil> alertDialogUtilLazy;
    @Inject protected UserServiceWrapper userServiceWrapper;

    @NonNull private final Context context;
    @NonNull protected final UserBaseKey heroId;

    //<editor-fold desc="Constructors">
    public SimpleFollowUserAssistant(
            @NonNull Context context,
            @NonNull UserBaseKey heroId)
    {
        super();
        this.context = context;
        this.heroId = heroId;
        HierarchyInjector.inject(context, this);
    }
    //</editor-fold>

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

    @NonNull protected Observable<UserProfileDTO> launchPremiumFollowRx()
    {
        showProgress(R.string.following_this_hero);
        return userServiceWrapper.followRx(heroId)
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo(() -> alertDialogUtilLazy.get().dismissProgressDialog());
    }

    protected void showProgress(@StringRes int contentResId)
    {
        alertDialogUtilLazy.get().showProgressDialog(
                context,
                context.getString(contentResId));
    }
}
