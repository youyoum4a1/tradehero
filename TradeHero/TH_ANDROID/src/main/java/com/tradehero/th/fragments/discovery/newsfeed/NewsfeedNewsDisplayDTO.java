package com.ayondo.academy.fragments.discovery.newsfeed;

import com.ayondo.academy.api.discussion.newsfeed.NewsfeedNewsDTO;
import com.ayondo.academy.api.news.NewsItemCompactDTO;
import org.ocpsoft.prettytime.PrettyTime;

public class NewsfeedNewsDisplayDTO extends NewsfeedDisplayDTO
{
    public final String heroImage;
    public final String title;
    public final String url;
    private final String description;

    public static NewsfeedNewsDisplayDTO from(NewsItemCompactDTO newsItemCompactDTO, PrettyTime prettyTime)
    {
        return new NewsfeedNewsDisplayDTO(NewsfeedNewsDTO.from(newsItemCompactDTO), prettyTime);
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
