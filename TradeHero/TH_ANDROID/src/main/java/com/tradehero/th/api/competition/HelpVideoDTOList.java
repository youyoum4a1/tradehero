package com.tradehero.th.api.competition;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import org.jetbrains.annotations.NotNull;

public class HelpVideoDTOList extends BaseArrayList<HelpVideoDTO>
    implements DTO
{
    //<editor-fold desc="Constructors">
    public HelpVideoDTOList()
    {
        super();
    }
    //</editor-fold>

    @NotNull public HelpVideoIdList createKeys()
    {
        HelpVideoIdList list = new HelpVideoIdList();
        for (HelpVideoDTO videoDTO : this)
        {
            list.add(videoDTO.getHelpVideoId());
        }
        return list;
    }
}
