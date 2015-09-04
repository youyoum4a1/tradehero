package com.tradehero.th.fragments.discovery.newsfeed;

import com.tradehero.th.api.discussion.newsfeed.NewsfeedStockTwitDTO;
import org.ocpsoft.prettytime.PrettyTime;

public class NewsfeedStockTwitDisplayDTO extends NewsfeedDisplayDTO
{
    private final String message;
    public final String heroImage;

    public NewsfeedStockTwitDisplayDTO(NewsfeedStockTwitDTO newsfeedDTO, PrettyTime prettyTime)
    {
        super(newsfeedDTO, prettyTime);
        this.message = newsfeedDTO.message;
        this.heroImage = newsfeedDTO.thumbnail;
    }

    @Override public String getBody()
    {
        return message;
    }
}
