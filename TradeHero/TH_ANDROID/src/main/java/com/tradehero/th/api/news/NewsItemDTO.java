package com.tradehero.th.api.news;

import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.security.SecurityCompactDTO;
import java.util.Collections;
import java.util.List;


public class NewsItemDTO extends AbstractDiscussionDTO
{
    public String title;
    public String caption;
    public String description;

    public NewsItemSourceDTO source;

    public String imageUrl;

    public String url;

    public NewsItemCategoryDTO category;

    private List<NewsItemMediaDTO> textEntities; // Needed to Hyperlink NewsItem's content
    private List<NewsItemMediaDTO> entities; // Needed to Hyperlink NewsItem's content
    private List<NewsItemMediaDTO> categories; // Header:Referenced Calais Entities
    public List<Integer> securityIds;

    public String message;

    public SecurityCompactDTO topReferencedSecurity;

    public NewsItemDTO()
    {
    }

    public List<NewsItemMediaDTO> getTextEntities()
    {
        return Collections.unmodifiableList(textEntities);
    }

    public void setTextEntities(List<NewsItemMediaDTO> textEntities)
    {
        this.textEntities = textEntities;
    }

    public List<NewsItemMediaDTO> getEntities()
    {
        return Collections.unmodifiableList(entities);
    }

    public void setEntities(List<NewsItemMediaDTO> entities)
    {
        this.entities = entities;
    }

    public List<NewsItemMediaDTO> getCategories()
    {
        return Collections.unmodifiableList(categories);
    }

    public void setCategories(List<NewsItemMediaDTO> categories)
    {
        this.categories = categories;
    }

    //convenient method
    @Override public NewsItemDTOKey getDiscussionKey()
    {
        return new NewsItemDTOKey(id);
    }
}
