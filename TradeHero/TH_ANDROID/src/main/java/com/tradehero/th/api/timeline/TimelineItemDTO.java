package com.tradehero.th.api.timeline;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.SecurityMediaDTO;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TimelineItemDTO extends AbstractDiscussionDTO
{
    public int type;
    public Date userViewedAtUtc;
    private List<SecurityMediaDTO> medias;
    public Integer pushTypeId;

    public boolean useSysIcon;
    public boolean renderSysStyle;
    public String imageUrl;

    private UserProfileCompactDTO user;

    public TimelineItemDTO()
    {
    }

    public List<SecurityMediaDTO> getMedias()
    {
        return Collections.unmodifiableList(medias);
    }

    public void setMedias(List<SecurityMediaDTO> medias)
    {
        this.medias = medias;
    }

    public List<SecurityId> getMediaSecurityIds()
    {
        if (medias == null)
        {
            return null;
        }
        List<SecurityId> securityIds = new ArrayList<>();
        for (SecurityMediaDTO securityMediaDTO : medias)
        {
            if (securityMediaDTO != null &&
                    securityMediaDTO.hasValidSecurityId())
            {
                securityIds.add(securityMediaDTO.createSecurityId());
            }
        }
        return securityIds;
    }

    @JsonIgnore
    public SecurityMediaDTO getFlavorSecurityForDisplay()
    {
        SecurityMediaDTO securityMediaDTO = null;
        for (SecurityMediaDTO m: medias)
        {
            if (m.securityId != 0)
            {
                securityMediaDTO = m;
            }

            // we prefer the first security with photo
            if (securityMediaDTO != null && securityMediaDTO.url != null)
            {
                return securityMediaDTO;
            }
        }
        return securityMediaDTO;
    }

    public SecurityId createFlavorSecurityIdForDisplay()
    {
        SecurityMediaDTO securityMediaDTO = getFlavorSecurityForDisplay();
        if (securityMediaDTO == null)
        {
            return null;
        }
        return securityMediaDTO.createSecurityId();
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