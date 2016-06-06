package com.androidth.general.api.security;

import android.support.annotation.NonNull;
import com.androidth.general.common.api.BaseArrayList;
import com.androidth.general.common.persistence.ContainerDTO;
import com.androidth.general.common.persistence.DTO;
import java.util.ArrayList;
import java.util.List;

public class SecurityCompactDTOList extends BaseArrayList<SecurityCompactDTO>
    implements DTO, ContainerDTO<SecurityCompactDTO, SecurityCompactDTOList>
{
    //<editor-fold desc="Constructors">
    public SecurityCompactDTOList()
    {
        super();
    }

    @Override public SecurityCompactDTOList getList()
    {
        return this;
    }
    //</editor-fold>

    @NonNull public List<SecurityId> getSecurityIds()
    {
        List<SecurityId> ids = new ArrayList<>();
        for (SecurityCompactDTO dto : this)
        {
            ids.add(dto.getSecurityId());
        }
        return ids;
    }
}
