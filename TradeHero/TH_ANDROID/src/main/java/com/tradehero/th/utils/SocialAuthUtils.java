package com.tradehero.th.utils;

import android.app.Activity;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.misc.callback.LogInCallback;
import rx.Observable;

public interface SocialAuthUtils
{
    void logIn(Activity activity, LogInCallback callback);
}
