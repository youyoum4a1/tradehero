package com.tradehero.th.fragments.discussion;

import android.content.Context;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.pagination.PaginationDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.loaders.PaginatedLoader;
import com.tradehero.th.network.service.DiscussionService;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DaggerUtils;
import java.util.List;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/12/14 Time: 5:48 PM Copyright (c) TradeHero
 */
public class DiscussionListLoader extends PaginatedLoader<DiscussionDTO>
{
    private final int timelineId;
    private final DiscussionType discussionType;

    @Inject DiscussionService discussionService;

    private PaginatedDTO<DiscussionDTO> currentPaginatedDiscussion;

    public DiscussionListLoader(Context context, DiscussionType discussionType, int timelineId)
    {
        super(context);
        this.timelineId = timelineId;
        this.discussionType = discussionType;

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

    @Override public List<DiscussionDTO> loadInBackground()
    {
        PaginatedDTO<DiscussionDTO> discussions = getPaginatedDTO();
        return discussions.getData();
    }

    private void resetQuery()
    {
        //this.pagedCommentKey = this.pagedCommentKey.cloneAtPage(1);
        //if (items != null)
        //{
        //    items.clear();
        //}
    }

    @Override protected PaginatedDTO<DiscussionDTO> getPaginatedDTO()
    {
        PaginationDTO paginationDTO = null;
        if (currentPaginatedDiscussion != null && currentPaginatedDiscussion.getPagination() != null)
        {
            switch (getLoadMode())
            {
                case NEXT:
                    paginationDTO = currentPaginatedDiscussion.getPagination().next;
                    break;

                case PREVIOUS:
                    paginationDTO = currentPaginatedDiscussion.getPagination().prev;
                    break;
            }
        }

        if (paginationDTO == null)
        {
            paginationDTO = new PaginationDTO();
            paginationDTO.page = 1;
            paginationDTO.perPage = Constants.TIMELINE_ITEM_PER_PAGE;
        }

        currentPaginatedDiscussion = discussionService.getDiscussions(discussionType, timelineId, paginationDTO.page, paginationDTO.perPage);

        return currentPaginatedDiscussion;
    }
}
