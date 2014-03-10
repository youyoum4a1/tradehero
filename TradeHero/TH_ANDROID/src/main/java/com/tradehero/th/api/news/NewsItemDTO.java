package com.tradehero.th.api.news;

import android.os.Bundle;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import java.util.Collections;
import java.util.Date;
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
        return Collections.unmodifiableList(securityIds);
    }

    public void setSecurityIds(List<Integer> securityIds)
    {
        this.securityIds = securityIds;
    }

    private static String KEY_PARAMETER_ID = "news_id";
    private static String KEY_PARAMETER_TITLE = "news_title";
    private static String KEY_PARAMETER_DATE = "news_date";
    private static String KEY_PARAMETER_URL = "url";
    private static String KEY_PARAMETER_IS_VOTED_UP = "is_voted_up";

    public Bundle toBundle(boolean isVotedUp){
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PARAMETER_ID,id);
        bundle.putString(KEY_PARAMETER_TITLE,title);
        bundle.putSerializable(KEY_PARAMETER_DATE,createdAtUtc);
        bundle.putString(KEY_PARAMETER_URL,url);
        bundle.putBoolean(KEY_PARAMETER_IS_VOTED_UP,isVotedUp);
        return bundle;
    }

    public static NewsItemDTO getSampleNewsItemDTO(Bundle bundle) {
        NewsItemDTO dto = new NewsItemDTO();
        dto.id = bundle.getInt(KEY_PARAMETER_ID);
        dto.title = bundle.getString(KEY_PARAMETER_TITLE);
        dto.createdAtUtc = (Date)bundle.getSerializable(KEY_PARAMETER_DATE);
        dto.url = bundle.getString(KEY_PARAMETER_URL);
        dto.voteDirection = bundle.getBoolean(KEY_PARAMETER_IS_VOTED_UP) ? 1:0;
        return dto;
    }
}
