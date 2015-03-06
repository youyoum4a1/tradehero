package com.tradehero.th.network.service.stub;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.network.service.DiscussionService;

import javax.inject.Inject;
import java.util.Map;

public class DiscussionServiceStub implements DiscussionService
{
    public static final int DEFAULT_MAX_COUNT = 5;

    private final CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    @Inject public DiscussionServiceStub(CurrentUserId currentUserId)
    {
        super();
        this.currentUserId = currentUserId;
    }
    //</editor-fold>

    @Override public DiscussionDTO getComment(int commentId)
    {
        DiscussionDTO discussionDTO = new DiscussionDTO();
        discussionDTO.id = commentId;
        discussionDTO.text = "discussion " + commentId;
        discussionDTO.userId = (commentId % 2 == 0) ? currentUserId.toUserBaseKey().key : 23;
        return discussionDTO;
    }

    @Override public PaginatedDTO<DiscussionDTO> getDiscussions(
            DiscussionType inReplyToType,
            int inReplyToId,
            Integer page,
            Integer perPage)
    {
        return null;
    }

    @Override public PaginatedDTO<DiscussionDTO> getMessageThread(
            DiscussionType inReplyToType,
            int inReplyToId,
            Map<String, Object> options)
    {
        return null;
    }


    @Override public DiscussionDTO share(
            DiscussionType inReplyToType,
            int inReplyToId,
            TimelineItemShareRequestDTO timelineItemShareRequestDTO)
    {
        return null;
    }
}
