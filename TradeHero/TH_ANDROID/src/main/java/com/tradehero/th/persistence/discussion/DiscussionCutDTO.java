package com.tradehero.th.persistence.discussion;

import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.users.UserBaseDTO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class DiscussionCutDTO extends AbstractDiscussionCutDTO
{
    public DiscussionType type;
    public UserBaseDTO user;
    public int inReplyToId;
    public DiscussionType inReplyToType;

    public String url; // to post a link

    public Integer sentToCount;         // only applicable to Messages

    // BEGIN: duplicated from buy/sell
    public Boolean publishToFb;
    public Boolean publishToLi;
    public Boolean publishToTw;
    public Boolean publishToWb;
    public String geo_alt;
    public String geo_lat;
    public String geo_long;
    public boolean isPublic;

    DiscussionCutDTO(@NotNull DiscussionDTO discussionDTO)
    {
        super(discussionDTO);
        this.type = discussionDTO.type;
        this.user = discussionDTO.user;
        this.inReplyToId = discussionDTO.inReplyToId;
        this.inReplyToType = discussionDTO.inReplyToType;
        this.url = discussionDTO.url;
        this.sentToCount = discussionDTO.sentToCount;
        this.publishToFb = discussionDTO.publishToFb;
        this.publishToLi = discussionDTO.publishToLi;
        this.publishToTw = discussionDTO.publishToTw;
        this.publishToWb = discussionDTO.publishToWb;
        this.geo_alt = discussionDTO.geo_alt;
        this.geo_lat = discussionDTO.geo_lat;
        this.geo_long = discussionDTO.geo_long;
        this.isPublic = discussionDTO.isPublic;
    }

    @Nullable DiscussionDTO inflate()
    {
        DiscussionDTO inflated = new DiscussionDTO();
        if (!populate(inflated))
        {
            return null;
        }
        return inflated;
    }

    final boolean populate(@NotNull DiscussionDTO inflated)
    {
        if (!super.populate(inflated))
        {
            return false;
        }
        inflated.type = this.type;
        inflated.user = this.user;
        inflated.inReplyToId = this.inReplyToId;
        inflated.inReplyToType = this.inReplyToType;
        inflated.url = this.url;
        inflated.sentToCount = this.sentToCount;
        inflated.publishToFb = this.publishToFb;
        inflated.publishToLi = this.publishToLi;
        inflated.publishToTw = this.publishToTw;
        inflated.publishToWb = this.publishToWb;
        inflated.geo_alt = this.geo_alt;
        inflated.geo_lat = this.geo_lat;
        inflated.geo_long = this.geo_long;
        inflated.isPublic = this.isPublic;
        return true;
    }
}
