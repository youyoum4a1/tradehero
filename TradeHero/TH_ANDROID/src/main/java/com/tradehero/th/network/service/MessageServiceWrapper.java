package com.ayondo.academy.network.service;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.BaseResponseDTO;
import com.ayondo.academy.api.discussion.DiscussionDTO;
import com.ayondo.academy.api.discussion.MessageHeaderDTO;
import com.ayondo.academy.api.discussion.ReadablePaginatedMessageHeaderDTO;
import com.ayondo.academy.api.discussion.form.MessageCreateFormDTO;
import com.ayondo.academy.api.discussion.key.MessageHeaderId;
import com.ayondo.academy.api.discussion.key.MessageHeaderUserId;
import com.ayondo.academy.api.discussion.key.MessageListKey;
import com.ayondo.academy.api.discussion.key.RecipientTypedMessageListKey;
import com.ayondo.academy.api.discussion.key.TypedMessageListKey;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.users.UserMessagingRelationshipDTO;
import com.ayondo.academy.models.discussion.DTOProcessorAllMessagesRead;
import com.ayondo.academy.models.discussion.DTOProcessorDiscussionCreate;
import com.ayondo.academy.models.discussion.DTOProcessorMessageDeleted;
import com.ayondo.academy.models.discussion.DTOProcessorMessageRead;
import com.ayondo.academy.models.discussion.DTOProcessorReadablePaginatedMessageReceived;
import com.ayondo.academy.network.DelayRetriesOrFailFunc1;
import com.ayondo.academy.persistence.discussion.DiscussionCacheRx;
import com.ayondo.academy.persistence.message.MessageHeaderCacheRx;
import com.ayondo.academy.persistence.message.MessageHeaderListCacheRx;
import com.ayondo.academy.persistence.user.UserMessagingRelationshipCacheRx;
import com.ayondo.academy.persistence.user.UserProfileCacheRx;
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
    @NonNull private final CurrentUserId currentUserId;

    // We need Lazy here because MessageStatusCache also injects a MessageServiceWrapper
    @NonNull private final Lazy<MessageHeaderListCacheRx> messageHeaderListCache;
    @NonNull private final Lazy<MessageHeaderCacheRx> messageHeaderCache;
    @NonNull private final Lazy<UserMessagingRelationshipCacheRx> userMessagingRelationshipCache;
    @NonNull private final Lazy<DiscussionCacheRx> discussionCache;
    @NonNull private final Lazy<UserProfileCacheRx> userProfileCache;

    //<editor-fold desc="Constructors">
    @Inject MessageServiceWrapper(
            @NonNull MessageServiceRx messageServiceRx,
            @NonNull CurrentUserId currentUserId,
            @NonNull Lazy<MessageHeaderListCacheRx> messageHeaderListCache,
            @NonNull Lazy<MessageHeaderCacheRx> messageHeaderCache,
            @NonNull Lazy<UserMessagingRelationshipCacheRx> userMessagingRelationshipCache,
            @NonNull Lazy<DiscussionCacheRx> discussionCache,
            @NonNull Lazy<UserProfileCacheRx> userProfileCache)
    {
        this.messageServiceRx = messageServiceRx;
        this.currentUserId = currentUserId;
        this.messageHeaderListCache = messageHeaderListCache;
        this.messageHeaderCache = messageHeaderCache;
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
        this.discussionCache = discussionCache;
        this.userProfileCache = userProfileCache;
    }
    //</editor-fold>

    //<editor-fold desc="Get Message Headers">
    @NonNull public Observable<ReadablePaginatedMessageHeaderDTO> getMessageHeadersRx(
            @NonNull MessageListKey messageListKey)
    {
        Observable<ReadablePaginatedMessageHeaderDTO> returned;
        if (messageListKey instanceof RecipientTypedMessageListKey)
        {
            returned = messageServiceRx.getMessageHeaders(
                    ((RecipientTypedMessageListKey) messageListKey).discussionType.description,
                    ((RecipientTypedMessageListKey) messageListKey).recipientId.key,
                    messageListKey.page,
                    messageListKey.perPage);
        }
        else if (messageListKey instanceof TypedMessageListKey)
        {
            returned = messageServiceRx.getMessageHeaders(
                    ((TypedMessageListKey) messageListKey).discussionType.description,
                    null,
                    messageListKey.page,
                    messageListKey.perPage);
        }
        else
        {
            returned = messageServiceRx.getMessageHeaders(
                    messageListKey.page,
                    messageListKey.perPage);
        }
        return returned
                .map(new DTOProcessorReadablePaginatedMessageReceived<
                        MessageHeaderDTO,
                        ReadablePaginatedMessageHeaderDTO>(
                        userProfileCache.get(),
                        currentUserId.toUserBaseKey()));
    }
    //</editor-fold>

    //<editor-fold desc="Get Message Header">
    @NonNull public Observable<MessageHeaderDTO> getMessageHeaderRx(@NonNull MessageHeaderId messageHeaderId)
    {
        Integer referencedUser = null;
        if (messageHeaderId instanceof MessageHeaderUserId
                && ((MessageHeaderUserId) messageHeaderId).userBaseKey != null)
        {
            referencedUser =((MessageHeaderUserId) messageHeaderId).userBaseKey.key;
        }
        return messageServiceRx.getMessageHeader(
                messageHeaderId.commentId,
                referencedUser);
    }

    @NonNull public Observable<MessageHeaderDTO> getMessageThreadRx(@NonNull UserBaseKey correspondentId)
    {
        return messageServiceRx.getMessageThread(correspondentId.key);
    }
    //</editor-fold>

    //<editor-fold desc="Get Messaging Relationship Status">
    @NonNull public Observable<UserMessagingRelationshipDTO> getMessagingRelationshipStatusRx(
            @NonNull UserBaseKey recipient)
    {
        return messageServiceRx.getMessagingRelationgshipStatus(recipient.key);
    }
    //</editor-fold>

    //<editor-fold desc="Create Message">
    @NonNull public Observable<DiscussionDTO> createMessageRx(@NonNull MessageCreateFormDTO form)
    {
        return messageServiceRx.createMessage(form)
                .retryWhen(new DelayRetriesOrFailFunc1(RETRY_COUNT, RETRY_DELAY_MILLIS))
                .map(new DTOProcessorDiscussionCreate(
                        currentUserId,
                        discussionCache.get(),
                        userMessagingRelationshipCache.get(),
                        null));
    }
    //</editor-fold>

    //<editor-fold desc="Delete Message">
    @NonNull public Observable<BaseResponseDTO> deleteMessageRx(
            @NonNull MessageHeaderId messageHeaderId,
            @NonNull UserBaseKey senderUserId,
            @NonNull UserBaseKey recipientUserId,
            @NonNull UserBaseKey readerId)
    {
        return messageServiceRx.deleteMessage(
                messageHeaderId.commentId,
                senderUserId.key,
                recipientUserId.key)
                .map(new DTOProcessorMessageDeleted(
                        messageHeaderCache.get(),
                        userProfileCache.get(),
                        messageHeaderListCache.get(),
                        messageHeaderId,
                        readerId));
    }
    //</editor-fold>

    //<editor-fold desc="Read Message">
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
                .map(new DTOProcessorMessageRead(
                        messageHeaderCache.get(),
                        userProfileCache.get(),
                        messageHeaderId,
                        readerId));
    }
    //</editor-fold>

    //<editor-fold desc="Read All Message">
    @NonNull public Observable<BaseResponseDTO> readAllMessageRx(
            @NonNull UserBaseKey readerId)
    {
        return messageServiceRx.readAllMessage()
                .map(new DTOProcessorAllMessagesRead(
                        messageHeaderCache.get(),
                        userProfileCache.get(),
                        readerId));
    }
    //</editor-fold>
}
