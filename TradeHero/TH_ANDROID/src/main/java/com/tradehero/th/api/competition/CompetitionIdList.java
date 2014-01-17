package com.tradehero.th.api.competition;

import com.tradehero.common.persistence.DTOKeyIdList;
import java.util.Collection;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 6:55 PM To change this template use File | Settings | File Templates. */
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
