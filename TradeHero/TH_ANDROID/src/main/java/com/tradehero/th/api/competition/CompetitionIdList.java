package com.tradehero.th.api.competition;

import com.tradehero.common.persistence.DTOKeyIdList;
import com.tradehero.th.api.competition.key.CompetitionId;
import java.util.Collection;


public class CompetitionIdList extends DTOKeyIdList<CompetitionId>
{
    public static final String TAG = CompetitionIdList.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public CompetitionIdList()
    {
        super();
    }

    public CompetitionIdList(int capacity)
    {
        super(capacity);
    }

    public CompetitionIdList(Collection<? extends CompetitionId> collection)
    {
        super(collection);
    }
    //</editor-fold>
}
