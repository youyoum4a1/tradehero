package com.androidth.general.network.service;

import android.support.annotation.NonNull;
import com.androidth.general.api.auth.AccessTokenForm;
import com.androidth.general.api.users.UserProfileDTO;
import rx.Observable;

public interface SocialLinker
{
    @NonNull Observable<UserProfileDTO> link(AccessTokenForm userFormDTO);
}
