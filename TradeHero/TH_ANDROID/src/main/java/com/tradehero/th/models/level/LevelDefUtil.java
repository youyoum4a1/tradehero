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
    @NotNull private LevelDefListCache levelDefListCache;

    private LevelDefDTOList levelDefDTOList;
    @NotNull private final LevelDefListId levelDefListId = new LevelDefListId();
    @Nullable private DTOCacheNew.Listener<LevelDefListId, LevelDefDTOList> leaderDefListener;

    @Inject public LevelDefUtil(@NotNull LevelDefListCache levelDefListCache)
    {
        super();
        this.levelDefListCache = levelDefListCache;
        leaderDefListener = createLevelDefListener();
        setAndSortLevelDefDTOList(this.levelDefListCache.get(levelDefListId));
        this.levelDefListCache.register(levelDefListId, leaderDefListener);
        this.levelDefListCache.getOrFetchAsync(levelDefListId);
    }

    public void destroy()
    {
        this.levelDefListCache.unregister(leaderDefListener);
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
        LevelDefDTO found = null;
        if (levelDefDTOList != null)
        {
            found = levelDefDTOList.findCurrentLevel(currentXP);
        }
        return found;
    }

    @Nullable public LevelDefDTO getNextLevelDTO(int currentLevel)
    {
        if(levelDefDTOList != null)
        {
            return levelDefDTOList.getNextLevelDTO(currentLevel);
        }
        return null;
    }

    public boolean isMaxLevel(LevelDefDTO levelDefDTO)
    {
        return levelDefDTO != null && levelDefDTO.equals(getMaxLevelDTO());
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
