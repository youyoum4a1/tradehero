package com.tradehero.th.fragments.discussion;

import android.content.Context;
import com.tradehero.th.api.PaginatedDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.PagedDiscussionKey;
import com.tradehero.th.api.timeline.TimelineItemDTOKey;
import com.tradehero.th.loaders.PaginationLoader;
import com.tradehero.th.network.service.DiscussionService;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/12/14 Time: 5:48 PM Copyright (c) TradeHero
 */
public class CommentListLoader extends PaginationLoader<DiscussionDTO>
{
    private static final String TYPE = "timelineitem";
    private final TimelineItemDTOKey timelineItemKey;
    private PagedDiscussionKey pagedCommentKey;

    @Inject DiscussionService discussionService;

    public CommentListLoader(Context context, TimelineItemDTOKey timelineItemKey)
    {
        super(context);
        this.timelineItemKey = timelineItemKey;

        DaggerUtils.inject(this);
    }

    @Override protected void onLoadNext(DiscussionDTO endItem)
    {
        if (endItem == null)
        {
            return;
        }

        //lowerItemId = endItem.id;
        forceLoad();
    }

    @Override protected void onLoadPrevious(DiscussionDTO startItem)
    {
    }

    @Override public PaginatedDTO<DiscussionDTO> loadInBackground()
    {
        return discussionService.getDiscussions(TYPE, timelineItemKey.key, pagedCommentKey.getPage(), 42);
    }

    private void resetQuery()
    {
        //this.pagedCommentKey = this.pagedCommentKey.cloneAtPage(1);
        //if (items != null)
        //{
        //    items.clear();
        //}
    }
}
