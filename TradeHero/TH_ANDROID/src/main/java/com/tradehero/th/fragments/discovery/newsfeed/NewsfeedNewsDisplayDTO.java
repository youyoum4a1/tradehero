package com.tradehero.th.fragments.discovery.newsfeed;

import com.tradehero.th.api.discussion.newsfeed.NewsfeedNewsDTO;
import org.ocpsoft.prettytime.PrettyTime;

public class NewsfeedNewsDisplayDTO extends NewsfeedDisplayDTO
{
    public final String heroImage;
    public final String title;
    public final String url;
    private final String description;

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
