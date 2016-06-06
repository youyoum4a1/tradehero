package com.androidth.general.network.service;

import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.discussion.DiscussionDTO;
import com.androidth.general.api.discussion.DiscussionType;
import com.androidth.general.api.discussion.MessageHeaderDTO;
import com.androidth.general.api.discussion.ReadablePaginatedMessageHeaderDTO;
import com.androidth.general.api.discussion.form.MessageCreateFormDTO;
import com.androidth.general.api.pagination.PaginationInfoDTO;
import com.androidth.general.api.users.UserMessagingRelationshipDTO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import timber.log.Timber;

public class MessageServiceRxStub implements MessageServiceRx
{
    //<editor-fold desc="Constructors">
    @Inject public MessageServiceRxStub()
    {
        super();
    }
    //</editor-fold>

    @Override
    public Observable<ReadablePaginatedMessageHeaderDTO> getMessageHeaders(Integer page, Integer perPage)
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

        return Observable.just(paginatedDTO);
    }

    @Override public Observable<ReadablePaginatedMessageHeaderDTO> getMessageHeaders(
            String discussionType,
            Integer senderId,
            Integer page,
            Integer perPage)
    {
        ReadablePaginatedMessageHeaderDTO paginatedDTO = new ReadablePaginatedMessageHeaderDTO();
        List<MessageHeaderDTO> data = new ArrayList<>();
        data.add(createMessageHeaderNeerajToOscarAguilar());
        paginatedDTO.setData(data);
        return Observable.just(paginatedDTO);
    }

    @Override public Observable<MessageHeaderDTO> getMessageHeader(int commentId, Integer referencedUserId)
    {
        return Observable.just(createMessageHeader(commentId, referencedUserId, new Date()));
    }

    @Override public Observable<MessageHeaderDTO> getMessageThread(int correspondentId)
    {
        return Observable.empty();
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

    @Override public Observable<UserMessagingRelationshipDTO> getMessagingRelationgshipStatus(int recipientUserId)
    {
        UserMessagingRelationshipDTO statusDTO = new UserMessagingRelationshipDTO();
        return Observable.just(statusDTO);
    }

    @Override public Observable<DiscussionDTO> createMessage(MessageCreateFormDTO form)
    {
        throw new IllegalArgumentException("Implement it");
    }

    @Override public Observable<BaseResponseDTO> deleteMessage(int commentId, int senderUserId,
            int recipientUserId)
    {
        throw new RuntimeException("Not implemented");
    }

    @Override public Observable<BaseResponseDTO> readMessage(int commentId, int senderUserId,
            int recipientUserId)
    {
        throw new RuntimeException("Not implemented");
    }

    @Override public Observable<BaseResponseDTO> readAllMessage()
    {
        throw new RuntimeException("Not implemented");
    }
}
