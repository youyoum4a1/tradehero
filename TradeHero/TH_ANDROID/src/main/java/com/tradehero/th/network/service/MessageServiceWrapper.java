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
import com.tradehero.th.persistence.discussion.DiscussionCacheRx;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.message.MessageHeaderCacheRx;
import com.tradehero.th.persistence.message.MessageHeaderListCacheRx;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import retrofit.Callback;
import rx.Observable;

@Singleton
public class MessageServiceWrapper
{
    @NonNull private final MessageService messageService;
    @NonNull private final MessageServiceAsync messageServiceAsync;
    @NonNull private final MessageServiceRx messageServiceRx;
    @NonNull private final DiscussionDTOFactory discussionDTOFactory;
    @NonNull private final CurrentUserId currentUserId;

    // We need Lazy here because MessageStatusCache also injects a MessageServiceWrapper
    @NonNull private final Lazy<MessageHeaderListCacheRx> messageHeaderListCache;
    @NonNull private final Lazy<MessageHeaderCacheRx> messageHeaderCache;
    @NonNull private final Lazy<UserMessagingRelationshipCacheRx> userMessagingRelationshipCache;
    @NonNull private final Lazy<DiscussionCacheRx> discussionCache;
    @NonNull private final Lazy<UserProfileCacheRx> userProfileCache;
    @NonNull private final Lazy<HomeContentCacheRx> homeContentCache;

    //<editor-fold desc="Constructors">
    @Inject MessageServiceWrapper(
            @NonNull MessageService messageService,
            @NonNull MessageServiceAsync messageServiceAsync,
            @NonNull MessageServiceRx messageServiceRx,
            @NonNull DiscussionDTOFactory discussionDTOFactory,
            @NonNull CurrentUserId currentUserId,
            @NonNull Lazy<MessageHeaderListCacheRx> messageHeaderListCache,
            @NonNull Lazy<MessageHeaderCacheRx> messageHeaderCache,
            @NonNull Lazy<UserMessagingRelationshipCacheRx> userMessagingRelationshipCache,
            @NonNull Lazy<DiscussionCacheRx> discussionCache,
            @NonNull Lazy<UserProfileCacheRx> userProfileCache,
            @NonNull Lazy<HomeContentCacheRx> homeContentCache)
    {
        this.messageService = messageService;
        this.messageServiceAsync = messageServiceAsync;
        this.messageServiceRx = messageServiceRx;
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

    public Observable<ReadablePaginatedMessageHeaderDTO> getMessageHeadersRx(MessageListKey messageListKey)
    {
        if (messageListKey instanceof TypedMessageListKey)
        {
            return getMessageHeadersRx((TypedMessageListKey) messageListKey);
        }
        return messageServiceRx.getMessageHeaders(
                        messageListKey.page,
                        messageListKey.perPage);
    }

    public Observable<ReadablePaginatedMessageHeaderDTO> getMessageHeadersRx(TypedMessageListKey messageListKey)
    {
        if (messageListKey instanceof RecipientTypedMessageListKey)
        {
            return getMessageHeadersRx((RecipientTypedMessageListKey) messageListKey);
        }
        return messageServiceRx.getMessageHeaders(
                        messageListKey.discussionType.description,
                        null,
                        messageListKey.page,
                        messageListKey.perPage);
    }

    public Observable<ReadablePaginatedMessageHeaderDTO> getMessageHeadersRx(
            RecipientTypedMessageListKey messageListKey)
    {
        return messageServiceRx.getMessageHeaders(
                        messageListKey.discussionType.description,
                        messageListKey.recipientId.key,
                        messageListKey.page,
                        messageListKey.perPage);
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

    public Observable<MessageHeaderDTO> getMessageHeaderRx(MessageHeaderId messageHeaderId)
    {
        if (messageHeaderId instanceof MessageHeaderUserId && ((MessageHeaderUserId) messageHeaderId).userBaseKey != null)
        {
            return messageServiceRx.getMessageHeader(
                    messageHeaderId.commentId,
                    ((MessageHeaderUserId) messageHeaderId).userBaseKey.key);
        }
        return messageServiceRx.getMessageHeader(messageHeaderId.commentId, null);
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

    public Observable<MessageHeaderDTO> getMessageThreadRx(UserBaseKey correspondentId)
    {
        return messageServiceRx.getMessageThread(correspondentId.key);
    }
    //</editor-fold>

    //<editor-fold desc="Get Messaging Relationship Status">
    public Observable<UserMessagingRelationshipDTO> getMessagingRelationshipStatusRx(UserBaseKey recipient)
    {
        return messageServiceRx.getMessagingRelationgshipStatus(recipient.key);
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

    public MiddleCallback<DiscussionDTO> createMessage(MessageCreateFormDTO form, Callback<DiscussionDTO> callback)
    {
        MiddleCallback<DiscussionDTO> middleCallback = new BaseMiddleCallback<>(
                callback,
                createDiscussionCreateProcessor(null));
        messageServiceAsync.createMessage(form, middleCallback);
        return middleCallback;
    }

    public Observable<DiscussionDTO> createMessageRx(MessageCreateFormDTO form)
    {
        return messageServiceRx.createMessage(form);
    }
    //</editor-fold>

    //<editor-fold desc="Delete Message">
    protected DTOProcessor<BaseResponseDTO> createMessageHeaderDeletedProcessor(
            @NonNull MessageHeaderId messageHeaderId,
            @NonNull UserBaseKey readerId)
    {
        return new DTOProcessorMessageDeleted(
                messageHeaderCache.get(),
                userProfileCache.get(),
                homeContentCache.get(),
                messageHeaderListCache.get(),
                messageHeaderId,
                readerId);
    }

    public MiddleCallback<BaseResponseDTO> deleteMessage(
            @NonNull MessageHeaderId messageHeaderId,
            @NonNull UserBaseKey senderUserId,
            @NonNull UserBaseKey recipientUserId,
            @NonNull UserBaseKey readerId,
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

    public Observable<BaseResponseDTO> deleteMessage(
            @NonNull MessageHeaderId messageHeaderId,
            @NonNull UserBaseKey senderUserId,
            @NonNull UserBaseKey recipientUserId,
            @NonNull UserBaseKey readerId)
    {
        return messageServiceRx.deleteMessage(
                        messageHeaderId.commentId,
                        senderUserId.key,
                        recipientUserId.key);
    }
    //</editor-fold>

    //<editor-fold desc="Read Message">
    protected DTOProcessor<BaseResponseDTO> createMessageHeaderReadProcessor(
            @NonNull MessageHeaderId messageHeaderId,
            @NonNull UserBaseKey readerId)
    {
        return new DTOProcessorMessageRead(
                messageHeaderCache.get(),
                userProfileCache.get(),
                homeContentCache.get(),
                messageHeaderId,
                readerId);
    }

    @NonNull public MiddleCallback<BaseResponseDTO> readMessage(
            @NonNull MessageHeaderId commentId,
            @NonNull UserBaseKey senderUserId,
            @NonNull UserBaseKey recipientUserId,
            @NonNull MessageHeaderId messageHeaderId,
            @NonNull UserBaseKey readerId,
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

    @NonNull public Observable<BaseResponseDTO> readMessageRx(
            @NonNull MessageHeaderId commentId,
            @NonNull UserBaseKey senderUserId,
            @NonNull UserBaseKey recipientUserId,
            @NonNull MessageHeaderId messageHeaderId,
            @NonNull UserBaseKey readerId)
    {
        return messageServiceRx.readMessage(
                        commentId.commentId,
                        senderUserId.key,
                        recipientUserId.key);
    }
    //</editor-fold>

    //<editor-fold desc="Read All Message">
    @NonNull protected DTOProcessor<BaseResponseDTO> createMessageHeaderReadAllProcessor(
            @NonNull UserBaseKey readerId)
    {
        return new DTOProcessorAllMessagesRead(
                messageHeaderCache.get(),
                userProfileCache.get(),
                homeContentCache.get(),
                readerId);
    }

    @NonNull public MiddleCallback<BaseResponseDTO> readAllMessage(
            @NonNull UserBaseKey readerId,
            @Nullable Callback<BaseResponseDTO> callback)
    {
        MiddleCallback<BaseResponseDTO> middleCallback = new BaseMiddleCallback<>(
                callback,
                createMessageHeaderReadAllProcessor(readerId));
        messageServiceAsync.readAllMessage(middleCallback);
        return middleCallback;
    }

    @NonNull public Observable<BaseResponseDTO> readAllMessageRx(
            @NonNull UserBaseKey readerId)
    {
        return messageServiceRx.readAllMessage();
    }
    //</editor-fold>
}
