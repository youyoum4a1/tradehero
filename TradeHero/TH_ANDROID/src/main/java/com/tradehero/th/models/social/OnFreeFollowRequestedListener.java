package com.tradehero.th.models.social;

import android.support.annotation.NonNull;
import com.tradehero.th.api.users.UserBaseKey;

public interface OnFreeFollowRequestedListener
{
    void freeFollowRequested(@NonNull UserBaseKey heroId);
}
