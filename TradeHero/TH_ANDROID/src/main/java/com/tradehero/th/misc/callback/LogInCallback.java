package com.tradehero.th.misc.callback;

import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.misc.exception.THException;
import org.json.JSONObject;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 1:08 PM Copyright (c) TradeHero */
public abstract class LogInCallback
{
    public abstract void done(UserLoginDTO user, THException ex);

    public abstract void onStart();

    /**
     * @param json data from social network such as twitter, facebook, linkedin
     * @return boolean Whether to continue authentication with tradehero server after getting json data
     */
    public boolean onSocialAuthDone(JSONObject json)
    {
        return true;
    }
}
