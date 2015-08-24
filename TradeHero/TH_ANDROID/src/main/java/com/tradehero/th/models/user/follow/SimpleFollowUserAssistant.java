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
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;

public class SimpleFollowUserAssistant
{
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
                .finallyDo(new Action0()
                {
                    @Override public void call()
                    {
                        AlertDialogUtil.dismissProgressDialog();
                    }
                });
    }

    @NonNull public Observable<UserProfileDTO> launchFreeFollowRx()
    {
        showProgress(R.string.following_this_hero);
        return userServiceWrapper.freeFollowRx(heroId)
                .observeOn(AndroidSchedulers.mainThread())
                .finallyDo(new Action0()
                {
                    @Override public void call()
                    {
                        AlertDialogUtil.dismissProgressDialog();
                    }
                });
    }

    protected void showProgress(@StringRes int contentResId)
    {
        AlertDialogUtil.showProgressDialog(
                context,
                context.getString(contentResId));
    }
}
