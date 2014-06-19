package com.tradehero.th.persistence.social;

import com.tradehero.common.persistence.StraightDTOCache;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.network.service.FollowerServiceWrapper;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton public class FollowerSummaryCache extends StraightDTOCache<UserBaseKey, FollowerSummaryDTO>
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

    @Override protected FollowerSummaryDTO fetch(@NotNull UserBaseKey key) throws Throwable
    {
        return followerServiceWrapper.getAllFollowersSummary(key);
    }

    @Contract("null -> null; !null -> !null") @Nullable
    public List<FollowerSummaryDTO> getOrFetch(@Nullable List<UserBaseKey> baseKeys) throws Throwable
    {
        if (baseKeys == null)
        {
            return null;
        }

        List<FollowerSummaryDTO> followerSummaryDTOs = new ArrayList<>();
        for (@NotNull UserBaseKey baseKey : baseKeys)
        {
            followerSummaryDTOs.add(getOrFetch(baseKey, false));
        }
        return followerSummaryDTOs;
    }
}
