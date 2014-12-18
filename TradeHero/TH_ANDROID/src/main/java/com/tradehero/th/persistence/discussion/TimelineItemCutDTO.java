package com.tradehero.th.persistence.discussion;

import com.tradehero.th.api.security.SecurityMediaDTOList;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.persistence.user.UserProfileCompactCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

class TimelineItemCutDTO extends AbstractDiscussionCutDTO
{
    public int type;
    public Date userViewedAtUtc;
    private SecurityMediaDTOList medias;
    public Integer pushTypeId;

    public boolean useSysIcon;
    public boolean renderSysStyle;
    public String imageUrl;

    private UserBaseKey user;

    public String header;
    public boolean isQuestionItem;
    public Integer prizeAmount;
    public boolean isAnswered;
    public boolean isDeleted;

    TimelineItemCutDTO(
            @NotNull TimelineItemDTO timelineItemDTO,
            @NotNull UserProfileCompactCache userProfileCompactCache)
    {
        super(timelineItemDTO);
        this.type = timelineItemDTO.type;
        this.createdAtUtc = timelineItemDTO.createdAtUtc;
        this.medias = timelineItemDTO.getMedias();
        this.pushTypeId = timelineItemDTO.pushTypeId;
        this.useSysIcon = timelineItemDTO.useSysIcon;
        this.renderSysStyle = timelineItemDTO.renderSysStyle;
        this.imageUrl = timelineItemDTO.imageUrl;
        this.header = timelineItemDTO.header;
        this.isQuestionItem = timelineItemDTO.isQuestionItem;
        this.prizeAmount = timelineItemDTO.prizeAmount;
        this.isAnswered = timelineItemDTO.isAnswered;
        this.isDeleted = timelineItemDTO.isDeleted;
        UserProfileCompactDTO userCompact = timelineItemDTO.getUser();
        if (userCompact != null)
        {
            this.user = userCompact.getBaseKey();
            userProfileCompactCache.put(this.user, userCompact);
        }
    }

    @Nullable TimelineItemDTO inflate(@NotNull UserProfileCompactCache userProfileCompactCache)
    {
        TimelineItemDTO inflated = new TimelineItemDTO();
        if (!populate(inflated, userProfileCompactCache))
        {
            return null;
        }
        return inflated;
    }


    boolean populate(
            @NotNull TimelineItemDTO inflated,
            @NotNull UserProfileCompactCache userProfileCompactCache)
    {
        if (!super.populate(inflated))
        {
            return false;
        }
        inflated.type = this.type;
        inflated.createdAtUtc = this.createdAtUtc;
        inflated.setMedias(this.medias);
        inflated.pushTypeId = this.pushTypeId;
        inflated.useSysIcon = this.useSysIcon;
        inflated.renderSysStyle = this.renderSysStyle;
        inflated.imageUrl = this.imageUrl;
        inflated.header = this.header;
        inflated.isQuestionItem = this.isQuestionItem;
        inflated.prizeAmount = this.prizeAmount;
        inflated.isAnswered = this.isAnswered;
        inflated.isDeleted = this.isDeleted;
        if (this.user != null)
        {
            inflated.setUser(userProfileCompactCache.get(this.user));
            if (inflated.getUser() == null)
            {
                return false;
            }
        }
        return true;
    }
}
