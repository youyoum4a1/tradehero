package com.tradehero.th.api.level;

import com.tradehero.common.persistence.DTOKeyIdList;
import com.tradehero.th.api.level.key.LevelDefId;
import java.util.Collection;

public class LevelDefIdList extends DTOKeyIdList<LevelDefId>
{
    //<editor-fold desc="Constructors">
    public LevelDefIdList(Collection<? extends LevelDefDTO> collection)
    {
        super();
        for (LevelDefDTO levelDefDTO : collection)
        {
            add(levelDefDTO.getId());
        }
    }
    //</editor-fold>
}
