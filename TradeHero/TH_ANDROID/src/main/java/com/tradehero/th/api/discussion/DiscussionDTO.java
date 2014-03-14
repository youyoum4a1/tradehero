package com.tradehero.th.api.discussion;

import com.tradehero.th.api.users.UserBaseDTO;

/**
 * Created by xavier on 3/7/14.
 */
public class DiscussionDTO extends AbstractDiscussionDTO
{
    public static final String TAG = DiscussionDTO.class.getSimpleName();

    public String type; //probably not needed - should always be `DiscussionTypeEnum.COMMENT`

    public UserBaseDTO user;
    public DiscussionType inReplyToType;
    public int inReplyToId;

    public String url; // to post a link

    // BEGIN: duplicated from buy/sell
    public Boolean publishToFb;
    public Boolean publishToLi;
    public Boolean publishToTw;
    public String geo_alt;
    public String geo_lat;
    public String geo_long;
    public boolean isPublic;
    // END: duplicated from buy/sell
}
