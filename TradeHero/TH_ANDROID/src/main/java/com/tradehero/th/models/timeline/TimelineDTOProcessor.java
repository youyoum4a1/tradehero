package com.tradehero.th.models.timeline;

import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import rx.functions.Action1;

/**
 * Created by thonguyen on 27/10/14.
 */
public class TimelineDTOProcessor implements Action1<TimelineDTO>
{
    private final DiscussionCache discussionCache;

    @Inject public TimelineDTOProcessor(DiscussionCache discussionCache)
    {
        this.discussionCache = discussionCache;
    }

    @Override public void call(@NotNull TimelineDTO timelineDTO)
    {
        List<TimelineItemDTO> timelineItemList = timelineDTO.getEnhancedItems();
        if (timelineItemList != null)
        {
            for (TimelineItemDTO timelineItemDTO: timelineItemList)
            {
                discussionCache.put(timelineItemDTO.getDiscussionKey(), timelineItemDTO);
            }
        }
    }
}
