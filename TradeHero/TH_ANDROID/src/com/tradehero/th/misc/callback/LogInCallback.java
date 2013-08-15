package com.tradehero.th.misc.callback;

import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.misc.exception.THException;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 1:08 PM Copyright (c) TradeHero */
public abstract class LogInCallback implements THCallback<UserBaseDTO>
{
    public abstract void done(UserBaseDTO user, THException ex);

    @Override public void internalDone(UserBaseDTO user, THException ex)
    {
        done(user, ex);
    }

    public abstract void onStart();
}
