package com.tradehero.th.persistence.social;

import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.FollowerServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class FollowerSummaryCache extends StraightDTOCacheNew<UserBaseKey, FollowerSummaryDTO>
{
    public static final int DEFAULT_MAX_SIZE = 100;

    @NotNull protected final FollowerServiceWrapper followerServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public FollowerSummaryCache(FollowerServiceWrapper followerServiceWrapper)
    {
        super(DEFAULT_MAX_SIZE);
        this.followerServiceWrapper = followerServiceWrapper;
    }
    //</editor-fold>

    @Override @NotNull public FollowerSummaryDTO fetch(@NotNull UserBaseKey key) throws Throwable
    {
        return followerServiceWrapper.getAllFollowersSummary(key);
    }
}
