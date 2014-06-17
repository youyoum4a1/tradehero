package com.tradehero.th.network.service;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOFactory;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.form.MessageCreateFormDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.api.discussion.key.MessageHeaderUserId;
import com.tradehero.th.api.discussion.key.MessageListKey;
import com.tradehero.th.api.discussion.key.RecipientTypedMessageListKey;
import com.tradehero.th.api.discussion.key.TypedMessageListKey;
import com.tradehero.th.api.pagination.ReadablePaginatedDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.discussion.DTOProcessorDiscussionCreate;
import com.tradehero.th.models.discussion.DTOProcessorMessageDeleted;
import com.tradehero.th.models.discussion.DTOProcessorMessageRead;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.message.MessageHeaderCache;
import com.tradehero.th.persistence.message.MessageHeaderListCache;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.client.Response;

@Singleton
public class MessageServiceWrapper
{
    @NotNull private MessageService messageService;
    @NotNull private MessageServiceAsync messageServiceAsync;
    @NotNull private DiscussionDTOFactory discussionDTOFactory;

    // We need Lazy here because MessageStatusCache also injects a MessageServiceWrapper
    @NotNull private Lazy<MessageHeaderListCache> messageHeaderListCache;
    @NotNull private Lazy<MessageHeaderCache> messageHeaderCache;
    @NotNull private Lazy<UserMessagingRelationshipCache> userMessagingRelationshipCache;
    @NotNull private Lazy<DiscussionCache> discussionCache;
    @NotNull private Lazy<UserProfileCache> userProfileCache;

    @Inject MessageServiceWrapper(
            @NotNull MessageService messageService,
            @NotNull MessageServiceAsync messageServiceAsync,
            @NotNull DiscussionDTOFactory discussionDTOFactory,
            @NotNull Lazy<MessageHeaderListCache> messageHeaderListCache,
            @NotNull Lazy<MessageHeaderCache> messageHeaderCache,
            @NotNull Lazy<UserMessagingRelationshipCache> userMessagingRelationshipCache,
            @NotNull Lazy<DiscussionCache> discussionCache,
            @NotNull Lazy<UserProfileCache> userProfileCache)
    {
        this.messageService = messageService;
        this.messageServiceAsync = messageServiceAsync;
        this.discussionDTOFactory = discussionDTOFactory;
        this.messageHeaderListCache = messageHeaderListCache;
        this.messageHeaderCache = messageHeaderCache;
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
        this.discussionCache = discussionCache;
        this.userProfileCache = userProfileCache;
    }

    //<editor-fold desc="DTO Processors">
    protected DTOProcessor<DiscussionDTO> createDiscussionCreateProcessor(DiscussionKey stubKey)
    {
        return new DTOProcessorDiscussionCreate(
                discussionDTOFactory,
                discussionCache.get(),
                stubKey);
    }

    protected DTOProcessor<Response> createMessageHeaderReadProcessor(
            MessageHeaderId messageHeaderId,
            UserBaseKey readerId)
    {
        return new DTOProcessorMessageRead(messageHeaderCache.get(),
                userProfileCache.get(),
                messageHeaderId,
                readerId);
    }

    protected DTOProcessor<Response> createMessageHeaderDeletedProcessor(
            MessageHeaderId messageHeaderId,
            UserBaseKey readerId)
    {
        return new DTOProcessorMessageDeleted(
                messageHeaderCache.get(),
                userProfileCache.get(),
                messageHeaderListCache.get(),
                messageHeaderId,
                readerId);
    }
    //</editor-fold>

    //<editor-fold desc="Get Message Headers">
    public ReadablePaginatedDTO<MessageHeaderDTO> getMessageHeaders(MessageListKey messageListKey)
    {
        if (messageListKey instanceof TypedMessageListKey)
        {
            return getMessageHeaders((TypedMessageListKey) messageListKey);
        }
        return messageService.getMessageHeaders(
                messageListKey.page,
                messageListKey.perPage);
    }

    public ReadablePaginatedDTO<MessageHeaderDTO> getMessageHeaders(TypedMessageListKey messageListKey)
    {
        if (messageListKey instanceof RecipientTypedMessageListKey)
        {
            return getMessageHeaders((RecipientTypedMessageListKey) messageListKey);
        }
        return messageService.getMessageHeaders(
                messageListKey.discussionType.description,
                null,
                messageListKey.page,
                messageListKey.perPage);
    }

    public ReadablePaginatedDTO<MessageHeaderDTO> getMessageHeaders(
            RecipientTypedMessageListKey messageListKey)
    {
        return messageService.getMessageHeaders(
                messageListKey.discussionType.description,
                messageListKey.recipientId.key,
                messageListKey.page,
                messageListKey.perPage);
    }

    public MiddleCallback<ReadablePaginatedDTO<MessageHeaderDTO>> getMessageHeaders(MessageListKey messageListKey,
            Callback<ReadablePaginatedDTO<MessageHeaderDTO>> callback)
    {
        if (messageListKey instanceof TypedMessageListKey)
        {
            return getMessageHeaders((TypedMessageListKey) messageListKey, callback);
        }
        MiddleCallback<ReadablePaginatedDTO<MessageHeaderDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        messageServiceAsync.getMessageHeaders(
                messageListKey.page,
                messageListKey.perPage,
                middleCallback);
        return middleCallback;
    }

    public MiddleCallback<ReadablePaginatedDTO<MessageHeaderDTO>> getMessageHeaders(
            TypedMessageListKey messageListKey, Callback<ReadablePaginatedDTO<MessageHeaderDTO>> callback)
    {
        if (messageListKey instanceof RecipientTypedMessageListKey)
        {
            return getMessageHeaders((RecipientTypedMessageListKey) messageListKey, callback);
        }
        MiddleCallback<ReadablePaginatedDTO<MessageHeaderDTO>> middleCallback = new BaseMiddleCallback<>(callback);
        messageServiceAsync.getMessageHeaders(
                messageListKey.discussionType.description,
                null,
                messageListKey.page,
                messageListKey.perPage,
                middleCallback);
        return middleCallback;
    }

    public MiddleCallback<ReadablePaginatedDTO<MessageHeaderDTO>> getMessageHeaders(
            RecipientTypedMessageListKey messageListKey,
            Callback<ReadablePaginatedDTO<MessageHeaderDTO>> callback)
    {
        MiddleCallback<ReadablePaginatedDTO<MessageHeaderDTO>> middleCallback = new BaseMiddleCallback(callback);
        messageServiceAsync.getMessageHeaders(
                messageListKey.discussionType.description,
                messageListKey.recipientId.key,
                messageListKey.page,
                messageListKey.perPage,
                middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get Message Header">
    public MessageHeaderDTO getMessageHeader(MessageHeaderId messageHeaderId)
    {
        if (messageHeaderId instanceof MessageHeaderUserId && ((MessageHeaderUserId) messageHeaderId).userBaseKey != null)
        {
            return messageService.getMessageHeader(
                    messageHeaderId.commentId,
                    ((MessageHeaderUserId) messageHeaderId).userBaseKey.key);
        }
        return messageService.getMessageHeader(messageHeaderId.commentId, null);
    }

    public MiddleCallback<MessageHeaderDTO> getMessageHeader(MessageHeaderId messageHeaderId, Callback<MessageHeaderDTO> callback)
    {
        MiddleCallback<MessageHeaderDTO> middleCallback = new BaseMiddleCallback<>(callback);
        if (messageHeaderId instanceof MessageHeaderUserId)
        {
            messageServiceAsync.getMessageHeader(
                    messageHeaderId.commentId,
                    ((MessageHeaderUserId) messageHeaderId).userBaseKey.key,
                    middleCallback);
        }
        else
        {
            messageServiceAsync.getMessageHeader(messageHeaderId.commentId, null, middleCallback);
        }
        return middleCallback;
    }

    public MessageHeaderDTO getMessageThread(UserBaseKey correspondentId)
    {
        return messageService.getMessageThread(correspondentId.key);
    }

    public MiddleCallback<MessageHeaderDTO> getMessageThread(UserBaseKey correspondentId, Callback<MessageHeaderDTO> callback)
    {
        MiddleCallback<MessageHeaderDTO> middleCallback = new BaseMiddleCallback<>(callback);
        messageServiceAsync.getMessageThread(correspondentId.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get Messaging Relationship Status">
    public UserMessagingRelationshipDTO getMessagingRelationgshipStatus(UserBaseKey recipient)
    {
        return messageService.getMessagingRelationgshipStatus(recipient.key);
    }

    public MiddleCallback<UserMessagingRelationshipDTO> getMessagingRelationgshipStatus(
            UserBaseKey recipient,
            Callback<UserMessagingRelationshipDTO> callback)
    {
        MiddleCallback<UserMessagingRelationshipDTO>
                middleCallback = new BaseMiddleCallback<>(callback);
        messageServiceAsync.getMessagingRelationshipStatus(recipient.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Create Message">
    public DiscussionDTO createMessage(MessageCreateFormDTO form)
    {
        DiscussionDTO discussionDTO = messageService.createMessage(form);
        if (discussionDTO != null)
        {
            userMessagingRelationshipCache.get().invalidate(new UserBaseKey(discussionDTO.userId));
        }
        return discussionDTO;
    }

    public MiddleCallback<DiscussionDTO> createMessage(MessageCreateFormDTO form, Callback<DiscussionDTO> callback)
    {
        MiddleCallback<DiscussionDTO> middleCallback = new BaseMiddleCallback<>(
                callback,
                createDiscussionCreateProcessor(null));
        messageServiceAsync.createMessage(form, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Delete Message">
    public Response deleteMessage(
            MessageHeaderId messageHeaderId,
            int senderUserId,
            int recipientUserId,
            UserBaseKey readerId)
    {
        return createMessageHeaderDeletedProcessor(messageHeaderId, readerId).process(
                messageService.deleteMessage(messageHeaderId.commentId, senderUserId, recipientUserId));
    }

    public MiddleCallback<Response> deleteMessage(
            final MessageHeaderId messageHeaderId,
            int senderUserId,
            int recipientUserId,
            UserBaseKey readerId,
            Callback<Response> callback)
    {
        MiddleCallback<Response> middleCallback = new BaseMiddleCallback<>(
                callback,
                createMessageHeaderDeletedProcessor(messageHeaderId, readerId));
        messageServiceAsync.deleteMessage(messageHeaderId.commentId, senderUserId, recipientUserId, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Read Message">
    public Response readMessage(
            int commentId,
            int senderUserId,
            int recipientUserId,
            MessageHeaderId messageHeaderId,
            UserBaseKey readerId)
    {
        return createMessageHeaderReadProcessor(messageHeaderId, readerId).process(
                messageService.readMessage(commentId, senderUserId, recipientUserId));
    }

    public MiddleCallback<Response> readMessage(
            int commentId,
            int senderUserId,
            int recipientUserId,
            final MessageHeaderId messageHeaderId,
            final UserBaseKey readerId,
            Callback<Response> callback)
    {
        MiddleCallback<Response> middleCallback = new BaseMiddleCallback<>(
                callback,
                createMessageHeaderReadProcessor(messageHeaderId, readerId));
        messageServiceAsync.readMessage(commentId, senderUserId, recipientUserId, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
