package com.tradehero.th.network.service.stub;

import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.ReadablePaginatedMessageHeaderDTO;
import com.tradehero.th.api.pagination.PaginationInfoDTO;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import com.tradehero.th.network.service.MessageService;
import retrofit.client.Response;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageServiceStub implements MessageService
{
    @Inject public MessageServiceStub()
    {
        super();
    }

    @Override
    public ReadablePaginatedMessageHeaderDTO getMessageHeaders(Integer page, Integer perPage)
    {
        Timber.d("Returning stub messages");
        ReadablePaginatedMessageHeaderDTO paginatedDTO = new ReadablePaginatedMessageHeaderDTO();
        List<MessageHeaderDTO> messageDTOList = new ArrayList<>();
        Date date = new Date();
        for (int i = 0; i < perPage; i++)
        {
            messageDTOList.add(createMessageHeader(i, page, date));
        }

        paginatedDTO.setData(messageDTOList);

        PaginationInfoDTO paginationInfoDTO = new PaginationInfoDTO();
        paginatedDTO.setPagination(paginationInfoDTO);

        return paginatedDTO;
    }

    @Override public ReadablePaginatedMessageHeaderDTO getMessageHeaders(
            String discussionType,
            Integer senderId,
            Integer page,
            Integer perPage)
    {
        ReadablePaginatedMessageHeaderDTO paginatedDTO = new ReadablePaginatedMessageHeaderDTO();
        List<MessageHeaderDTO> data = new ArrayList<>();
        data.add(createMessageHeaderNeerajToOscarAguilar());
        paginatedDTO.setData(data);
        return paginatedDTO;
    }

    @Override public MessageHeaderDTO getMessageHeader(int commentId, Integer referencedUserId)
    {
        return createMessageHeader(commentId, referencedUserId, new Date());
    }

    @Override public MessageHeaderDTO getMessageThread(int correspondentId)
    {
        return null;
    }

    private MessageHeaderDTO createMessageHeaderNeerajToOscarAguilar()
    {
        MessageHeaderDTO messageHeader = new MessageHeaderDTO();
        messageHeader.id = 1192391;
        messageHeader.discussionType = DiscussionType.PRIVATE_MESSAGE;
        messageHeader.message = "doom";
        messageHeader.senderUserId = 239284;
        messageHeader.recipientUserId = 106711;
        //messageHeader.createdAtUtc = new Date("2014-04-11T12:33:50");
        return messageHeader;
    }

    private MessageHeaderDTO createMessageHeader(int commentId, Integer page, Date date)
    {
        return new MessageHeaderDTO("title-" + commentId + "-" + page, "subtitle-" + commentId, "text-" + commentId, date, true);
    }

    @Override public UserMessagingRelationshipDTO getMessagingRelationgshipStatus(int recipientUserId)
    {
        UserMessagingRelationshipDTO statusDTO = new UserMessagingRelationshipDTO();
        return statusDTO;
    }

    @Override public Response readAllMessage()
    {
        throw new RuntimeException("Not implemented");
    }
}
