package com.tradehero.th.api.discussion;

import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;

import javax.inject.Inject;

public class AbstractDiscussionCompactDTOFactory
{
    //<editor-fold desc="Constructors">
    @Inject public AbstractDiscussionCompactDTOFactory()
    {
    }
    //</editor-fold>

    //<editor-fold desc="Clone">
    public AbstractDiscussionCompactDTO clone(AbstractDiscussionCompactDTO original)
    {
        if (original == null)
        {
            return null;
        }
        if (original instanceof NewsItemCompactDTO)
        {
            return clone((NewsItemCompactDTO) original);
        }
        if (original instanceof AbstractDiscussionDTO)
        {
            return clone((AbstractDiscussionDTO) original);
        }

        throw new IllegalArgumentException("Unhandled type " + original.getClass());
    }

    protected NewsItemCompactDTO clone(NewsItemCompactDTO original)
    {
        if (original instanceof NewsItemDTO)
        {
            return new NewsItemDTO(original, NewsItemDTO.class);
        }
        return new NewsItemCompactDTO(original, NewsItemCompactDTO.class);
    }

    protected AbstractDiscussionDTO clone(AbstractDiscussionDTO original)
    {
        if (original instanceof TimelineItemDTO)
        {
            return new TimelineItemDTO(original, TimelineItemDTO.class);
        }
        if (original instanceof DiscussionDTO)
        {
            return clone((DiscussionDTO) original);
        }
        throw new IllegalArgumentException("Unhandled type " + original.getClass());
    }

    protected DiscussionDTO clone(DiscussionDTO original)
    {
        if (original instanceof PrivateDiscussionDTO)
        {
            return new PrivateDiscussionDTO(original, PrivateDiscussionDTO.class);
        }
        return new DiscussionDTO(original, DiscussionDTO.class);
    }

}
