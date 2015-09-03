package com.tradehero.th.fragments.discovery.newsfeed;

import com.tradehero.th.api.discussion.newsfeed.NewsfeedDiscussionDTO;
import org.ocpsoft.prettytime.PrettyTime;

public class NewsfeedDiscussionDisplayDTO extends NewsfeedDisplayDTO
{
    public String logo;
    public String content;

    public NewsfeedDiscussionDisplayDTO(NewsfeedDiscussionDTO newsfeedDTO, PrettyTime prettyTime)
    {
        super(newsfeedDTO, prettyTime);
    }
}