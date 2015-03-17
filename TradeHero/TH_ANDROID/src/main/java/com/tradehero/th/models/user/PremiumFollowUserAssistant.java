package com.tradehero.th.models.user;

import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.persistence.user.UserProfileCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;

public class PremiumFollowUserAssistant extends SimplePremiumFollowUserAssistant
        implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
{
    @Inject protected UserProfileCache userProfileCache;
    @Inject protected CurrentUserId currentUserId;
    protected UserProfileDTO currentUserProfile;
    protected final OwnedPortfolioId applicablePortfolioId;
    @Nullable protected Integer requestCode;

    //<editor-fold desc="Constructors">
    public PremiumFollowUserAssistant(
            @NotNull UserBaseKey userToFollow,
            @Nullable OnUserFollowedListener userFollowedListener,
            OwnedPortfolioId applicablePortfolioId)
    {
        super(userToFollow, userFollowedListener);
        this.applicablePortfolioId = applicablePortfolioId;
    }
    //</editor-fold>

    @Override public void launchFollow()
    {
        userProfileCache.register(currentUserId.toUserBaseKey(), this);
        userProfileCache.getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
    {
        this.currentUserProfile = value;
        checkBalanceAndFollow();
    }

    @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
    {
        notifyFollowFailed(userToFollow, error);
    }

    protected void checkBalanceAndFollow()
    {
        if (this.currentUserProfile.ccBalance > 0)
        {
            super.launchFollow();
        }
        else
        {
            haveInteractorForget();
        }
    }

    protected void haveInteractorForget()
    {
        requestCode = null;
    }

    @Override protected void notifyFollowFailed(@NotNull UserBaseKey userToFollow, @NotNull Throwable error)
    {
        haveInteractorForget();
        super.notifyFollowFailed(userToFollow, error);
    }

    @Override protected void notifyFollowSuccess(@NotNull UserBaseKey userToFollow, @NotNull UserProfileDTO currentUserProfile)
    {
        haveInteractorForget();
        super.notifyFollowSuccess(userToFollow, currentUserProfile);
    }
}
