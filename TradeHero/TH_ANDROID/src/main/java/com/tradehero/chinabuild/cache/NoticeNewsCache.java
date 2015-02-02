package com.tradehero.chinabuild.cache;

import com.tradehero.th.api.timeline.TimelineDTO;

/**
 * Created by palmer on 15/2/2.
 */
public class NoticeNewsCache {

    private static NoticeNewsCache noticeNewsCache;

    private TimelineDTO timelineDTO;

    private NoticeNewsCache(){}

    public static NoticeNewsCache getInstance(){
        synchronized (NoticeNewsCache.class){
            if(noticeNewsCache == null){
                noticeNewsCache = new NoticeNewsCache();
            }
            return noticeNewsCache;
        }
    }

    public TimelineDTO getTimelineDTO() {
        return timelineDTO;
    }

    public void setTimelineDTO(TimelineDTO timelineDTO) {
        this.timelineDTO = timelineDTO;
    }
}
