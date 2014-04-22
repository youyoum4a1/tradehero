package com.tradehero.th.network.service;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTOFactory;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.form.MessageCreateFormDTO;
import com.tradehero.th.api.discussion.key.MessageHeaderId;
import com.tradehero.th.api.discussion.key.MessageListKey;
import com.tradehero.th.api.discussion.key.RecipientTypedMessageListKey;
import com.tradehero.th.api.discussion.key.TypedMessageListKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserMessagingRelationshipDTO;
import com.tradehero.th.models.discussion.MiddleCallbackDiscussion;
import com.tradehero.th.models.discussion.MiddleCallbackMessageDeleted;
import com.tradehero.th.models.discussion.MiddleCallbackMessageHeader;
import com.tradehero.th.models.discussion.MiddleCallbackMessagePaginatedHeader;
import com.tradehero.th.models.discussion.MiddleCallbackMessagingRelationship;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.message.MessageHeaderListCache;
import com.tradehero.th.persistence.user.UserMessagingRelationshipCache;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import retrofit.client.Response;

@Singleton
public class MessageServiceWrapper
{
    private MessageService messageService;
    private MessageServiceAsync messageServiceAsync;
    private DiscussionDTOFactory discussionDTOFactory;

    // We need Lazy here because MessageStatusCache also injects a MessageServiceWrapper
    private Lazy<MessageHeaderListCache> messageHeaderListCache;
    private Lazy<UserMessagingRelationshipCache> userMessagingRelationshipCache;

    @Inject MessageServiceWrapper(
            MessageService messageService,
            MessageServiceAsync messageServiceAsync,
            DiscussionDTOFactory discussionDTOFactory,
            Lazy<MessageHeaderListCache> messageHeaderListCache,
            Lazy<UserMessagingRelationshipCache> userMessagingRelationshipCache)
    {
        this.messageService = messageService;
        this.messageServiceAsync = messageServiceAsync;
        this.discussionDTOFactory = discussionDTOFactory;
        this.messageHeaderListCache = messageHeaderListCache;
        this.userMessagingRelationshipCache = userMessagingRelationshipCache;
    }

    //<editor-fold desc="Get Message Headers">
    public PaginatedDTO<MessageHeaderDTO> getMessageHeaders(MessageListKey messageListKey)
    {
        if (messageListKey instanceof TypedMessageListKey)
        {
            return getMessageHeaders((TypedMessageListKey) messageListKey);
        }
        return messageService.getMessageHeaders(
                messageListKey.page,
                messageListKey.perPage);
    }

    public PaginatedDTO<MessageHeaderDTO> getMessageHeaders(TypedMessageListKey messageListKey)
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

    public PaginatedDTO<MessageHeaderDTO> getMessageHeaders(
            RecipientTypedMessageListKey messageListKey)
    {
        return messageService.getMessageHeaders(
                messageListKey.discussionType.description,
                messageListKey.recipientId.key,
                messageListKey.page,
                messageListKey.perPage);
    }

    public MiddleCallbackMessagePaginatedHeader getMessageHeaders(MessageListKey messageListKey,
            Callback<PaginatedDTO<MessageHeaderDTO>> callback)
    {
        if (messageListKey instanceof TypedMessageListKey)
        {
            return getMessageHeaders((TypedMessageListKey) messageListKey, callback);
        }
        MiddleCallbackMessagePaginatedHeader middleCallback = new MiddleCallbackMessagePaginatedHeader(callback);
        messageServiceAsync.getMessageHeaders(
                messageListKey.page,
                messageListKey.perPage,
                middleCallback);
        return middleCallback;
    }

    public MiddleCallbackMessagePaginatedHeader getMessageHeaders(
            TypedMessageListKey messageListKey, Callback<PaginatedDTO<MessageHeaderDTO>> callback)
    {
        if (messageListKey instanceof RecipientTypedMessageListKey)
        {
            return getMessageHeaders((RecipientTypedMessageListKey) messageListKey, callback);
        }
        MiddleCallbackMessagePaginatedHeader middleCallback = new MiddleCallbackMessagePaginatedHeader(callback);
        messageServiceAsync.getMessageHeaders(
                messageListKey.discussionType.description,
                null,
                messageListKey.page,
                messageListKey.perPage,
                middleCallback);
        return middleCallback;
    }

    public MiddleCallbackMessagePaginatedHeader getMessageHeaders(
            RecipientTypedMessageListKey messageListKey,
            Callback<PaginatedDTO<MessageHeaderDTO>> callback)
    {
        MiddleCallbackMessagePaginatedHeader middleCallback = new MiddleCallbackMessagePaginatedHeader(callback);
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
    public MessageHeaderDTO getMessageHeader(int commentId)
    {
        return messageService.getMessageHeader(commentId);
    }

    public MiddleCallbackMessageHeader getMessageHeader(int commentId, Callback<MessageHeaderDTO> callback)
    {
        MiddleCallbackMessageHeader middleCallback = new MiddleCallbackMessageHeader(callback);
        messageServiceAsync.getMessageHeader(commentId, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Get Messaging Relationship Status">
    public UserMessagingRelationshipDTO getMessagingRelationgshipStatus(UserBaseKey recipient)
    {
        return messageService.getMessagingRelationgshipStatus(recipient.key);
    }

    public MiddleCallbackMessagingRelationship getMessagingRelationgshipStatus(
            UserBaseKey recipient,
            Callback<UserMessagingRelationshipDTO> callback)
    {
        MiddleCallbackMessagingRelationship
                middleCallback = new MiddleCallbackMessagingRelationship(callback);
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

    public MiddleCallbackDiscussion createMessage(MessageCreateFormDTO form, Callback<DiscussionDTO> callback)
    {
        MiddleCallbackDiscussion middleCallback = new MiddleCallbackDiscussion(
                callback,
                discussionDTOFactory,
                userMessagingRelationshipCache.get());
        messageServiceAsync.createMessage(form, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Delete Message">
    public Response deleteMessage(MessageHeaderId messageHeaderId)
    {
        Response response = messageService.deleteMessage(messageHeaderId.key);
        messageHeaderListCache.get().invalidateKeysThatList(messageHeaderId);
        return response;
    }

    public MiddleCallbackMessageDeleted deleteMessage(final MessageHeaderId messageHeaderId, Callback<Response> callback)
    {
        MiddleCallbackMessageDeleted middleCallback = new MiddleCallbackMessageDeleted(messageHeaderId, callback, messageHeaderListCache.get());
        messageServiceAsync.deleteMessage(messageHeaderId.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Read Message">
    public Response readMessage(int commentId)
    {
        return messageService.readMessage(commentId);
    }

    public MiddleCallback<Response> readMessage(int commentId, Callback<Response> callback)
    {
        MiddleCallback<Response> middleCallback = new MiddleCallback<>(callback);
        messageServiceAsync.readMessage(commentId, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
