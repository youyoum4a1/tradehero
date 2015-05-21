package com.tradehero.th.api.news;

import com.tradehero.common.persistence.DTO;
import java.util.ArrayList;
import java.util.Collection;

public class NewsHeadlineList extends ArrayList<NewsHeadline> implements DTO
{
    public NewsHeadlineList(Collection<? extends NewsHeadline> collection)
    {
        super(collection);
    }
    //</editor-fold>

}
