package com.androidth.general.models.discussion;

import android.support.annotation.NonNull;
import com.androidth.general.api.alert.AlertId;
import com.androidth.general.api.discussion.AbstractDiscussionCompactDTO;

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
