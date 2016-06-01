package com.ayondo.academy.network.service;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.auth.AccessTokenForm;
import com.ayondo.academy.api.users.UserProfileDTO;
import rx.Observable;

public interface SocialLinker
{
    @NonNull Observable<UserProfileDTO> link(AccessTokenForm userFormDTO);
}
