package com.tradehero.th.auth;

import android.app.Activity;
import com.tradehero.th.api.users.UserProfileDTO;
import org.jetbrains.annotations.NotNull;
import rx.Observable;

public interface AuthenticationProvider
{
    Observable<AuthData> logIn(Activity activity);

    Observable<UserProfileDTO> socialLink(@NotNull Activity activity);
}
