package com.tradehero.th.models.level;

import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.api.level.LevelDefDTO;
import com.tradehero.th.api.level.LevelDefDTOList;
import com.tradehero.th.api.level.LevelDefDTONumericLevelComparator;
import com.tradehero.th.api.level.key.LevelDefListId;
import com.tradehero.th.persistence.level.LevelDefListCache;
import java.util.Collections;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

@Singleton public class LevelDefUtil
{
    private LevelDefListCache levelDefListCache;
    private LevelDefDTOList levelDefDTOList;

    private final LevelDefListId levelDefListId = new LevelDefListId();


    @Inject public LevelDefUtil(LevelDefListCache levelDefListCache)
    {
        super();
        this.levelDefListCache = levelDefListCache;
        setAndSortLevelDefDTOList(this.levelDefListCache.get(levelDefListId));
        this.levelDefListCache.register(levelDefListId, createLevelDefListener());
        this.levelDefListCache.getOrFetchAsync(levelDefListId);
    }

    public void update()
    {
        this.levelDefListCache.getOrFetchAsync(levelDefListId);
    }

    public void forceUpdate()
    {
        this.levelDefListCache.getOrFetchAsync(levelDefListId, true);
    }

    @Nullable public LevelDefDTO getCurrentLevel(int currentXP)
    {
        if (levelDefDTOList != null)
        {
            for (LevelDefDTO levelDefDTO : levelDefDTOList)
            {
                if (levelDefDTO.xpFrom <= currentXP && levelDefDTO.xpTo >= currentXP)
                {
                    return levelDefDTO;
                }
            }
        }
        return null;
    }

    @Nullable public LevelDefDTO getNextLevelDTO(int currentLevel)
    {
        if(levelDefDTOList != null && !levelDefDTOList.isEmpty())
        {
            for (int i = 0; i < levelDefDTOList.size(); i++)
            {
                LevelDefDTO levelDefDTO = levelDefDTOList.get(i);
                if (levelDefDTO.level == currentLevel)
                {
                    if (isMaxLevel(levelDefDTO))
                    {
                        return levelDefDTO;
                    }
                    return levelDefDTOList.get(i + 1);
                }
            }
        }
        return null;
    }

    public boolean isMaxLevel(LevelDefDTO levelDefDTO)
    {
        if (levelDefDTO == null)
        {
            return false;
        }
        return levelDefDTO.equals(getMaxLevelDTO());
    }

    @Nullable public LevelDefDTO getMaxLevelDTO()
    {
        if (levelDefDTOList != null && !levelDefDTOList.isEmpty())
        {
            return levelDefDTOList.get(levelDefDTOList.size() - 1);
        }
        return null;
    }

    public void setAndSortLevelDefDTOList(@Nullable LevelDefDTOList levelDefDTOList)
    {
        this.levelDefDTOList = levelDefDTOList;
        sortLevelDefDTOList();
    }

    private void sortLevelDefDTOList()
    {
        if (levelDefDTOList != null)
        {
            Collections.sort(levelDefDTOList, new LevelDefDTONumericLevelComparator());
        }
    }

    private LevelDefCacheListener createLevelDefListener()
    {
        return new LevelDefCacheListener();
    }

    private class LevelDefCacheListener implements DTOCacheNew.Listener<LevelDefListId, LevelDefDTOList>
    {

        @Override public void onDTOReceived(@NotNull LevelDefListId key, @NotNull LevelDefDTOList value)
        {
            //setAndSortLevelDefDTOList(value);
        }

        @Override public void onErrorThrown(@NotNull LevelDefListId key, @NotNull Throwable error)
        {
            Timber.e("error on getting LevelDef %s", error);
        }
    }
}
