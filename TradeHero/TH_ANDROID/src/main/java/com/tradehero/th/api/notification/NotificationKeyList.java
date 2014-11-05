package com.tradehero.th.api.notification;

import com.tradehero.common.persistence.DTOKeyIdList;
import java.util.List;
import android.support.annotation.NonNull;

public class NotificationKeyList extends DTOKeyIdList<NotificationKey>
{
    //<editor-fold desc="Constructors">
    public NotificationKeyList()
    {
        super();
    }

    public NotificationKeyList(@NonNull List<NotificationDTO> notificationDTOs)
    {
        for (NotificationDTO notificationDTO : notificationDTOs)
        {
            add(notificationDTO.getDTOKey());
        }
    }
    //</editor-fold>
}
