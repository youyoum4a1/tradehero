package com.tradehero.th.fragments.discovery.newsfeed;

import com.tradehero.th.api.discussion.newsfeed.NewsfeedNewsDTO;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import org.ocpsoft.prettytime.PrettyTime;

public class NewsfeedNewsDisplayDTO extends NewsfeedDisplayDTO
{
    public final String heroImage;
    public final String title;
    public final String url;
    private final String description;

    public static NewsfeedNewsDisplayDTO from(NewsItemCompactDTO newsItemCompactDTO, PrettyTime prettyTime)
    {
        NewsfeedNewsDTO newsfeedNewsDTO = new NewsfeedNewsDTO();
        newsfeedNewsDTO.id = newsItemCompactDTO.id;
        newsfeedNewsDTO.createdAtUTC = newsItemCompactDTO.createdAtUtc;
        newsfeedNewsDTO.picture = newsItemCompactDTO.source.imageUrl;
        newsfeedNewsDTO.displayName = newsItemCompactDTO.source.rootName;
        newsfeedNewsDTO.url = newsItemCompactDTO.url;
        newsfeedNewsDTO.thumbnail = newsItemCompactDTO.thumbnail;
        newsfeedNewsDTO.title = newsItemCompactDTO.title;
        newsfeedNewsDTO.description = newsItemCompactDTO.description;
        return new NewsfeedNewsDisplayDTO(newsfeedNewsDTO, prettyTime);
    }

    public NewsfeedNewsDisplayDTO(NewsfeedNewsDTO newsfeedDTO, PrettyTime prettyTime)
    {
        super(newsfeedDTO, prettyTime);
        this.heroImage = newsfeedDTO.thumbnail;
        this.title = newsfeedDTO.title;
        this.url = newsfeedDTO.url;
        this.description = newsfeedDTO.description;
    }

    @Override public String getBody()
    {
        return this.description;
    }
}
