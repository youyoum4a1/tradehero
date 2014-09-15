package com.tradehero.th.persistence.discussion;

import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.news.NewsItemMediaDTO;
import com.tradehero.th.persistence.security.SecurityCompactCache;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class NewsItemCutDTO extends NewsItemCompactCutDTO
{
    public String text;
    public String imageUrl;
    private List<NewsItemMediaDTO> textEntities;
    private List<NewsItemMediaDTO> entities;
    @Nullable private List<NewsItemMediaDTO> categories;
    @Nullable public List<Integer> securityIds;
    public String message;

    NewsItemCutDTO(@NotNull NewsItemDTO newsItemDTO,
            @NotNull SecurityCompactCache securityCompactCache)
    {
        super(newsItemDTO, securityCompactCache);
        this.text = newsItemDTO.text;
        this.imageUrl = newsItemDTO.imageUrl;
        this.textEntities = newsItemDTO.getTextEntities();
        this.entities = newsItemDTO.getEntities();
        this.categories = newsItemDTO.getCategories();
        this.securityIds = newsItemDTO.securityIds;
        this.message = newsItemDTO.message;
    }

    @Nullable @Override NewsItemDTO inflate(@NotNull SecurityCompactCache securityCompactCache)
    {
        NewsItemDTO inflated = new NewsItemDTO();
        if (!populate(inflated, securityCompactCache))
        {
            return null;
        }
        return inflated;
    }

    boolean populate(@NotNull NewsItemDTO inflated,
            @NotNull SecurityCompactCache securityCompactCache)
    {
        if (!super.populate(inflated, securityCompactCache))
        {
            return false;
        }
        inflated.text = this.text;
        inflated.imageUrl = this.imageUrl;
        inflated.setTextEntities(this.textEntities);
        inflated.setEntities(this.entities);
        inflated.setCategories(this.categories);
        inflated.securityIds = this.securityIds;
        inflated.message = this.message;
        return true;
    }
}
