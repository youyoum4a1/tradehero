package com.tradehero.th.api.level;

import com.android.internal.util.Predicate;
import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import java.util.Collections;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class LevelDefDTOList extends BaseArrayList<LevelDefDTO>
        implements DTO
{
    @Nullable public LevelDefDTO findCurrentLevel(final int currentXP)
    {
        return findFirstWhere(new Predicate<LevelDefDTO>()
        {
            @Override public boolean apply(@NonNull LevelDefDTO levelDefDTO)
            {
                return levelDefDTO.isXPInLevel(currentXP);
            }
        });
    }

    @Nullable public LevelDefDTO getNextLevelDTO(int currentLevel)
    {
        if (!isEmpty())
        {
            for (int index = 0; index < size(); index++)
            {
                LevelDefDTO levelDefDTO = get(index);
                if (levelDefDTO.level == currentLevel)
                {
                    if (isMaxLevel(levelDefDTO))
                    {
                        return levelDefDTO;
                    }
                    return get(index + 1);
                }
            }
        }
        return null;
    }

    public boolean isMaxLevel(@Nullable LevelDefDTO levelDefDTO)
    {
        return levelDefDTO != null && levelDefDTO.equals(getMaxLevelDTO());
    }

    @Nullable public LevelDefDTO getMaxLevelDTO()
    {
        if (!isEmpty())
        {
            return get(size() - 1);
        }
        return null;
    }

    public void sort()
    {
        Collections.sort(this, new LevelDefDTONumericLevelComparator());
    }
}
