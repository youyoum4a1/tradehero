package com.tradehero.th.api.users;

/** Created with IntelliJ IDEA. User: xavier Date: 11/14/13 Time: 10:47 AM To change this template use File | Settings | File Templates. */
public class CurrentUserBaseKeyHolder
{
    public static final String TAG = CurrentUserBaseKeyHolder.class.getSimpleName();

    public CurrentUserBaseKeyHolder()
    {
        super();
    }

    private UserBaseKey currentUserBaseKey;

    public UserBaseKey getCurrentUserBaseKey()
    {
        return currentUserBaseKey;
    }

    public void setCurrentUserBaseKey(UserBaseKey currentUserBaseKey)
    {
        this.currentUserBaseKey = currentUserBaseKey;
    }
}
