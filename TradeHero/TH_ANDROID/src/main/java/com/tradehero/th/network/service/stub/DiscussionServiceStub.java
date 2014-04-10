package com.tradehero.th.network.service.stub;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOList;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.VoteDirection;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.pagination.RangeDTO;
import com.tradehero.th.api.pagination.RangeSequenceDTO;
import com.tradehero.th.api.pagination.RangedDTO;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import com.tradehero.th.network.service.DiscussionService;
import java.util.Map;
import javax.inject.Inject;

/**
 * Created by xavier2 on 2014/4/10.
 */
public class DiscussionServiceStub implements DiscussionService
{
    public static final int DEFAULT_MAX_COUNT = 5;

    @Inject public DiscussionServiceStub()
    {
        super();
    }

    @Override public DiscussionDTO getComment(int commentId)
    {
        DiscussionDTO discussionDTO = new DiscussionDTO();
        discussionDTO.id = commentId;
        discussionDTO.text = "discussion " + commentId;
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

    @Override public PaginatedDTO<DiscussionDTO> getDiscussions(
            DiscussionType inReplyToType,
            int inReplyToId,
            Map<String, Object> options)
    {
        return null;
    }

    @Override public RangedDTO<DiscussionDTO, DiscussionDTOList> getMessageThread(
            DiscussionType inReplyToType, int inReplyToId,
            Integer maxCount, Integer maxId, Integer minId)
    {
        maxCount = maxCount == null ? DEFAULT_MAX_COUNT : maxCount;
        RangedDTO<DiscussionDTO, DiscussionDTOList> rangedDTO = new RangedDTO<>();
        DiscussionDTOList data = new DiscussionDTOList();
        if (maxId != null)
        {
            maxId = addFromAboveDown(data, maxCount, maxId, minId);
        }
        else if (minId != null)
        {
            maxId = addFromBelowUp(data, maxCount, minId);
        }
        else
        {
            throw new IllegalArgumentException("Cannot find such stuff");
        }
        rangedDTO.setData(data);
        RangeSequenceDTO sequenceDTO = new RangeSequenceDTO();
        sequenceDTO.prev = new RangeDTO(maxCount, minId - 1, null);
        sequenceDTO.next = new RangeDTO(maxCount, null, maxId + 1);
        rangedDTO.setSequenceDTO(sequenceDTO);
        return rangedDTO;
    }

    private int addFromAboveDown(DiscussionDTOList data, int maxCount, int maxId, Integer minId)
    {
        minId = minId == null ? 0 : minId;
        while (maxCount-- > 0 && maxId >= minId)
        {
            data.add(getComment(maxId--));
        }
        return maxId;
    }

    private int addFromBelowUp(DiscussionDTOList data, int maxCount, int minId)
    {
        while (maxCount-- > 0)
        {
            data.add(getComment(minId++));
        }
        return minId;
    }

    @Override public RangedDTO<DiscussionDTO, DiscussionDTOList> getMessageThread(
            DiscussionType inReplyToType,
            int inReplyToId,
            Map<String, Object> options)
    {
        return null;
    }

    @Override public DiscussionDTO createDiscussion(DiscussionDTO discussionDTO)
    {
        return null;
    }

    @Override public DiscussionDTO vote(
            DiscussionType inReplyToType,
            int inReplyToId,
            VoteDirection direction)
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
