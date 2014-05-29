package com.tradehero.th.api.news;

import com.tradehero.th.api.ExtendedDTO;
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

    //<editor-fold desc="Constructors">
    public NewsItemDTO()
    {
        super();
    }

    public <ExtendedDTOType extends ExtendedDTO> NewsItemDTO(ExtendedDTOType other,
            Class<? extends NewsItemDTO> myClass)
    {
        super(other, myClass);
    }
    //</editor-fold>

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

    @Override public NewsItemDTOKey getDiscussionKey()
    {
        return new NewsItemDTOKey(id);
    }

    @Override public String toString()
    {
        return "NewsItemDTO{" +
                "title='" + title + '\'' +
                ", caption='" + caption + '\'' +
                ", description='" + description + '\'' +
                ", source=" + source +
                ", imageUrl='" + imageUrl + '\'' +
                ", url='" + url + '\'' +
                ", category=" + category +
                ", textEntities=" + textEntities +
                ", entities=" + entities +
                ", categories=" + categories +
                ", securityIds=" + securityIds +
                ", message='" + message + '\'' +
                ", topReferencedSecurity=" + topReferencedSecurity +
                '}';
    }
}
