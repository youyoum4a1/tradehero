package com.androidth.general.api.timeline;

import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.androidth.general.api.ExtendedDTO;
import com.androidth.general.api.discussion.AbstractDiscussionDTO;
import com.androidth.general.api.security.SecurityId;
import com.androidth.general.api.security.SecurityMediaDTO;
import com.androidth.general.api.security.SecurityMediaDTOList;
import com.androidth.general.api.timeline.key.TimelineItemDTOKey;
import com.androidth.general.api.users.UserProfileCompactDTO;
import java.util.Date;

public class TimelineItemDTO extends AbstractDiscussionDTO<TimelineItemDTO>
{
    public TimeLineItemType type;
    public Date userViewedAtUtc;
    private SecurityMediaDTOList medias;
    public Integer pushTypeId;

    public boolean useSysIcon;
    public boolean renderSysStyle;
    public String imageUrl;
    @Nullable public Integer tradeId;

    private UserProfileCompactDTO user;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration") // Needed for deserialiser
    TimelineItemDTO()
    {
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

    @JsonIgnore
    @Nullable public SecurityMediaDTO getFlavorSecurityForDisplay()
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
}