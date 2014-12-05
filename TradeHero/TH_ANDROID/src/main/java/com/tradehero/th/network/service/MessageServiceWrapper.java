package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
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
import com.tradehero.th.models.discussion.DTOProcessorAllMessagesRead;
import com.tradehero.th.models.discussion.DTOProcessorDiscussionCreate;
import com.tradehero.th.models.discussion.DTOProcessorMessageDeleted;
import com.tradehero.th.models.discussion.DTOProcessorMessageRead;
import com.tradehero.th.models.discussion.DTOProcessorReadablePaginatedMessageReceived;
import com.tradehero.th.network.DelayRetriesOrFailFunc1;
import com.tradehero.th.persistence.discussion.DiscussionCacheRx;
import com.tradehero.th.persistence.home.HomeContentCacheRx;
import com.tradehero.th.persistence.message.MessageHeaderCacheRx;
import com.tradehero.th.persistence.message.MessageHeaderListCacheRx;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton
public class MessageServiceWrapper
{
    private static final int RETRY_COUNT = 3;
    private static final long RETRY_DELAY_MILLIS = 1000;

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
    protected DTOProcessorReadablePaginatedMessageReceived<MessageHeaderDTO, ReadablePaginatedMessageHeaderDTO> createReadablePaginatedMessageHeaderReceivedProcessor()
    {
        return new DTOProcessorReadablePaginatedMessageReceived<>(userProfileCache.get(), currentUserId.toUserBaseKey());
    }

    public Observable<ReadablePaginatedMessageHeaderDTO> getMessageHeadersRx(MessageListKey messageListKey)
    {
        if (messageListKey instanceof TypedMessageListKey)
        {
            return getMessageHeadersRx((TypedMessageListKey) messageListKey);
        }
        return messageServiceRx.getMessageHeaders(
                messageListKey.page,
                messageListKey.perPage)
                .map(createReadablePaginatedMessageHeaderReceivedProcessor());
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
                messageListKey.perPage)
                .map(createReadablePaginatedMessageHeaderReceivedProcessor());
    }

    public Observable<ReadablePaginatedMessageHeaderDTO> getMessageHeadersRx(
            RecipientTypedMessageListKey messageListKey)
    {
        return messageServiceRx.getMessageHeaders(
                messageListKey.discussionType.description,
                messageListKey.recipientId.key,
                messageListKey.page,
                messageListKey.perPage)
                .map(createReadablePaginatedMessageHeaderReceivedProcessor());
    }
    //</editor-fold>

    //<editor-fold desc="Get Message Header">
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
    protected DTOProcessorDiscussionCreate createDiscussionCreateProcessor(DiscussionKey stubKey)
    {
        return new DTOProcessorDiscussionCreate(
                discussionDTOFactory,
                currentUserId,
                discussionCache.get(),
                userMessagingRelationshipCache.get(),
                stubKey);
    }

    public Observable<DiscussionDTO> createMessageRx(MessageCreateFormDTO form)
    {
        return messageServiceRx.createMessage(form)
                .retryWhen(new DelayRetriesOrFailFunc1(RETRY_COUNT, RETRY_DELAY_MILLIS))
                .map(createDiscussionCreateProcessor(null));
    }
    //</editor-fold>

    //<editor-fold desc="Delete Message">
    protected DTOProcessorMessageDeleted createMessageHeaderDeletedProcessor(
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

    public Observable<BaseResponseDTO> deleteMessageRx(
            @NonNull MessageHeaderId messageHeaderId,
            @NonNull UserBaseKey senderUserId,
            @NonNull UserBaseKey recipientUserId,
            @NonNull UserBaseKey readerId)
    {
        return messageServiceRx.deleteMessage(
                messageHeaderId.commentId,
                senderUserId.key,
                recipientUserId.key)
                .map(createMessageHeaderDeletedProcessor(messageHeaderId, readerId));
    }
    //</editor-fold>

    //<editor-fold desc="Read Message">
    protected DTOProcessorMessageRead createMessageHeaderReadProcessor(
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
                recipientUserId.key)
                .map(createMessageHeaderReadProcessor(messageHeaderId, readerId));
    }
    //</editor-fold>

    //<editor-fold desc="Read All Message">
    @NonNull protected DTOProcessorAllMessagesRead createMessageHeaderReadAllProcessor(
            @NonNull UserBaseKey readerId)
    {
        return new DTOProcessorAllMessagesRead(
                messageHeaderCache.get(),
                userProfileCache.get(),
                homeContentCache.get(),
                readerId);
    }

    @NonNull public Observable<BaseResponseDTO> readAllMessageRx(
            @NonNull UserBaseKey readerId)
    {
        return messageServiceRx.readAllMessage()
                .map(createMessageHeaderReadAllProcessor(readerId));
    }
    //</editor-fold>
}
