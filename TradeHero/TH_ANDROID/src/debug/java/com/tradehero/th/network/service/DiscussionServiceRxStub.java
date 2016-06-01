package com.ayondo.academy.network.service;

import com.ayondo.academy.api.BaseResponseDTO;
import com.ayondo.academy.api.discussion.DiscussionDTO;
import com.ayondo.academy.api.discussion.DiscussionDTOList;
import com.ayondo.academy.api.discussion.DiscussionType;
import com.ayondo.academy.api.discussion.VoteDirection;
import com.ayondo.academy.api.discussion.form.DiscussionFormDTO;
import com.ayondo.academy.api.pagination.PaginatedDTO;
import com.ayondo.academy.api.pagination.RangeDTO;
import com.ayondo.academy.api.pagination.RangeSequenceDTO;
import com.ayondo.academy.api.timeline.TimelineItemShareRequestDTO;
import com.ayondo.academy.api.users.CurrentUserId;
import java.util.Map;
import javax.inject.Inject;
import retrofit.http.Body;
import retrofit.http.Path;
import rx.Observable;

public class DiscussionServiceRxStub implements DiscussionServiceRx
{
    public static final int DEFAULT_MAX_COUNT = 5;

    private final CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    @Inject public DiscussionServiceRxStub(CurrentUserId currentUserId)
    {
        super();
        this.currentUserId = currentUserId;
    }
    //</editor-fold>

    @Override public Observable<DiscussionDTO> getComment(int commentId)
    {
        DiscussionDTO discussionDTO = new DiscussionDTO();
        discussionDTO.id = commentId;
        discussionDTO.text = "discussion " + commentId;
        discussionDTO.userId = (commentId % 2 == 0) ? currentUserId.toUserBaseKey().key : 23;
        return Observable.just(discussionDTO);
    }

    @Override public Observable<PaginatedDTO<DiscussionDTO>> getDiscussions(
            DiscussionType inReplyToType,
            int inReplyToId,
            Integer page,
            Integer perPage)
    {
        return Observable.empty();
    }

    @Override public Observable<PaginatedDTO<DiscussionDTO>> getMessageThread(
            DiscussionType inReplyToType, int inReplyToId,
            int senderUserId, int recipientUserId,
            Integer maxCount, Integer maxId, Integer minId)
    {
        maxCount = maxCount == null ? DEFAULT_MAX_COUNT : maxCount;
        PaginatedDTO<DiscussionDTO> rangedDTO = new PaginatedDTO<>();
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
            return Observable.error(new IllegalArgumentException("Cannot find such stuff"));
        }
        rangedDTO.setData(data);
        RangeSequenceDTO sequenceDTO = new RangeSequenceDTO();
        sequenceDTO.prev = new RangeDTO(maxCount, minId - 1, null);
        sequenceDTO.next = new RangeDTO(maxCount, null, maxId + 1);
        //rangedDTO.setSequenceDTO(sequenceDTO);
        return Observable.just(rangedDTO);
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

    @Override public Observable<PaginatedDTO<DiscussionDTO>> getMessageThread(
            DiscussionType inReplyToType,
            int inReplyToId,
            Map<String, Object> options)
    {
        return Observable.empty();
    }

    @Override public Observable<DiscussionDTO> createDiscussion(DiscussionFormDTO discussionFormDTO)
    {
        return Observable.empty();
    }

    @Override public Observable<DiscussionDTO> vote(
            DiscussionType inReplyToType,
            int inReplyToId,
            VoteDirection direction)
    {
        return Observable.empty();
    }

    @Override public Observable<BaseResponseDTO> share(
            DiscussionType inReplyToType,
            int inReplyToId,
            TimelineItemShareRequestDTO timelineItemShareRequestDTO)
    {
        return Observable.empty();
    }

    @Override public Observable<DiscussionDTO> postToTimeline(@Path("userId") int userId, @Body DiscussionFormDTO discussionFormDTO)
    {
        return Observable.empty();
    }
}
