package com.tradehero.th.api.competition;

import com.tradehero.common.api.BaseArrayList;
import com.tradehero.common.persistence.DTO;
import android.support.annotation.NonNull;

public class CompetitionDTOList extends BaseArrayList<CompetitionDTO>
    implements DTO
{
    //<editor-fold desc="Constructors">
    public CompetitionDTOList()
    {
        super();
    }
    //</editor-fold>

    @NonNull public CompetitionIdList createKeys()
    {
        CompetitionIdList list = new CompetitionIdList();
        for (CompetitionDTO competitionDTO : this)
        {
            list.add(competitionDTO.getCompetitionId());
        }
        return list;
    }
}
