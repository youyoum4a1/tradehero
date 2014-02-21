package com.tradehero.th.api.competition;

import com.tradehero.common.persistence.DTOKeyIdList;
import com.tradehero.th.api.competition.key.HelpVideoId;
import java.util.Collection;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 6:55 PM To change this template use File | Settings | File Templates. */
public class HelpVideoIdList extends DTOKeyIdList<HelpVideoId>
{
    public static final String TAG = HelpVideoIdList.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public HelpVideoIdList()
    {
        super();
    }

    public HelpVideoIdList(int capacity)
    {
        super(capacity);
    }

    public HelpVideoIdList(Collection<? extends HelpVideoId> collection)
    {
        super(collection);
    }
    //</editor-fold>
}
