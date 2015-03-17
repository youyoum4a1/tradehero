package com.tradehero.th.api.users;

import com.tradehero.th.api.portfolio.PortfolioCompactDTOUtil;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

public class UserProfileDTOUtil extends UserBaseDTOUtil
{
    public final static int IS_NOT_FOLLOWER_WANT_MSG = -1;
    public final static int IS_NOT_FOLLOWER = 0;
    public final static int IS_FREE_FOLLOWER = 1;
    public final static int IS_PREMIUM_FOLLOWER = 2;

    @NotNull protected PortfolioCompactDTOUtil portfolioCompactDTOUtil;

    //<editor-fold desc="Constructors">
    @Inject public UserProfileDTOUtil(
            @NotNull PortfolioCompactDTOUtil portfolioCompactDTOUtil)
    {
        super();
        this.portfolioCompactDTOUtil = portfolioCompactDTOUtil;
    }
    //</editor-fold>
}
