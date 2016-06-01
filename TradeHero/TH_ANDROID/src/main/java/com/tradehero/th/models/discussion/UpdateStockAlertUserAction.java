package com.ayondo.academy.models.discussion;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.alert.AlertId;
import com.ayondo.academy.api.discussion.AbstractDiscussionCompactDTO;

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
