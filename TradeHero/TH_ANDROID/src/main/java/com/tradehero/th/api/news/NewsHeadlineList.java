package com.tradehero.th.api.news;

import com.tradehero.common.persistence.DTO;
import java.util.ArrayList;
import java.util.Collection;

public class NewsHeadlineList extends ArrayList<NewsHeadline> implements DTO
{
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
