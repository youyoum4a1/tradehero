package com.tradehero.th.network.service;

import com.tradehero.common.persistence.prefs.LongPreference;
import com.tradehero.th.api.discussion.DiscussionDTOFactory;
import com.tradehero.th.api.discussion.MessageStatusDTO;
import com.tradehero.th.api.discussion.form.MessageCreateFormDTO;
import com.tradehero.th.api.discussion.key.RecipientTypedMessageListKey;
import com.tradehero.th.api.discussion.key.TypedMessageListKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.key.MessageListKey;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.models.discussion.MiddleCallbackDiscussion;
import com.tradehero.th.models.discussion.MiddleCallbackMessageHeader;
import com.tradehero.th.models.discussion.MiddleCallbackMessagePaginatedHeader;
import com.tradehero.th.models.discussion.MiddleCallbackMessageStatus;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.persistence.MessageListTimeline;
import com.tradehero.th.persistence.message.MessageHeaderListCache;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

@Singleton
public class MessageServiceWrapper
{
    private MessageService messageService;
    private MessageServiceAsync messageServiceAsync;
    private DiscussionDTOFactory discussionDTOFactory;
    LongPreference timelinePreference;


    @Inject MessageServiceWrapper(
            MessageService messageService,
            MessageServiceAsync messageServiceAsync,
            @MessageListTimeline LongPreference timelinePreference)
    {
        this.messageService = messageService;
        this.messageServiceAsync = messageServiceAsync;
        this.timelinePreference = timelinePreference;
    }

    private void saveTimeline(long timeline)
    {
        timelinePreference.set(timeline);
    }

    //<editor-fold desc="Get Messages">
    public PaginatedDTO<MessageHeaderDTO> getMessages(MessageListKey messageListKey)
    {
        Timber.d("getMessages messageService:%s", messageService);
        if (messageListKey instanceof TypedMessageListKey)
        {
            return getMessages((TypedMessageListKey) messageListKey);
        }
        return messageService.getMessages(
                messageListKey.page,
                messageListKey.perPage);
    }

    public PaginatedDTO<MessageHeaderDTO> getMessages(TypedMessageListKey messageListKey)
    {
        if (messageListKey instanceof RecipientTypedMessageListKey)
        {
            return getMessages((RecipientTypedMessageListKey) messageListKey);
        }
        return messageService.getMessages(
                messageListKey.discussionType.description,
                null,
                messageListKey.page,
                messageListKey.perPage);
    }

    public PaginatedDTO<MessageHeaderDTO> getMessages(RecipientTypedMessageListKey messageListKey)
    {
        return messageService.getMessages(
                messageListKey.discussionType.description,
                messageListKey.recipientId.key,
                messageListKey.page,
                messageListKey.perPage);
    }

    public MiddleCallbackMessagePaginatedHeader getMessages(MessageListKey messageListKey, Callback<PaginatedDTO<MessageHeaderDTO>> callback)
    {
        if (messageListKey instanceof TypedMessageListKey)
        {
            return getMessages((TypedMessageListKey) messageListKey, callback);
        }
        MiddleCallbackMessagePaginatedHeader middleCallback = new MiddleCallbackMessagePaginatedHeader(callback);
        messageServiceAsync.getMessages(
                messageListKey.page,
                messageListKey.perPage,
                middleCallback);
        return middleCallback;
    }

    public MiddleCallbackMessagePaginatedHeader getMessages(TypedMessageListKey messageListKey, Callback<PaginatedDTO<MessageHeaderDTO>> callback)
    {
        if (messageListKey instanceof RecipientTypedMessageListKey)
        {
            return getMessages((RecipientTypedMessageListKey) messageListKey, callback);
        }
        MiddleCallbackMessagePaginatedHeader middleCallback = new MiddleCallbackMessagePaginatedHeader(callback);
        messageServiceAsync.getMessages(
                messageListKey.discussionType.description,
                null,
                messageListKey.page,
                messageListKey.perPage,
                middleCallback);
        return middleCallback;
    }

    public MiddleCallbackMessagePaginatedHeader getMessages(RecipientTypedMessageListKey messageListKey, Callback<PaginatedDTO<MessageHeaderDTO>> callback)
    {
        MiddleCallbackMessagePaginatedHeader middleCallback = new MiddleCallbackMessagePaginatedHeader(callback);
        messageServiceAsync.getMessages(
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

    //<editor-fold desc="Get Free Count">
    public MessageStatusDTO getFreeCount(UserBaseKey userBaseKey)
    {
        return messageService.getStatus(userBaseKey.key);
    }

    public MiddleCallbackMessageStatus getFreeCount(UserBaseKey userBaseKey, Callback<MessageStatusDTO> callback)
    {
        MiddleCallbackMessageStatus middleCallback = new MiddleCallbackMessageStatus(callback);
        messageServiceAsync.getFreeCount(userBaseKey.key, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Create Message">
    public DiscussionDTO createMessage(MessageCreateFormDTO form)
    {
        return messageService.createMessage(form);
    }

    public MiddleCallbackDiscussion createMessage(MessageCreateFormDTO form, Callback<DiscussionDTO> callback)
    {
        MiddleCallbackDiscussion middleCallback = new MiddleCallbackDiscussion(callback, discussionDTOFactory);
        messageServiceAsync.createMessage(form, middleCallback);
        return middleCallback;
    }


    //</editor-fold>

    //<editor-fold desc="Delete Message">
    public Response deleteMessage(int commentId)
    {
        return messageService.deleteMessage(commentId);
    }

    public MiddleCallback<Response> deleteMessage(final int commentId, final MessageHeaderListCache messageHeaderListCache, Callback<Response> callback)
    {
        MiddleCallback<Response> middleCallback = new MiddleCallback<Response>(callback)
        {
            @Override public void success(Response response, Response response2)
            {
                super.success(response, response2);
                Timber.d("Delete message success :%d",commentId);
                messageHeaderListCache.markMessageDeleted(commentId);
            }

            @Override public void failure(RetrofitError error)
            {
                super.failure(error);
            }
        };
        messageServiceAsync.deleteMessage(commentId, middleCallback);
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
