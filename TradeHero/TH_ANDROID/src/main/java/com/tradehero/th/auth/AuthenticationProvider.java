package com.ayondo.academy.auth;

import android.app.Activity;
import rx.Observable;

public interface AuthenticationProvider
{
    Observable<AuthData> logIn(Activity activity);

    void logout();
}
