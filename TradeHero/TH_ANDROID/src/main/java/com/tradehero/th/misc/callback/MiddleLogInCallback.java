package com.tradehero.th.misc.callback;

import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.misc.exception.THException;
import org.jetbrains.annotations.Nullable;

public class MiddleLogInCallback extends LogInCallback
{
    @Nullable private LogInCallback innerCallback;

    //<editor-fold desc="Constructors">
    public MiddleLogInCallback(@Nullable LogInCallback innerCallback)
    {
        this.innerCallback = innerCallback;
    }
    //</editor-fold>

    public void setInnerCallback(@Nullable LogInCallback innerCallback)
    {
        this.innerCallback = innerCallback;
    }

    @Override public void onStart()
    {
        LogInCallback callbackCopy = innerCallback;
        if (callbackCopy != null)
        {
            callbackCopy.onStart();
        }
    }

    @Override public boolean onSocialAuthDone(JSONCredentials json)
    {
        LogInCallback callbackCopy = innerCallback;
        if (callbackCopy != null)
        {
            return callbackCopy.onSocialAuthDone(json);
        }
        return super.onSocialAuthDone(json);
    }

    @Override public void done(UserLoginDTO user, THException ex)
    {
        LogInCallback callbackCopy = innerCallback;
        if (callbackCopy != null)
        {
            callbackCopy.done(user, ex);
        }
    }
}
