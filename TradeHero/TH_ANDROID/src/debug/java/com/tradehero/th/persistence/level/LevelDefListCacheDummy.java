package com.tradehero.th.persistence.level;

import com.tradehero.th.api.level.LevelDefDTO;
import com.tradehero.th.api.level.LevelDefDTOList;
import com.tradehero.th.api.level.key.LevelDefListId;
import com.tradehero.th.network.service.AchievementServiceWrapper;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class LevelDefListCacheDummy extends LevelDefListCache
{
    @Inject public LevelDefListCacheDummy(
            @NotNull AchievementServiceWrapper userServiceWrapper,
            @NotNull LevelDefCache levelDefCache)
    {
        super(userServiceWrapper, levelDefCache);
        getOrFetchAsync(new LevelDefListId());
    }

    @NotNull @Override public LevelDefDTOList fetch(@NotNull LevelDefListId key) throws Throwable
    {
        LevelDefDTO mockLevel1 = new LevelDefDTO();
        mockLevel1.id = 1;
        mockLevel1.level = 1;
        mockLevel1.xpFrom = 0;
        mockLevel1.xpTo = 1000;

        LevelDefDTO mockLevel2 = new LevelDefDTO();
        mockLevel2.id = 2;
        mockLevel2.level = 2;
        mockLevel2.xpFrom = 1001;
        mockLevel2.xpTo = 2000;

        LevelDefDTO mockLevel3 = new LevelDefDTO();
        mockLevel3.id = 3;
        mockLevel3.level = 3;
        mockLevel3.xpFrom = 2001;
        mockLevel3.xpTo = 3000;

        LevelDefDTOList levelDefDTOList = new LevelDefDTOList();
        levelDefDTOList.add(mockLevel1);
        levelDefDTOList.add(mockLevel3);
        levelDefDTOList.add(mockLevel2);

        return levelDefDTOList;
    }
}
