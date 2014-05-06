package com.tradehero.th.network.service.stub;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.form.MessageCreateFormDTO;
import com.tradehero.th.api.pagination.PaginationInfoDTO;
import com.tradehero.th.api.pagination.ReadablePaginatedDTO;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import com.tradehero.th.network.service.MessageService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import retrofit.client.Response;
import retrofit.http.Path;
import timber.log.Timber;

public class MessageServiceStub implements MessageService
{
    @Inject public MessageServiceStub()
    {
        super();
    }


    @Override
    public ReadablePaginatedDTO<MessageHeaderDTO> getMessageHeaders(Integer page, Integer perPage)
    {
        Timber.d("Returning stub messages");
        ReadablePaginatedDTO<MessageHeaderDTO> paginatedDTO = new ReadablePaginatedDTO<>();
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

    @Override public ReadablePaginatedDTO<MessageHeaderDTO> getMessageHeaders(
            String discussionType,
            Integer senderId,
            Integer page,
            Integer perPage)
    {
        ReadablePaginatedDTO<MessageHeaderDTO> paginatedDTO = new ReadablePaginatedDTO<>();
        List<MessageHeaderDTO> data = new ArrayList<>();
        data.add(createMessageHeaderNeerajToOscarAguilar());
        paginatedDTO.setData(data);
        return paginatedDTO;
    }

    @Override public MessageHeaderDTO getMessageHeader(int commentId)
    {
        return createMessageHeader(commentId, null, new Date());
    }

    @Override public MessageHeaderDTO getMessageThread(@Path("correspondentId") int correspondentId)
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

    @Override public DiscussionDTO createMessage(MessageCreateFormDTO form)
    {
        throw new IllegalArgumentException("Implement it");
    }

    @Override public Response deleteMessage(@Path("commentId") int commentId, @Path("senderUserId") int senderUserId,
            @Path("recipientUserId") int recipientUserId)
    {
        return null;
    }

    @Override public Response readMessage(@Path("commentId") int commentId, @Path("senderUserId") int senderUserId,
            @Path("recipientUserId") int recipientUserId)
    {
        throw new RuntimeException("Not implemented");
    }
}
