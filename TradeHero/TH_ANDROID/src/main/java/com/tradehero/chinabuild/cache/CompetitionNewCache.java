package com.tradehero.chinabuild.cache;

import com.tradehero.chinabuild.data.UserCompetitionDTOList;
import com.tradehero.common.persistence.StraightDTOCacheNew;
import com.tradehero.th.network.service.CompetitionServiceWrapper;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class CompetitionNewCache extends StraightDTOCacheNew<CompetitionListType, UserCompetitionDTOList>
{
    public static final int DEFAULT_MAX_SIZE = 500;
    @NotNull private final CompetitionServiceWrapper competitionServiceWrapper;
    @Inject Lazy<CompetitionNewCache> competitionNewCacheLazy;

    @Inject public CompetitionNewCache(
            @NotNull CompetitionServiceWrapper competitionServiceWrapper)
    {
        this(DEFAULT_MAX_SIZE,competitionServiceWrapper);
    }

    public CompetitionNewCache(
            int maxSize,
            @NotNull CompetitionServiceWrapper competitionServiceWrapper)
    {
        super(maxSize);
        this.competitionServiceWrapper = competitionServiceWrapper;

    }
    //</editor-fold>

    @Override @NotNull public UserCompetitionDTOList fetch(@NotNull CompetitionListType key) throws Throwable
    {
        return competitionServiceWrapper.getCompetition(key);
    }



}
