package com.tradehero.th.models.user.follow;

import android.app.Activity;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.social.hero.HeroAlertDialogUtil;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.social.FollowDialogCombo;
import com.tradehero.th.models.social.OnFollowRequestedListener;
import com.tradehero.th.persistence.user.UserProfileCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChoiceFollowUserAssistantWithDialog
    implements OnFollowRequestedListener,
        DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
{
    @Inject protected CurrentUserId currentUserId;
    @Inject protected UserProfileCache userProfileCache;
    @Inject HeroAlertDialogUtil heroAlertDialogUtil;

    @NotNull private final Activity activity;
    @NotNull protected final UserBaseKey heroId;
    @NotNull protected final FollowUserAssistant followUserAssistant;
    @Nullable protected FollowDialogCombo followDialogCombo;
    @Nullable protected UserProfileDTO currentUserProfile;
    @Nullable protected UserBaseDTO heroBaseInfo;

    //<editor-fold desc="Constructors">
    public ChoiceFollowUserAssistantWithDialog(
            @NotNull Activity activity,
            @NotNull UserBaseKey heroId,
            @Nullable SimpleFollowUserAssistant.OnUserFollowedListener userFollowedListener,
            @NotNull OwnedPortfolioId applicablePortfolioId)
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
        detachUserCache();
        detachFollowDialogCombo();
        followUserAssistant.onDestroy();
    }

    protected void detachUserCache()
    {
        userProfileCache.unregister(this);
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
    public void setHeroBaseInfo(@NotNull UserBaseDTO heroBaseInfo)
    {
        this.heroBaseInfo = heroBaseInfo;
    }

    public void launchChoice()
    {
        userProfileCache.register(currentUserId.toUserBaseKey(), this);
        userProfileCache.getOrFetchAsync(currentUserId.toUserBaseKey());
        if (heroBaseInfo == null)
        {
            userProfileCache.register(heroId, this);
            userProfileCache.getOrFetchAsync(heroId);
        }
    }

    @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
    {
        if (key.equals(heroId))
        {
            heroBaseInfo = value;
        }
        else
        {
            currentUserProfile = value;
        }
        launchFollowChoice();
    }

    @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
    {
        followUserAssistant.notifyFollowFailed(heroId, error);
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

    @Override public void freeFollowRequested(@NotNull UserBaseKey heroId)
    {
        followUserAssistant.launchFreeFollow();
    }

    @Override public void premiumFollowRequested(@NotNull UserBaseKey heroId)
    {
        followUserAssistant.launchPremiumFollow();
    }
}
