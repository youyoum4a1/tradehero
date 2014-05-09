package com.tradehero.th.misc.callback;

import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.base.JSONCredentials;
import com.tradehero.th.misc.exception.THException;

public abstract class LogInCallback
{
    public abstract void done(UserLoginDTO user, THException ex);

    public abstract void onStart();

    /**
     * @param json data from social network such as twitter, facebook, linkedin
     * @return boolean Whether to continue authentication with tradehero server after getting json data
     */
    public boolean onSocialAuthDone(JSONCredentials json)
    {
        return true;
    }
}
