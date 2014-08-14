package com.tradehero.th.persistence.discussion;

import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.PrivateDiscussionDTO;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.persistence.user.UserProfileCompactCache;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class AbstractDiscussionCompactCutDTOFactory
{
    @NotNull private final UserProfileCompactCache userProfileCompactCache;
    @NotNull private final SecurityCompactCache securityCompactCache;

    //<editor-fold desc="Constructors">
    @Inject AbstractDiscussionCompactCutDTOFactory(
            @NotNull UserProfileCompactCache userProfileCompactCache,
            @NotNull SecurityCompactCache securityCompactCache)
    {
        this.userProfileCompactCache = userProfileCompactCache;
        this.securityCompactCache = securityCompactCache;
    }
    //</editor-fold>

    @NotNull AbstractDiscussionCompactCutDTO shrinkValue(@NotNull AbstractDiscussionCompactDTO value)
    {
        if (value instanceof NewsItemDTO)
        {
            return new NewsItemCutDTO((NewsItemDTO) value, securityCompactCache);
        }
        else if (value instanceof NewsItemCompactDTO)
        {
            return new NewsItemCompactCutDTO((NewsItemCompactDTO) value, securityCompactCache);
        }
        else if (value instanceof TimelineItemDTO)
        {
            return new TimelineItemCutDTO((TimelineItemDTO) value, userProfileCompactCache);
        }
        else if (value instanceof PrivateDiscussionDTO)
        {
            return new PrivateDiscussionCutDTO((PrivateDiscussionDTO) value);
        }
        else if (value instanceof DiscussionDTO)
        {
            return new DiscussionCutDTO((DiscussionDTO) value);
        }
        throw new IllegalArgumentException("Unhandled class " + value.getClass().getCanonicalName());
    }

    @Nullable AbstractDiscussionCompactDTO inflate(@Nullable AbstractDiscussionCompactCutDTO cutValue)
    {
        if (cutValue == null)
        {
            return null;
        }
        if (cutValue instanceof NewsItemCutDTO)
        {
            return ((NewsItemCutDTO) cutValue).inflate(securityCompactCache);
        }
        else if (cutValue instanceof NewsItemCompactCutDTO)
        {
            return ((NewsItemCompactCutDTO) cutValue).inflate(securityCompactCache);
        }
        else if (cutValue instanceof TimelineItemCutDTO)
        {
            return ((TimelineItemCutDTO) cutValue).inflate(userProfileCompactCache);
        }
        else if (cutValue instanceof PrivateDiscussionCutDTO)
        {
            return ((PrivateDiscussionCutDTO) cutValue).inflate();
        }
        else if (cutValue instanceof DiscussionCutDTO)
        {
            return ((DiscussionCutDTO) cutValue).inflate();
        }
        throw new IllegalArgumentException("Unhandled class " + cutValue.getClass().getCanonicalName());
    }
}
