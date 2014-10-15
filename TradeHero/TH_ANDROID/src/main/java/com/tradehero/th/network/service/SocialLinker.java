package com.tradehero.th.network.service;

import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.auth.AccessTokenForm;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

public interface SocialLinker
{
    @NotNull Observable<UserProfileDTO> link(AccessTokenForm userFormDTO);
}
