package com.tradehero.th.api.discussion.newsfeed;

import com.tradehero.th.api.news.NewsItemCompactDTO;

public class NewsfeedNewsDTO extends NewsfeedDTO
{
    public static final String DTO_DESERIALISING_TYPE = "news";

    public String description;
    public String thumbnail;
    public String title;
    public String url;

    public static NewsfeedNewsDTO from(NewsItemCompactDTO compactDTO)
    {
        NewsfeedNewsDTO newsfeedNewsDTO = new NewsfeedNewsDTO();
        newsfeedNewsDTO.id = compactDTO.id;
        newsfeedNewsDTO.createdAtUTC = compactDTO.createdAtUtc;
        newsfeedNewsDTO.picture = compactDTO.source.imageUrl;
        newsfeedNewsDTO.displayName = compactDTO.source.rootName;
        newsfeedNewsDTO.url = compactDTO.url;
        newsfeedNewsDTO.thumbnail = compactDTO.thumbnail;
        newsfeedNewsDTO.title = compactDTO.title;
        newsfeedNewsDTO.description = compactDTO.description;
        return newsfeedNewsDTO;
    }
}
