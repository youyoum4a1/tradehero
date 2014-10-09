package com.tradehero.th.network.service;

import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOFactory;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.ReadablePaginatedMessageHeaderDTO;
import com.tradehero.th.api.discussion.form.MessageCreateFormDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.api.discussion.key.MessageHeaderUserId;
import com.tradehero.th.api.discussion.key.MessageListKey;
import com.tradehero.th.api.discussion.key.RecipientTypedMessageListKey;
import com.tradehero.th.api.discussion.key.TypedMessageListKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import com.tradehero.th.models.DTOProcessor;
import com.tradehero.th.models.discussion.DTOProcessorAllMessagesRead;
import com.tradehero.th.models.discussion.DTOProcessorDiscussionCreate;
import com.tradehero.th.models.discussion.DTOProcessorMessageDeleted;
import com.tradehero.th.models.discussion.DTOProcessorMessageRead;
import com.tradehero.th.models.discussion.DTOProcessorReadablePaginatedMessageReceived;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.home.HomeContentCache;
import com.tradehero.th.persistence.message.MessageHeaderCache;
import com.tradehero.th.persistence.message.MessageHeaderListCache;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;

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
    @NotNull private final Lazy<HomeContentCache> homeContentCache;

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
            @NotNull Lazy<UserProfileCache> userProfileCache,
            @NotNull Lazy<HomeContentCache> homeContentCache)
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
        this.homeContentCache = homeContentCache;
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
    protected DTOProcessor<DiscussionDTO> createDiscussionCreateProcessor(DiscussionKey stubKey)
    {
        return new DTOProcessorDiscussionCreate(
                discussionDTOFactory,
                currentUserId,
                discussionCache.get(),
                userMessagingRelationshipCache.get(),
                stubKey);
    }

    public DiscussionDTO createMessage(MessageCreateFormDTO form)
    {
        return createDiscussionCreateProcessor(null).process(messageService.createMessage(form));
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
    protected DTOProcessor<BaseResponseDTO> createMessageHeaderDeletedProcessor(
            @NotNull MessageHeaderId messageHeaderId,
            @NotNull UserBaseKey readerId)
    {
        return new DTOProcessorMessageDeleted(
                messageHeaderCache.get(),
                userProfileCache.get(),
                homeContentCache.get(),
                messageHeaderListCache.get(),
                messageHeaderId,
                readerId);
    }

    public BaseResponseDTO deleteMessage(
            @NotNull MessageHeaderId messageHeaderId,
            @NotNull UserBaseKey senderUserId,
            @NotNull UserBaseKey recipientUserId,
            @NotNull UserBaseKey readerId)
    {
        return createMessageHeaderDeletedProcessor(messageHeaderId, readerId).process(
                messageService.deleteMessage(
                        messageHeaderId.commentId,
                        senderUserId.key,
                        recipientUserId.key));
    }

    public MiddleCallback<BaseResponseDTO> deleteMessage(
            @NotNull MessageHeaderId messageHeaderId,
            @NotNull UserBaseKey senderUserId,
            @NotNull UserBaseKey recipientUserId,
            @NotNull UserBaseKey readerId,
            @Nullable Callback<BaseResponseDTO> callback)
    {
        MiddleCallback<BaseResponseDTO> middleCallback = new BaseMiddleCallback<>(
                callback,
                createMessageHeaderDeletedProcessor(messageHeaderId, readerId));
        messageServiceAsync.deleteMessage(
                messageHeaderId.commentId,
                senderUserId.key,
                recipientUserId.key,
                middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Read Message">
    protected DTOProcessor<BaseResponseDTO> createMessageHeaderReadProcessor(
            @NotNull MessageHeaderId messageHeaderId,
            @NotNull UserBaseKey readerId)
    {
        return new DTOProcessorMessageRead(
                messageHeaderCache.get(),
                userProfileCache.get(),
                homeContentCache.get(),
                messageHeaderId,
                readerId);
    }

    @NotNull public BaseResponseDTO readMessage(
            @NotNull MessageHeaderId commentId,
            @NotNull UserBaseKey senderUserId,
            @NotNull UserBaseKey recipientUserId,
            @NotNull MessageHeaderId messageHeaderId,
            @NotNull UserBaseKey readerId)
    {
        return createMessageHeaderReadProcessor(messageHeaderId, readerId).process(
                messageService.readMessage(
                        commentId.commentId,
                        senderUserId.key,
                        recipientUserId.key));
    }

    @NotNull public MiddleCallback<BaseResponseDTO> readMessage(
            @NotNull MessageHeaderId commentId,
            @NotNull UserBaseKey senderUserId,
            @NotNull UserBaseKey recipientUserId,
            @NotNull MessageHeaderId messageHeaderId,
            @NotNull UserBaseKey readerId,
            @Nullable Callback<BaseResponseDTO> callback)
    {
        MiddleCallback<BaseResponseDTO> middleCallback = new BaseMiddleCallback<>(
                callback,
                createMessageHeaderReadProcessor(messageHeaderId, readerId));
        messageServiceAsync.readMessage(
                commentId.commentId,
                senderUserId.key,
                recipientUserId.key,
                middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Read All Message">
    @NotNull protected DTOProcessor<BaseResponseDTO> createMessageHeaderReadAllProcessor(
            @NotNull UserBaseKey readerId)
    {
        return new DTOProcessorAllMessagesRead(
                messageHeaderCache.get(),
                userProfileCache.get(),
                homeContentCache.get(),
                readerId);
    }

    @NotNull public BaseResponseDTO readAllMessage(
            @NotNull UserBaseKey readerId)
    {
        return createMessageHeaderReadAllProcessor(readerId).process(
                messageService.readAllMessage());
    }

    @NotNull public MiddleCallback<BaseResponseDTO> readAllMessage(
            @NotNull UserBaseKey readerId,
            @Nullable Callback<BaseResponseDTO> callback)
    {
        MiddleCallback<BaseResponseDTO> middleCallback = new BaseMiddleCallback<>(
                callback,
                createMessageHeaderReadAllProcessor(readerId));
        messageServiceAsync.readAllMessage(middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
