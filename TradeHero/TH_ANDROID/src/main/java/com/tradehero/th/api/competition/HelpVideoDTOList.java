package com.tradehero.th.api.competition;

import android.support.annotation.NonNull;
import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;

public class HelpVideoDTOList extends BaseArrayList<HelpVideoDTO>
    implements DTO
{
    //<editor-fold desc="Constructors">
    public HelpVideoDTOList()
    {
        super();
    }
    //</editor-fold>

    @NonNull public HelpVideoIdList createKeys()
    {
        HelpVideoIdList list = new HelpVideoIdList();
        for (HelpVideoDTO videoDTO : this)
        {
            list.add(videoDTO.getHelpVideoId());
        }
        return list;
    }
}
