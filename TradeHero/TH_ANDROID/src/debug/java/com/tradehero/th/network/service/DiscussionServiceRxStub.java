package com.tradehero.th.network.service;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOList;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.VoteDirection;
import com.tradehero.th.api.discussion.form.DiscussionFormDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.pagination.RangeDTO;
import com.tradehero.th.api.pagination.RangeSequenceDTO;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import com.tradehero.th.api.users.CurrentUserId;
import java.util.ArrayList;
import java.util.List;
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

    @Override public Observable<PaginatedDTO<DiscussionDTO>> getDiscussions(
            DiscussionType inReplyToType,
            int inReplyToId,
            Map<String, Object> options)
    {
        PaginatedDTO<DiscussionDTO> paginatedDTO = new PaginatedDTO<>();

        List<DiscussionDTO> discussionDTOs = new ArrayList<>();

        for (int i = 0; i < 10; ++i)
        {
            DiscussionDTO discussionDTO = new DiscussionDTO();
            discussionDTO.id = i;
            discussionDTO.text = inReplyToType.description + ": asd asd asd asd asd asd asd asd asd asd asd asd asd asd asd asd asd asd asd " + i;
            discussionDTO.userId = (i % 2 == 0) ? currentUserId.toUserBaseKey().key : 23;
            discussionDTOs.add(discussionDTO);
        }

        paginatedDTO.setData(discussionDTOs);

        return Observable.just(paginatedDTO);
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
