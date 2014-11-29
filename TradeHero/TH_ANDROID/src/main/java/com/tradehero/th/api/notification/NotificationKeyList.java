package com.tradehero.th.api.notification;

import android.support.annotation.NonNull;
import com.tradehero.common.persistence.DTOKeyIdList;
import java.util.List;

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
