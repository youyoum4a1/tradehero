package com.tradehero.th.network.service.stub;

import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.MessageStatusDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.pagination.PaginationInfoDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.network.service.MessageService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * Created by xavier2 on 2014/4/9.
 */
public class MessageServiceStub implements MessageService
{
    @Inject public MessageServiceStub()
    {
        super();
    }

    @Override public PaginatedDTO<MessageHeaderDTO> getMessages(Integer page, Integer perPage)
    {
        Timber.d("Returning stub messages");
        PaginatedDTO<MessageHeaderDTO> paginatedDTO = new PaginatedDTO<>();
        List<MessageHeaderDTO> messsageDTOList = new ArrayList<>();
        Date date = new Date();
        for (int i = 0; i < perPage; i++)
        {
            messsageDTOList.add(createMessageHeader(i, page, date));
        }

        paginatedDTO.setData(messsageDTOList);

        PaginationInfoDTO paginationInfoDTO = new PaginationInfoDTO();
        paginatedDTO.setPagination(paginationInfoDTO);

        return paginatedDTO;
    }

    @Override public PaginatedDTO<MessageHeaderDTO> getMessages(
            DiscussionType discussionType,
            Integer senderId,
            Integer page,
            Integer perPage)
    {
        PaginatedDTO<MessageHeaderDTO> paginatedDTO = new PaginatedDTO<>();
        List<MessageHeaderDTO> data = new ArrayList<>();
        data.add(getMessageHeader(2));
        paginatedDTO.setData(data);
        return paginatedDTO;
    }

    @Override public MessageHeaderDTO getMessageHeader(int commentId)
    {
        return createMessageHeader(commentId, null, new Date());
    }

    private MessageHeaderDTO createMessageHeader(int commentId, Integer page, Date date)
    {
        return new MessageHeaderDTO("title-" + commentId + "-" + page, "subtitle-" + commentId, "text-" + commentId, date);
    }

    @Override public MessageStatusDTO getFreeCount(int recipientUserId)
    {
        MessageStatusDTO statusDTO = new MessageStatusDTO();
        statusDTO.recipientUserId = recipientUserId;
        statusDTO.privateFreeRemainingCount = 1;
        return statusDTO;
    }

    @Override public DiscussionDTO createMessage(MessageHeaderDTO form)
    {
        throw new IllegalArgumentException("Implement it");
    }
}
