package com.tradehero.th.api.social;

import com.tradehero.th.utils.Constants;

/** Created with IntelliJ IDEA. User: xavier Date: 10/22/13 Time: 8:53 PM To change this template use File | Settings | File Templates. */
public class UserFriendsDTO
{
    public static final String TAG = UserFriendsDTO.class.getSimpleName();

    public String name;       // name

    public String fbId;       // FB id
    public String liId;       // or LI id

    public String liPicUrl;   // LI gives is pics (FB pics can be dynamically gen'd)
    public String liHeadline; // LI: gives current position/title?

    public boolean alreadyInvited; //has an invitation been sent already

    public UserFriendsDTO()
    {
        super();
    }

    public String getProfilePictureURL()
    {
        if (fbId != null && !fbId.isEmpty())
        {
            return String.format(Constants.FACEBOOK_PROFILE_PICTURE, fbId);
        }
        else if (liPicUrl != null && !liPicUrl.isEmpty())
        {
            return liPicUrl;
        }
        return null;
    }
}
