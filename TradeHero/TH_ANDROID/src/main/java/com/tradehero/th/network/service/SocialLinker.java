package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.auth.AccessTokenForm;
import com.tradehero.th.api.users.UserProfileDTO;
import rx.Observable;

public interface SocialLinker
{
    @NonNull Observable<UserProfileDTO> link(AccessTokenForm userFormDTO);
}
