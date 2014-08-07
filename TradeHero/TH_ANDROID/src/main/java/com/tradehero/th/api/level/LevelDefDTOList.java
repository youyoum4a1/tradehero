package com.tradehero.th.api.level;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import org.jetbrains.annotations.NotNull;

public class LevelDefDTOList extends BaseArrayList<LevelDefDTO> implements DTO
{
    @NotNull public LevelDefIdList createKeys()
    {
        LevelDefIdList list = new LevelDefIdList();
        for (LevelDefDTO levelDefDTO : this)
        {
            list.add(levelDefDTO.getId());
        }
        return list;
    }
}
