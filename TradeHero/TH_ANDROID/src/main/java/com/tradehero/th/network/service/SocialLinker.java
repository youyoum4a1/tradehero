package com.tradehero.th.network.service;

import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.auth.AccessTokenForm;
import android.support.annotation.NonNull;
import rx.Observable;

public interface SocialLinker
{
    @NonNull Observable<UserProfileDTO> link(AccessTokenForm userFormDTO);
}
