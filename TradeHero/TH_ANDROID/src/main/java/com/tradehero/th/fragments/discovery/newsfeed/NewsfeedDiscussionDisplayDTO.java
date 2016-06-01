package com.ayondo.academy.fragments.discovery.newsfeed;

import com.ayondo.academy.api.discussion.newsfeed.NewsfeedDiscussionDTO;
import org.ocpsoft.prettytime.PrettyTime;

public class NewsfeedDiscussionDisplayDTO extends NewsfeedDisplayDTO
{
    private final String message;
    public String logo;
    public String content;

    public NewsfeedDiscussionDisplayDTO(NewsfeedDiscussionDTO newsfeedDTO, PrettyTime prettyTime)
    {
        super(newsfeedDTO, prettyTime);
        this.message = newsfeedDTO.message;
    }

    @Override public String getBody()
    {
        return this.message;
    }
}