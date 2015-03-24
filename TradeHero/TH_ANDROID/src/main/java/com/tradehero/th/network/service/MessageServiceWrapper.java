package com.tradehero.th.network.service;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOFactory;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.ReadablePaginatedMessageHeaderDTO;
import com.tradehero.th.api.discussion.form.MessageCreateFormDTO;
import com.tradehero.th.api.discussion.key.*;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.discussion.*;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.message.MessageHeaderCache;
import com.tradehero.th.persistence.message.MessageHeaderListCache;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.client.Response;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MessageServiceWrapper
{
    @NotNull private final MessageService messageService;
    @NotNull private final MessageServiceAsync messageServiceAsync;
    @NotNull private final DiscussionDTOFactory discussionDTOFactory;
    @NotNull private final CurrentUserId currentUserId;

    // We need Lazy here because MessageStatusCache also injects a MessageServiceWrapper
    @NotNull private final Lazy<MessageHeaderListCache> messageHeaderListCache;
    @NotNull private final Lazy<MessageHeaderCache> messageHeaderCache;
    @NotNull private final Lazy<UserMessagingRelationshipCache> userMessagingRelationshipCache;
    @NotNull private final Lazy<DiscussionCache> discussionCache;
    @NotNull private final Lazy<UserProfileCache> userProfileCache;

    //<editor-fold desc="Constructors">
    @Inject MessageServiceWrapper(
            @NotNull MessageService messageService,
            @NotNull MessageServiceAsync messageServiceAsync,
            @NotNull DiscussionDTOFactory discussionDTOFactory,
            @NotNull CurrentUserId currentUserId,
            @NotNull Lazy<MessageHeaderListCache> messageHeaderListCache,
            @NotNull Lazy<MessageHeaderCache> messageHeaderCache,
            @NotNull Lazy<UserMessagingRelationshipCache> userMessagingRelationshipCache,
            @NotNull Lazy<DiscussionCache> discussionCache,
            @NotNull Lazy<UserProfileCache> userProfileCache)
    {
        this.messageService = messageService;
        this.messageServiceAsync = messageServiceAsync;
        this.discussionDTOFactory = discussionDTOFactory;
        this.currentUserId = currentUserId;
        this.messageHeaderListCache = messageHeaderListCache;
        this.messageHeaderCache = messageHeaderCache;
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
        this.discussionCache = discussionCache;
        this.userProfileCache = userProfileCache;
    }
    //</editor-fold>

    //<editor-fold desc="Get Message Headers">
    protected DTOProcessor<ReadablePaginatedMessageHeaderDTO> createReadablePaginatedMessageHeaderReceivedProcessor()
    {
        return new DTOProcessorReadablePaginatedMessageReceived<>(userProfileCache.get(), currentUserId.toUserBaseKey());
    }

    public ReadablePaginatedMessageHeaderDTO getMessageHeaders(MessageListKey messageListKey)
    {
        if (messageListKey instanceof TypedMessageListKey)
        {
            return getMessageHeaders((TypedMessageListKey) messageListKey);
        }
        return createReadablePaginatedMessageHeaderReceivedProcessor().process(
                messageService.getMessageHeaders(
                        messageListKey.page,
                        messageListKey.perPage));
    }

    public ReadablePaginatedMessageHeaderDTO getMessageHeaders(TypedMessageListKey messageListKey)
    {
        if (messageListKey instanceof RecipientTypedMessageListKey)
        {
            return getMessageHeaders((RecipientTypedMessageListKey) messageListKey);
        }
        return createReadablePaginatedMessageHeaderReceivedProcessor().process(
                messageService.getMessageHeaders(
                        messageListKey.discussionType.description,
                        null,
                        messageListKey.page,
                        messageListKey.perPage));
    }

    public ReadablePaginatedMessageHeaderDTO getMessageHeaders(
            RecipientTypedMessageListKey messageListKey)
    {
        return createReadablePaginatedMessageHeaderReceivedProcessor().process(
                messageService.getMessageHeaders(
                        messageListKey.discussionType.description,
                        messageListKey.recipientId.key,
                        messageListKey.page,
                        messageListKey.perPage));
    }

    public MiddleCallback<ReadablePaginatedMessageHeaderDTO> getMessageHeaders(MessageListKey messageListKey,
            Callback<ReadablePaginatedMessageHeaderDTO> callback)
    {
        if (messageListKey instanceof TypedMessageListKey)
        {
            return getMessageHeaders((TypedMessageListKey) messageListKey, callback);
        }
        MiddleCallback<ReadablePaginatedMessageHeaderDTO> middleCallback = new BaseMiddleCallback<>(
                callback,
                createReadablePaginatedMessageHeaderReceivedProcessor());
        messageServiceAsync.getMessageHeaders(
                messageListKey.page,
                messageListKey.perPage,
                middleCallback);
        return middleCallback;
    }

    public MiddleCallback<ReadablePaginatedMessageHeaderDTO> getMessageHeaders(
            TypedMessageListKey messageListKey, Callback<ReadablePaginatedMessageHeaderDTO> callback)
    {
        if (messageListKey instanceof RecipientTypedMessageListKey)
        {
            return getMessageHeaders((RecipientTypedMessageListKey) messageListKey, callback);
        }
        MiddleCallback<ReadablePaginatedMessageHeaderDTO> middleCallback = new BaseMiddleCallback<>(
                callback,
                createReadablePaginatedMessageHeaderReceivedProcessor());
        messageServiceAsync.getMessageHeaders(
                messageListKey.discussionType.description,
                null,
                messageListKey.page,
                messageListKey.perPage,
                middleCallback);
        return middleCallback;
    }

    public MiddleCallback<ReadablePaginatedMessageHeaderDTO> getMessageHeaders(
            RecipientTypedMessageListKey messageListKey,
            Callback<ReadablePaginatedMessageHeaderDTO> callback)
    {
        MiddleCallback<ReadablePaginatedMessageHeaderDTO> middleCallback = new BaseMiddleCallback<>(
                callback,
                createReadablePaginatedMessageHeaderReceivedProcessor());
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

    public MessageHeaderDTO getMessageThread(UserBaseKey correspondentId)
    {
        return messageService.getMessageThread(correspondentId.key);
    }

    //<editor-fold desc="Get Messaging Relationship Status">
    public UserMessagingRelationshipDTO getMessagingRelationgshipStatus(UserBaseKey recipient)
    {
        return messageService.getMessagingRelationgshipStatus(recipient.key);
    }

    //<editor-fold desc="Create Message">
    protected DTOProcessor<DiscussionDTO> createDiscussionCreateProcessor(DiscussionKey stubKey)
    {
        return new DTOProcessorDiscussionCreate(
                discussionDTOFactory,
                discussionCache.get(),
                userMessagingRelationshipCache.get(),
                stubKey);
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
    protected DTOProcessor<Response> createMessageHeaderDeletedProcessor(
            @NotNull MessageHeaderId messageHeaderId,
            @NotNull UserBaseKey readerId)
    {
        return new DTOProcessorMessageDeleted(
                messageHeaderCache.get(),
                userProfileCache.get(),
                messageHeaderListCache.get(),
                messageHeaderId,
                readerId);
    }

    public MiddleCallback<Response> deleteMessage(
            @NotNull final MessageHeaderId messageHeaderId,
            int senderUserId,
            int recipientUserId,
            @NotNull UserBaseKey readerId,
            @Nullable Callback<Response> callback)
    {
        MiddleCallback<Response> middleCallback = new BaseMiddleCallback<>(
                callback,
                createMessageHeaderDeletedProcessor(messageHeaderId, readerId));
        messageServiceAsync.deleteMessage(messageHeaderId.commentId, senderUserId, recipientUserId, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Read Message">
    protected DTOProcessor<Response> createMessageHeaderReadProcessor(
            @NotNull MessageHeaderId messageHeaderId,
            @NotNull UserBaseKey readerId)
    {
        return new DTOProcessorMessageRead(messageHeaderCache.get(),
                userProfileCache.get(),
                messageHeaderId,
                readerId);
    }

    @NotNull public MiddleCallback<Response> readMessage(
            int commentId,
            int senderUserId,
            int recipientUserId,
            @NotNull final MessageHeaderId messageHeaderId,
            @NotNull final UserBaseKey readerId,
            @Nullable Callback<Response> callback)
    {
        MiddleCallback<Response> middleCallback = new BaseMiddleCallback<>(
                callback,
                createMessageHeaderReadProcessor(messageHeaderId, readerId));
        messageServiceAsync.readMessage(commentId, senderUserId, recipientUserId, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Read All Message">
    @NotNull protected DTOProcessor<Response> createMessageHeaderReadAllProcessor(
            @NotNull UserBaseKey readerId)
    {
        return new DTOProcessorAllMessagesRead(messageHeaderCache.get(), userProfileCache.get(), readerId);
    }

    @NotNull public MiddleCallback<Response> readAllMessage(
            @NotNull final UserBaseKey readerId,
            @Nullable Callback<Response> callback)
    {
        MiddleCallback<Response> middleCallback = new BaseMiddleCallback<>(
                callback,
                createMessageHeaderReadAllProcessor(readerId));
        messageServiceAsync.readAllMessage(middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
