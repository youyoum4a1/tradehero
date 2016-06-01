package com.ayondo.academy.models.timeline;

import android.support.annotation.NonNull;
import com.android.internal.util.Predicate;
import com.tradehero.common.utils.CollectionUtils;
import com.ayondo.academy.api.timeline.TimelineDTO;
import com.ayondo.academy.api.timeline.TimelineItemDTO;
import com.ayondo.academy.api.users.UserProfileCompactDTO;
import com.ayondo.academy.persistence.discussion.DiscussionCacheRx;
import java.util.List;
import javax.inject.Inject;
import rx.functions.Action1;

public class TimelineDTOProcessor implements Action1<TimelineDTO>
{
    private final DiscussionCacheRx discussionCache;

    @Inject public TimelineDTOProcessor(DiscussionCacheRx discussionCache)
    {
        this.discussionCache = discussionCache;
    }

    @Override public void call(@NonNull TimelineDTO timelineDTO)
    {
        List<TimelineItemDTO> timelineItemList = timelineDTO.getEnhancedItems();
        if (timelineItemList != null)
        {
            for (final TimelineItemDTO timelineItemDTO: timelineItemList)
            {
                timelineItemDTO.setUser(CollectionUtils.first(timelineDTO.getUsers(), new Predicate<UserProfileCompactDTO>()
                {
                    @Override public boolean apply(UserProfileCompactDTO userProfileCompactDTO)
                    {
                        return timelineItemDTO.userId == userProfileCompactDTO.id;
                    }
                }));
                discussionCache.onNext(timelineItemDTO.getDiscussionKey(), timelineItemDTO);
            }
        }
    }
}
