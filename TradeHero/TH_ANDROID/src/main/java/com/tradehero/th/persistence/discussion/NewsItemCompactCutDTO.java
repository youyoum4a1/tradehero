package com.tradehero.th.persistence.discussion;

import com.tradehero.th.api.news.NewsItemCategoryDTO;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.NewsItemSourceDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class NewsItemCompactCutDTO extends AbstractDiscussionCompactCutDTO
{
    public String title;
    public String caption;
    public String description;

    public NewsItemSourceDTO source;
    public String url;

    public NewsItemCategoryDTO category;
    public SecurityId topReferencedSecurity;

    NewsItemCompactCutDTO(
            @NotNull NewsItemCompactDTO newsItemCompactDTO,
            @NotNull SecurityCompactCache securityCompactCache)
    {
        super(newsItemCompactDTO);
        this.title = newsItemCompactDTO.title;
        this.caption = newsItemCompactDTO.caption;
        this.description = newsItemCompactDTO.description;
        this.source = newsItemCompactDTO.source;
        this.url = newsItemCompactDTO.url;
        this.category = newsItemCompactDTO.category;
        if (newsItemCompactDTO.topReferencedSecurity != null)
        {
            this.topReferencedSecurity = newsItemCompactDTO.topReferencedSecurity.getSecurityId();
            securityCompactCache.put(this.topReferencedSecurity, newsItemCompactDTO.topReferencedSecurity);

        }
    }

    @Nullable NewsItemCompactDTO inflate(@NotNull SecurityCompactCache securityCompactCache)
    {
        NewsItemCompactDTO inflated = new NewsItemCompactDTO();
        if (!populate(inflated, securityCompactCache))
        {
            return null;
        }
        return inflated;
    }

    boolean populate(@NotNull NewsItemCompactDTO inflated,
            @NotNull SecurityCompactCache securityCompactCache)
    {
        if (!super.populate(inflated))
        {
            return false;
        }
        inflated.title = this.title;
        inflated.caption = this.caption;
        inflated.description = this.description;
        inflated.source = this.source;
        inflated.url = this.url;
        inflated.category = this.category;
        if (this.topReferencedSecurity != null)
        {
            inflated.topReferencedSecurity = securityCompactCache.get(this.topReferencedSecurity);
            if (inflated.topReferencedSecurity == null)
            {
                return false;
            }
        }
        return true;
    }
}
