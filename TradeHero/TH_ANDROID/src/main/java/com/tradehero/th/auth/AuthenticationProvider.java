package com.tradehero.th.auth;

import android.app.Activity;
import android.content.Intent;
import rx.Observable;

public interface AuthenticationProvider
{
    Observable<AuthData> logIn(Activity activity);

    void onActivityResult(int requestCode, int resultCode, Intent data);

    void logout();
}
