package com.ayondo.academy.models.user.follow;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import com.ayondo.academy.R;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.inject.HierarchyInjector;
import com.ayondo.academy.network.service.UserServiceWrapper;
import com.ayondo.academy.persistence.user.UserProfileCacheRx;
import com.ayondo.academy.rx.ReplaceWithFunc1;
import com.ayondo.academy.rx.dialog.OnDialogClickEvent;
import com.ayondo.academy.utils.AlertDialogRxUtil;
import com.ayondo.academy.utils.AlertDialogUtil;
import java.util.ArrayList;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

public class FollowUserAssistant
{
    @Inject protected UserServiceWrapper userServiceWrapper;
    @Inject protected UserProfileCacheRx userProfileCacheRx;
    @Inject protected CurrentUserId currentUserId;

    @NonNull private final Context context;
    @NonNull protected final UserBaseKey heroId;

    public static void updateFollowButton(Button button, boolean isFollowing, UserBaseKey heroId)
    {
        if (heroId.isOfficialAccount())
        {
            button.setVisibility(View.GONE);
        }
        else
        {
            button.setVisibility(View.VISIBLE);
            if (isFollowing)
            {
                button.setBackgroundResource(R.drawable.basic_green_selector);
                button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_following, 0, 0, 0);
                button.setText(R.string.following);
            }
            else
            {
                button.setBackgroundResource(R.drawable.basic_blue_selector);
                button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_follow, 0, 0, 0);
                button.setText(R.string.follow);
            }
        }
    }

    public static void updateFollowImageButton(ImageButton imgButton, boolean isFollowing, UserBaseKey heroId)
    {
        if (heroId.isOfficialAccount())
        {
            imgButton.setVisibility(View.GONE);
        }
        else
        {
            imgButton.setVisibility(View.VISIBLE);
            imgButton.setBackgroundResource(isFollowing ? R.drawable.basic_green_selector : R.drawable.basic_blue_selector);
            imgButton.setImageResource(isFollowing ? R.drawable.ic_following : R.drawable.ic_follow);
        }
    }

    //<editor-fold desc="Constructors">
    public FollowUserAssistant(
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

    public void unFollowFromCache()
    {
        UserProfileDTO cachedValue = userProfileCacheRx.getCachedValue(currentUserId.toUserBaseKey());
        if (cachedValue != null && cachedValue.heroIds != null)
        {
            cachedValue.heroIds.remove(heroId.getUserId());
            userProfileCacheRx.onNext(currentUserId.toUserBaseKey(), cachedValue);
        }
    }

    public Observable<UserProfileDTO> unFollowFromServer()
    {
        return userServiceWrapper.unfollowRx(heroId).doOnNext(new Action1<UserProfileDTO>()
        {
            @Override public void call(UserProfileDTO userProfileDTO)
            {
                userProfileCacheRx.onNext(currentUserId.toUserBaseKey(), userProfileDTO);
            }
        });
    }

    public void followingInCache()
    {
        UserProfileDTO cachedValue = userProfileCacheRx.getCachedValue(currentUserId.toUserBaseKey());
        if (cachedValue != null)
        {
            if (cachedValue.heroIds == null)
            {
                cachedValue.heroIds = new ArrayList<>(1);
            }
            cachedValue.heroIds.add(heroId.getUserId());
            userProfileCacheRx.onNext(currentUserId.toUserBaseKey(), cachedValue);
        }
    }

    public Observable<UserProfileDTO> followingInServer()
    {
        return userServiceWrapper.freeFollowRx(heroId).doOnNext(new Action1<UserProfileDTO>()
        {
            @Override public void call(UserProfileDTO userProfileDTO)
            {
                userProfileCacheRx.onNext(currentUserId.toUserBaseKey(), userProfileDTO);
            }
        });
    }

    public Observable<OnDialogClickEvent> showUnFollowConfirmation(String displayName)
    {
        return AlertDialogRxUtil.build(context)
                .setTitle(context.getString(R.string.manage_heroes_alert_unfollow_title,
                        displayName))
                .setMessage(R.string.manage_heroes_alert_unfollow_message)
                .setPositiveButton(R.string.manage_heroes_alert_unfollow_ok)
                .setNegativeButton(R.string.manage_heroes_alert_unfollow_cancel)
                .build()
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OnDialogClickEvent, Boolean>()
                {
                    @Override public Boolean call(OnDialogClickEvent onDialogClickEvent)
                    {
                        return onDialogClickEvent.isPositive();
                    }
                });
    }

    public Observable<OnDialogClickEvent> showFollowForMessageConfirmation(String displayName)
    {
        return AlertDialogRxUtil.build(context)
                .setTitle(context.getString(R.string.pm_not_follow_title, displayName))
                .setMessage(R.string.pm_not_follow_msg)
                .setPositiveButton(R.string.pm_not_follow_positive)
                .setNegativeButton(R.string.pm_not_follow_negative)
                .build()
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OnDialogClickEvent, Boolean>()
                {
                    @Override public Boolean call(OnDialogClickEvent onDialogClickEvent)
                    {
                        return onDialogClickEvent.isPositive();
                    }
                });
    }

    public Observable<FollowUserAssistant> ensureCacheValue()
    {
        return userProfileCacheRx.getOne(currentUserId.toUserBaseKey())
                .map(new ReplaceWithFunc1<Pair<UserBaseKey, UserProfileDTO>, FollowUserAssistant>(this));
    }
}
