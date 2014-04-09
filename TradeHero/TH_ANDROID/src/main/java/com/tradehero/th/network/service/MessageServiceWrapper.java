package com.tradehero.th.network.service;

import com.tradehero.common.persistence.prefs.LongPreference;
import com.tradehero.th.api.PaginatedDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.MessageDTO;
import com.tradehero.th.api.discussion.key.MessageListKey;
import com.tradehero.th.models.discussion.MiddleCallbackDiscussion;
import com.tradehero.th.models.discussion.MiddleCallbackMessageList;
import com.tradehero.th.persistence.MessageListTimeline;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.Callback;

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
    public PaginatedDTO<MessageDTO> getMessages(MessageListKey messageListKey)
    {
        return messageService.getMessages(messageListKey.page, messageListKey.perPage);
    }

    public MiddleCallbackMessageList getMessages(MessageListKey messageListKey, Callback<PaginatedDTO<MessageDTO>> callback)
    {
        MiddleCallbackMessageList middleCallback = new MiddleCallbackMessageList(callback);
        messageServiceAsync.getMessages(messageListKey.page, messageListKey.perPage, middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    //<editor-fold desc="Create Message">
    public DiscussionDTO createMessage(MessageDTO form)
    {
        return messageService.createMessage(form);
    }

    public MiddleCallbackDiscussion createMessage(MessageDTO form, Callback<DiscussionDTO> callback)
    {
        MiddleCallbackDiscussion middleCallback = new MiddleCallbackDiscussion(callback);
        messageServiceAsync.createMessage(form, middleCallback);
        return middleCallback;
    }
    //</editor-fold>
}
