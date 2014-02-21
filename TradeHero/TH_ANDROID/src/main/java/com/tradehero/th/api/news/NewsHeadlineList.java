package com.tradehero.th.api.news;

import com.tradehero.common.persistence.DTO;
import java.util.ArrayList;
import java.util.Collection;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 7:27 PM To change this template use File | Settings | File Templates. */
public class NewsHeadlineList extends ArrayList<NewsHeadline> implements DTO
{
    public static final String TAG = NewsHeadlineList.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public NewsHeadlineList()
    {
        super();
    }

    public NewsHeadlineList(int capacity)
    {
        super(capacity);
    }

    public NewsHeadlineList(Collection<? extends NewsHeadline> collection)
    {
        super(collection);
    }
    //</editor-fold>

}
