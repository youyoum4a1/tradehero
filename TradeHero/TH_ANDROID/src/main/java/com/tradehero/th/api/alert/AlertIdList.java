package com.tradehero.th.api.alert;

import com.tradehero.common.persistence.DTOKeyIdList;
import com.tradehero.th.api.users.UserBaseKey;
import java.util.Collection;

public class AlertIdList extends DTOKeyIdList<AlertId>
{
    //<editor-fold desc="Constructors">
    public AlertIdList()
    {
        super();
    }

    public AlertIdList(int capacity)
    {
        super(capacity);
    }

    public AlertIdList(Collection<? extends AlertId> collection)
    {
        super(collection);
    }

    public AlertIdList(UserBaseKey userBaseKey, Collection<? extends AlertCompactDTO> alertCompactDTOs)
    {
        for (AlertCompactDTO compactDTO: alertCompactDTOs)
        {
            add(new AlertId(userBaseKey, compactDTO.id));
        }
    }
    //</editor-fold>

    @Override public boolean add(AlertId object)
    {
        return super.add(object);
    }
}
