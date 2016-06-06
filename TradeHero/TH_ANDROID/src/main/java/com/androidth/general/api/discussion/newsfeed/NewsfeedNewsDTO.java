package com.androidth.general.api.discussion.newsfeed;

import com.androidth.general.api.news.NewsItemCompactDTO;

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
        newsfeedNewsDTO.thumbnail = compactDTO.imageUrl;
        newsfeedNewsDTO.title = compactDTO.title;
        newsfeedNewsDTO.description = compactDTO.description;
        return newsfeedNewsDTO;
    }
}
