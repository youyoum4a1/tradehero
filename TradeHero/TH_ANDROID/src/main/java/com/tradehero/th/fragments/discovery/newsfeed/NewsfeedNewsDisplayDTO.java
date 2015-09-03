package com.tradehero.th.fragments.discovery.newsfeed;

import com.tradehero.th.api.discussion.newsfeed.NewsfeedNewsDTO;
import org.ocpsoft.prettytime.PrettyTime;

public class NewsfeedNewsDisplayDTO extends NewsfeedDisplayDTO
{
    public String heroImage;
    public String title;

    public NewsfeedNewsDisplayDTO(NewsfeedNewsDTO newsfeedDTO, PrettyTime prettyTime)
    {
        super(newsfeedDTO, prettyTime);
    }
}
