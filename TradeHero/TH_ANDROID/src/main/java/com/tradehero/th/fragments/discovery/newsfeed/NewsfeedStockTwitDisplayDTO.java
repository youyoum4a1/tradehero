package com.tradehero.th.fragments.discovery.newsfeed;

import com.tradehero.th.api.discussion.newsfeed.NewsfeedStockTwitDTO;
import org.ocpsoft.prettytime.PrettyTime;

public class NewsfeedStockTwitDisplayDTO extends NewsfeedDisplayDTO
{
    public String heroImage;

    public NewsfeedStockTwitDisplayDTO(NewsfeedStockTwitDTO newsfeedDTO, PrettyTime prettyTime)
    {
        super(newsfeedDTO, prettyTime);
    }
}
