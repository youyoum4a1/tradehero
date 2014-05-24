package com.tradehero.th.api.news;

import java.util.ArrayList;
import java.util.Collection;

public class NewsItemCompactDTOList extends ArrayList<NewsItemCompactDTO>
{
    //<editor-fold desc="Constructors">
    public NewsItemCompactDTOList(int initialCapacity)
    {
        super(initialCapacity);
    }

    public NewsItemCompactDTOList()
    {
        super();
    }

    public NewsItemCompactDTOList(Collection<? extends NewsItemCompactDTO> c)
    {
        super(c);
    }
    //</editor-fold>
}
