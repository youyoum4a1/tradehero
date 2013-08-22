package com.tradehero.th.misc.callback;

import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.misc.exception.THException;
import org.json.JSONObject;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 1:08 PM Copyright (c) TradeHero */
public abstract class LogInCallback implements THCallback<UserBaseDTO>
{
    public abstract void done(UserBaseDTO user, THException ex);

    @Override public void internalDone(UserBaseDTO user, THException ex)
    {
        done(user, ex);
    }

    public void onStart()
    {
        // do nothing for now
    }

    /**
     *
     * @param json data from social network such as twitter, facebook, linkedin
     * @return boolean Whether to continue authentication with th-server
     */
    public boolean onSocialAuthDone(JSONObject json)
    {
        return true;
    }
}
