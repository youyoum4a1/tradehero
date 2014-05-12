package com.tradehero.th.billing;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;

public interface OnFollowResultListener
{
    void onFollowSuccessful(UserBaseKey userBaseKey, UserProfileDTO userProfileDTO);
    void onFollowFailed(UserBaseKey userBaseKey, Exception exception);
}
