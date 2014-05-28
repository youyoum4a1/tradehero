package com.tradehero.th.api.share.wechat;

import com.tradehero.th.api.discussion.DiscussionDTO;
import javax.inject.Inject;

public class WeChatDTOFactory
{
    @Inject public WeChatDTOFactory()
    {
    }

    public WeChatDTO createFrom(DiscussionDTO discussionDTO)
    {
        return new WeChatDTO(
                discussionDTO.getDiscussionKey().id,
                WeChatMessageType.CreateDiscussion,
                discussionDTO.text);
    }
}
