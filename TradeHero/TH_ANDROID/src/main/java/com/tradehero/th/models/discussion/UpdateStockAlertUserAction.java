package com.tradehero.th.models.discussion;

import android.support.annotation.NonNull;
import com.tradehero.th.api.alert.AlertId;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;

public class UpdateStockAlertUserAction extends UserDiscussionAction
{
    @NonNull public final AlertId alertId;

    public UpdateStockAlertUserAction(
            @NonNull AbstractDiscussionCompactDTO discussionDTO,
            @NonNull AlertId alertId)
    {
        super(discussionDTO);
        this.alertId = alertId;
    }
}
