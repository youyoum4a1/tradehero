package com.androidth.general.api.news;

import android.support.annotation.Nullable;
import com.androidth.general.api.ExtendedDTO;
import com.androidth.general.api.news.key.NewsItemDTOKey;
import java.util.Collections;
import java.util.List;

public class NewsItemDTO extends NewsItemCompactDTO
{
    public String text;

    public String imageUrl;

    private List<NewsItemMediaDTO> textEntities; // Needed to Hyperlink NewsItem's content
    private List<NewsItemMediaDTO> entities; // Needed to Hyperlink NewsItem's content
    @Nullable private List<NewsItemMediaDTO> categories; // Header:Referenced Calais Entities
    @Nullable public List<Integer> securityIds;

    public String message;

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

    @Nullable public List<NewsItemMediaDTO> getCategories()
    {
        if(categories != null)
        {
            return Collections.unmodifiableList(categories);
        }
        return null;
    }

    public void setCategories(@Nullable List<NewsItemMediaDTO> categories)
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
                super.toString() +
                ", text='" + text + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", textEntities=" + textEntities +
                ", entities=" + entities +
                ", categories=" + categories +
                ", securityIds=" + securityIds +
                ", message='" + message + '\'' +
                '}';
    }
}
