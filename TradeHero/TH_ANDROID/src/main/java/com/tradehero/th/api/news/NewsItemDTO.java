package com.tradehero.th.api.news;

import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/6/14 Time: 4:10 PM Copyright (c) TradeHero
 */
public class NewsItemDTO extends AbstractDiscussionDTO
        implements DTO
{
    public String title;
    public String caption;
    public String description;

    public NewsItemSourceDTO source;

    public String imageUrl;

    public String url;

    public NewsItemCategoryDTO category;

    public String languageCode;

    private List<NewsItemMediaDTO> textEntities; // Needed to Hyperlink NewsItem's content
    private List<NewsItemMediaDTO> entities; // Needed to Hyperlink NewsItem's content
    private List<NewsItemMediaDTO> categories; // Header:Referenced Calais Entities
    private List<Integer> securityIds;

    public String message;

    public SecurityCompactDTO topReferencedSecurity;

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

    public List<Integer> getSecurityIds()
    {
        return securityIds != null ? Collections.unmodifiableList(securityIds) : null;
    }

    public void setSecurityIds(List<Integer> securityIds)
    {
        this.securityIds = securityIds;
    }

    //convenient method
    public NewsItemDTOKey getNewsItemDTOKey()
    {
        return new NewsItemDTOKey(id);
    }
}
