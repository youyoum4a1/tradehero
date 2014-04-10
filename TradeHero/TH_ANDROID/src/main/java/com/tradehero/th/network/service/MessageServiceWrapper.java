package com.tradehero.th.network.service;

import com.tradehero.common.persistence.prefs.LongPreference;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.key.MessageListKey;
import com.tradehero.th.models.discussion.MiddleCallbackDiscussion;
import com.tradehero.th.models.discussion.MiddleCallbackMessageHeader;
import com.tradehero.th.models.discussion.MiddleCallbackMessagePaginatedHeader;
import com.tradehero.th.persistence.MessageListTimeline;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;
import timber.log.Timber;

/**
 * Created by wangliang on 3/4/14.
 */
@Singleton
public class MessageServiceWrapper
{
    private MessageService messageService;
    private MessageServiceAsync messageServiceAsync;
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
        Timber.d("getMessages messageService:%s",messageService);
        return messageService.getMessages(messageListKey.page, messageListKey.perPage);
    }

    public MiddleCallbackMessagePaginatedHeader getMessages(MessageListKey messageListKey, Callback<PaginatedDTO<MessageHeaderDTO>> callback)
    {
        MiddleCallbackMessagePaginatedHeader middleCallback = new MiddleCallbackMessagePaginatedHeader(callback);
        messageServiceAsync.getMessages(messageListKey.page, messageListKey.perPage, middleCallback);
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

    //<editor-fold desc="Create Message">
    public DiscussionDTO createMessage(MessageHeaderDTO form)
    {
        return messageService.createMessage(form);
    }

    public MiddleCallbackDiscussion createMessage(MessageHeaderDTO form, Callback<DiscussionDTO> callback)
    {
        MiddleCallbackDiscussion middleCallback = new MiddleCallbackDiscussion(callback);
        messageServiceAsync.createMessage(form, middleCallback);
        return middleCallback;
    }

    //TODO fake,not real
    public MiddleCallbackDiscussion deleteMessage(MessageHeaderDTO form, Callback<DiscussionDTO> callback)
    {
        MiddleCallbackDiscussion middleCallback = new MiddleCallbackDiscussion(callback);
        messageServiceAsync.deleteMessage(form, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
