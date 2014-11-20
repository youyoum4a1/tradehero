package com.tradehero.th.models.user.follow;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogUtil;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.social.FollowDialogCombo;
import com.tradehero.th.models.social.OnFollowRequestedListener;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class ChoiceFollowUserAssistantWithDialog
        implements OnFollowRequestedListener
{
    @Inject protected CurrentUserId currentUserId;
    @Inject protected UserProfileCacheRx userProfileCache;
    @Inject HeroAlertDialogUtil heroAlertDialogUtil;

    @NonNull private final Activity activity;
    @NonNull protected final UserBaseKey heroId;
    @NonNull protected final FollowUserAssistant followUserAssistant;
    @Nullable protected FollowDialogCombo followDialogCombo;
    @Nullable protected Subscription currentUserProfileSubscription;
    @Nullable protected UserProfileDTO currentUserProfile;
    @Nullable protected Subscription heroSubscription;
    @Nullable protected UserBaseDTO heroBaseInfo;

    //<editor-fold desc="Constructors">
    public ChoiceFollowUserAssistantWithDialog(
            @NonNull Activity activity,
            @NonNull UserBaseKey heroId,
            @Nullable SimpleFollowUserAssistant.OnUserFollowedListener userFollowedListener,
            @NonNull OwnedPortfolioId applicablePortfolioId)
    {
        super();
        this.activity = activity;
        this.heroId = heroId;
        this.followUserAssistant = new FollowUserAssistant(activity, heroId, userFollowedListener, applicablePortfolioId);
        HierarchyInjector.inject(activity, this);
    }
    //</editor-fold>

    public void onDestroy()
    {
        unsubscribe(currentUserProfileSubscription);
        currentUserProfileSubscription = null;
        unsubscribe(heroSubscription);
        heroSubscription = null;
        detachFollowDialogCombo();
        followUserAssistant.onDestroy();
    }

    protected void unsubscribe(@Nullable Subscription subscription)
    {
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
    }

    protected void detachFollowDialogCombo()
    {
        FollowDialogCombo copy = followDialogCombo;
        if (copy != null)
        {
            copy.followDialogView.setFollowRequestedListener(null);
        }
        followDialogCombo = null;
    }

    @SuppressWarnings("NullableProblems")
    public void setHeroBaseInfo(@NonNull UserBaseDTO heroBaseInfo)
    {
        this.heroBaseInfo = heroBaseInfo;
    }

    public void launchChoice()
    {
        unsubscribe(currentUserProfileSubscription);
        currentUserProfileSubscription = userProfileCache.get(currentUserId.toUserBaseKey())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createUserProfileCacheObserver());
        if (heroBaseInfo == null)
        {
            unsubscribe(heroSubscription);
            heroSubscription = userProfileCache.get(heroId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(createUserProfileCacheObserver());
        }
    }

    @NonNull Observer<Pair<UserBaseKey, UserProfileDTO>> createUserProfileCacheObserver()
    {
        return new UserProfileCacheObserver();
    }

    protected class UserProfileCacheObserver implements Observer<Pair<UserBaseKey, UserProfileDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> pair)
        {
            if (pair.first.equals(heroId))
            {
                heroBaseInfo = pair.second;
            }
            else
            {
                currentUserProfile = pair.second;
            }
            launchFollowChoice();
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            followUserAssistant.notifyFollowFailed(heroId, e);
        }
    }

    protected void launchFollowChoice()
    {
        if (heroBaseInfo != null && currentUserProfile != null)
        {
            detachFollowDialogCombo();
            followDialogCombo = heroAlertDialogUtil.showFollowDialog(
                    activity,
                    heroBaseInfo,
                    currentUserProfile.getFollowType(heroId),
                    this);
        }
    }

    @Override public void freeFollowRequested(@NonNull UserBaseKey heroId)
    {
        followUserAssistant.launchFreeFollow();
    }

    @Override public void premiumFollowRequested(@NonNull UserBaseKey heroId)
    {
        followUserAssistant.launchPremiumFollow();
    }
}
