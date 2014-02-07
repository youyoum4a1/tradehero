package com.tradehero.th.api.alert;

import com.tradehero.common.persistence.DTOKeyIdList;
import com.tradehero.th.api.users.UserBaseKey;
import java.util.Collection;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 7:03 PM To change this template use File | Settings | File Templates. */
public class AlertIdList extends DTOKeyIdList<AlertId>
{
    public static final String TAG = AlertIdList.class.getSimpleName();

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
