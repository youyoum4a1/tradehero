package com.tradehero.th.network.service;

import com.sun.swing.internal.plaf.metal.resources.metal_sv;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.common.persistence.prefs.LongPreference;
import com.tradehero.th.api.PaginatedDTO;
import com.tradehero.th.api.PaginationInfoDTO;
import com.tradehero.th.api.messages.MessageDTO;
import com.tradehero.th.api.messages.MessageDetailDTO;
import com.tradehero.th.api.notification.NotificationDTO;
import com.tradehero.th.persistence.MessageListTimeline;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;

/**
 * Created by wangliang on 3/4/14.
 */
@Singleton
public class MessageServiceWrapper
{
    MessageService messageService;
    LongPreference timelinePreference;

    @Inject MessageServiceWrapper(MessageService messageService, @MessageListTimeline
    LongPreference timelinePreference)
    {
        this.messageService = messageService;
        this.timelinePreference = timelinePreference;
    }

    public PaginatedDTO<MessageDTO> getMessages(int page, int perPage, long timeline)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("page", String.valueOf(page));
        map.put("perPage", String.valueOf(perPage));
        map.put("timeline", String.valueOf(timeline));
        return messageService.getMessages(map);
    }

    public PaginatedDTO<MessageDTO> getMessages(int page, int perPage)
    {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("page", String.valueOf(page));
        map.put("perPage", String.valueOf(perPage));
        Long timeline = timelinePreference.get();
        timeline = (timeline == null) ? -1 : timeline;
        map.put("timeline", String.valueOf(timeline));

        return messageService.getMessages(map);
    }

    public PaginatedDTO<MessageDTO> getMessages(Map<String, Object> options)
    {
        return getFakeData((Integer)options.get("page"));
        //return messageService.getMessages(options);
    }

    public MessageDetailDTO getMessageDetail(int msgId)
    {
        return messageService.getMessageDetail(msgId);
    }

    private void saveTimeline(long timeline)
    {
        timelinePreference.set(timeline);
    }

    private PaginatedDTO<MessageDTO> getFakeData(int page)
    {
        PaginatedDTO paginatedDTO = new PaginatedDTO<MessageDTO>();
        List<MessageDTO> messsageDTOList = new ArrayList<MessageDTO>();
        Date date = new Date();
        for(int i=0;i<40;i++)
        {
            messsageDTOList.add(new MessageDTO(page+i,null,"title-"+i+"-"+page,"text-"+i,date));
        }

        paginatedDTO.setData(messsageDTOList);

        PaginationInfoDTO paginationInfoDTO = new PaginationInfoDTO();
        paginatedDTO.setPagination(paginationInfoDTO);

        return paginatedDTO;
    }
}
