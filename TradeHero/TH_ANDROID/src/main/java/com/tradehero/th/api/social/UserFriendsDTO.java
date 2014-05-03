package com.tradehero.th.api.social;

import com.tradehero.th.api.ExtendedDTO;
import com.tradehero.th.loaders.ContactEntry;
import com.tradehero.th.utils.Constants;


public class UserFriendsDTO extends ExtendedDTO
{
    public static final String TAG = UserFriendsDTO.class.getSimpleName();
    private static final String PROPERTY_KEY_SELECTED = TAG + ".selected";
    private static final String PROPERTY_KEY_EMAIL = TAG + ".email";

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

    public boolean isSelected()
    {
        Boolean selected = (Boolean) get(PROPERTY_KEY_SELECTED);
        return selected == null ? false : selected;
    }

    public void setSelected(boolean isSelected)
    {
        put(PROPERTY_KEY_SELECTED, isSelected);
    }

    public void setEmail(String email)
    {
        put(PROPERTY_KEY_EMAIL, email);
    }

    public String getEmail()
    {
        return (String) get(PROPERTY_KEY_EMAIL);
    }

    public static UserFriendsDTO parse(ContactEntry contactEntry)
    {
        UserFriendsDTO userFriendsDTO = new UserFriendsDTO();
        userFriendsDTO.name = contactEntry.getName();
        userFriendsDTO.setEmail(contactEntry.getEmail());
        return userFriendsDTO;
    }
}
