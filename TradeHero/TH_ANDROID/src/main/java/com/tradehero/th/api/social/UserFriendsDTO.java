package com.tradehero.th.api.social;

import com.tradehero.th.api.ExtendedDTO;
import com.tradehero.th.loaders.ContactEntry;
import com.tradehero.th.utils.Constants;

public class UserFriendsDTO extends ExtendedDTO implements Comparable<UserFriendsDTO>
{
    private static final String PROPERTY_KEY_SELECTED = UserFriendsDTO.class.getSimpleName() + ".selected";
    private static final String PROPERTY_KEY_EMAIL = UserFriendsDTO.class.getSimpleName() + ".email";

    public String name;       // name

    public String fbId;       // FB id
    public String liId;       // or LI id
    public String twId;
    public String wbId;

    public String liPicUrl;   // LI gives is pics (FB pics can be dynamically gen'd)
    public String liHeadline; // LI: gives current position/title?

    public boolean alreadyInvited; //has an invitation been sent already

    public int thUserId;

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

    public boolean isTradeHeroUser()
    {
        return thUserId > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserFriendsDTO that = (UserFriendsDTO) o;

        if (thUserId != that.thUserId) return false;
        if (fbId != null ? !fbId.equals(that.fbId) : that.fbId != null) return false;
        if (liId != null ? !liId.equals(that.liId) : that.liId != null) return false;
        if (liPicUrl != null ? !liPicUrl.equals(that.liPicUrl) : that.liPicUrl != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (twId != null ? !twId.equals(that.twId) : that.twId != null) return false;
        if (wbId != null ? !wbId.equals(that.wbId) : that.wbId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (fbId != null ? fbId.hashCode() : 0);
        result = 31 * result + (liId != null ? liId.hashCode() : 0);
        result = 31 * result + (twId != null ? twId.hashCode() : 0);
        result = 31 * result + (wbId != null ? wbId.hashCode() : 0);
        result = 31 * result + (liPicUrl != null ? liPicUrl.hashCode() : 0);
        result = 31 * result + thUserId;
        return result;
    }

    @Override
    public int compareTo(UserFriendsDTO another) {
        if (isTradeHeroUser())
        {
            if (!another.isTradeHeroUser())
            {
                return -1;
            }
            else
            {
                return name.compareToIgnoreCase(another.name);
            }
        }
        else
        {
            if (another.isTradeHeroUser())
            {
                return 1;
            }
            else
            {
                return name.compareToIgnoreCase(another.name);
            }

        }

    }
}
