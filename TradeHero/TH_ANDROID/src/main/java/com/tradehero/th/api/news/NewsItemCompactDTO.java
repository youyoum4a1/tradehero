package com.tradehero.th.api.news;

import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.security.SecurityCompactDTO;

public class NewsItemCompactDTO extends AbstractDiscussionCompactDTO
{
    public String title;
    public String caption;
    public String description;

    public NewsItemSourceDTO source;
    public String url;

    public NewsItemCategoryDTO category;
    public SecurityCompactDTO topReferencedSecurity;

    public NewsItemDTOKey createDTOKey()
    {
        return new NewsItemDTOKey(id);
    }

    @Override public String toString()
    {
        return "NewsItemCompactDTO{" +
                "title='" + title + '\'' +
                ", caption='" + caption + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
