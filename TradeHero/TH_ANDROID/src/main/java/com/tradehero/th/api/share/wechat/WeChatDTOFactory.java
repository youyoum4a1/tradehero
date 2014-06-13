package com.tradehero.th.api.share.wechat;

import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.security.SecurityMediaDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import javax.inject.Inject;

public class WeChatDTOFactory
{
    @Inject public WeChatDTOFactory()
    {
    }

    public WeChatDTO createFrom(AbstractDiscussionCompactDTO abstractDiscussionCompactDTO)
    {
        if (abstractDiscussionCompactDTO == null)
        {
            return null;
        }
        WeChatDTO weChatDTO = new WeChatDTO();
        populateWith(weChatDTO, abstractDiscussionCompactDTO);
        return weChatDTO;
    }

    protected void populateWith(WeChatDTO weChatDTO, AbstractDiscussionCompactDTO abstractDiscussionCompactDTO)
    {
        weChatDTO.id = abstractDiscussionCompactDTO.id;
        if (abstractDiscussionCompactDTO instanceof DiscussionDTO)
        {
            weChatDTO.type = WeChatMessageType.CreateDiscussion;
            weChatDTO.title = ((DiscussionDTO) abstractDiscussionCompactDTO).text;
            if (((DiscussionDTO) abstractDiscussionCompactDTO).user != null &&
                    ((DiscussionDTO) abstractDiscussionCompactDTO).user.picture != null)
            {
                weChatDTO.imageURL = ((DiscussionDTO) abstractDiscussionCompactDTO).user.picture;
            }
        }
        else if (abstractDiscussionCompactDTO instanceof NewsItemCompactDTO)
        {
            weChatDTO.type = WeChatMessageType.News;
            weChatDTO.title = ((NewsItemCompactDTO) abstractDiscussionCompactDTO).title;
        }
        else if (abstractDiscussionCompactDTO instanceof NewsItemDTO)
        {
            weChatDTO.type = WeChatMessageType.News;
            weChatDTO.title = ((NewsItemDTO) abstractDiscussionCompactDTO).title;

        }
        else if (abstractDiscussionCompactDTO instanceof TimelineItemDTO)
        {
            weChatDTO.type = WeChatMessageType.Discussion;
            weChatDTO.title = ((TimelineItemDTO) abstractDiscussionCompactDTO).text;
            SecurityMediaDTO firstMediaWithLogo = ((TimelineItemDTO) abstractDiscussionCompactDTO).getFlavorSecurityForDisplay();
            if (firstMediaWithLogo != null && firstMediaWithLogo.url != null)
            {
                weChatDTO.imageURL = firstMediaWithLogo.url;
            }
        }
    }
}
