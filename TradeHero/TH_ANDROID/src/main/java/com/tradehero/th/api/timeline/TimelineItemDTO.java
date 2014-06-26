package com.tradehero.th.api.timeline;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.th.api.ExtendedDTO;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityIdList;
import com.tradehero.th.api.security.SecurityMediaDTO;
import com.tradehero.th.api.security.SecurityMediaDTOList;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import java.util.Date;
import org.jetbrains.annotations.Nullable;

public class TimelineItemDTO extends AbstractDiscussionDTO
{
    public int type;
    public Date userViewedAtUtc;
    private SecurityMediaDTOList medias;
    public Integer pushTypeId;

    public boolean useSysIcon;
    public boolean renderSysStyle;
    public String imageUrl;

    private UserProfileCompactDTO user;

    //<editor-fold desc="Constructors">
    public TimelineItemDTO()
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

    public SecurityIdList getMediaSecurityIds()
    {
        if (medias == null)
        {
            return null;
        }
        return medias.getMediaSecurityIds();
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
}