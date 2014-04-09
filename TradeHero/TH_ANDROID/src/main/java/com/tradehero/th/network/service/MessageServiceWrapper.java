package com.tradehero.th.network.service;

import com.tradehero.common.persistence.prefs.LongPreference;
import com.tradehero.th.api.PaginatedDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.MessageHeaderDTO;
import com.tradehero.th.api.discussion.key.MessageListKey;
import com.tradehero.th.models.discussion.MiddleCallbackDiscussion;
import com.tradehero.th.models.discussion.MiddleCallbackMessageList;
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

//    public PaginatedDTO<MessageDTO> getMessages(int page, int perPage, long timeline)
//    {
//        Map<String, Object> map = new HashMap<String, Object>(3);
//        map.put("page", String.valueOf(page));
//        map.put("perPage", String.valueOf(perPage));
//        map.put("timeline", String.valueOf(timeline));
//        return messageService.getMessages(map);
//    }
//
//    public PaginatedDTO<MessageDTO> getMessages(int page, int perPage)
//    {
//        Map<String, Object> map = new HashMap<String, Object>(3);
//        map.put("page", String.valueOf(page));
//        map.put("perPage", String.valueOf(perPage));
//        Long timeline = timelinePreference.get();
//        timeline = (timeline == null) ? -1 : timeline;
//        map.put("timeline", String.valueOf(timeline));
//
//        //return messageService.getMessages(map);
//        return getFakeData(page);
//    }
//
//    public PaginatedDTO<MessageDTO> getMessages(Map<String, Object> options)
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

    public MiddleCallbackMessageList getMessages(MessageListKey messageListKey, Callback<PaginatedDTO<MessageHeaderDTO>> callback)
    {
        MiddleCallbackMessageList middleCallback = new MiddleCallbackMessageList(callback);
        messageServiceAsync.getMessages(messageListKey.page, messageListKey.perPage, middleCallback);
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
    //</editor-fold>
}
