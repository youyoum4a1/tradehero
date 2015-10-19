package com.tradehero.th.api.timeline;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.th.api.ExtendedDTO;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityMediaDTO;
import com.tradehero.th.api.security.SecurityMediaDTOList;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public class TimelineItemDTO extends AbstractDiscussionDTO
{
    public int tradeId = 0;
    public int type;
    public Date createdAtUtc;//userViewedAtUtc;
    private SecurityMediaDTOList medias;
    public Integer pushTypeId;

    public boolean useSysIcon;
    public boolean renderSysStyle;
    public String imageUrl;
    public String picUrl;

    public UserProfileCompactDTO user;
    public boolean isHighlight;

    public Date lastCommentAtUtc;//最后回复时间

    public boolean isEssential = false;
    public int stickType;
    public boolean isNotice;
    public boolean isGuide;

    /*Reward tips*/
    public String header;
    public boolean isQuestionItem;
    public Integer prizeAmount;
    public boolean isAnswered;
    public boolean isDeleted;


    //<editor-fold desc="Constructors">
    public TimelineItemDTO()
    {
        super();
    }

    public <ExtendedDTOType extends ExtendedDTO> TimelineItemDTO(ExtendedDTOType other,
            Class<? extends TimelineItemDTO> myClass)
    {
        super(other, myClass);
    }
    //</editor-fold>

    public void setMedias(SecurityMediaDTOList medias)
    {
        this.medias = medias;
    }

    public SecurityMediaDTOList getMedias()
    {
        return medias;
    }

    @JsonIgnore
    public SecurityMediaDTO getFlavorSecurityForDisplay()
    {
        if (medias == null)
        {
            return null;
        }
        return medias.getFlavorSecurityForDisplay();
    }

    @Nullable public SecurityId createFlavorSecurityIdForDisplay()
    {
        if (medias == null)
        {
            return null;
        }
        return medias.createFlavorSecurityIdForDisplay();
    }

    public void setUser(UserProfileCompactDTO user)
    {
        this.user = user;
    }

    public UserProfileCompactDTO getUser()
    {
        return user;
    }

    @Override public TimelineItemDTOKey getDiscussionKey()
    {
        return new TimelineItemDTOKey(id);
    }

    public boolean hasTrader()
    {
        return tradeId != 0;
    }

    public String getRewardString()
    {
        if(!isQuestionItem)return "";
        if(isAnswered)return "已采纳";
        else
        {
            return "悬赏$"+prizeAmount;
        }
    }
}