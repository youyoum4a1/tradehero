package com.tradehero.th.models.social;

import com.tradehero.th.api.users.UserBaseKey;
import android.support.annotation.NonNull;

public interface OnFreeFollowRequestedListener
{
    void freeFollowRequested(@NonNull UserBaseKey heroId);
}
