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
import com.tradehero.th.persistence.MessageListTimeline;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
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
            DiscussionDTOFactory discussionDTOFactory,
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

    //TODO fake,not real
    public MiddleCallbackDiscussion deleteMessage(MessageHeaderDTO form, Callback<DiscussionDTO> callback)
    {
        MiddleCallbackDiscussion middleCallback = new MiddleCallbackDiscussion(callback, discussionDTOFactory);
        messageServiceAsync.deleteMessage(form, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
