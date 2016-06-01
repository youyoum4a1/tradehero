package com.ayondo.academy.api.users;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.ayondo.academy.R;

public class UserBaseDTOUtil
{
    @NonNull public static String getLongDisplayName(@NonNull Resources resources, @Nullable UserBaseDTO userBaseDTO)
    {
        if (userBaseDTO != null)
        {
            if (userBaseDTO.firstName != null &&
                !userBaseDTO.firstName.isEmpty() &&
                userBaseDTO.lastName != null &&
                !userBaseDTO.lastName.isEmpty())
            {
                return getFirstLastName(resources, userBaseDTO);
            }

            return userBaseDTO.displayName;
        }

        return resources.getString(R.string.na);
    }

    //return displayName in Follow and Hero ListItemView as https://www.pivotaltracker.com/story/show/76497256
    @NonNull public static String getShortDisplayName(@NonNull Resources resources, @Nullable UserBaseDTO userBaseDTO)
    {
        if (userBaseDTO != null)
        {
            return userBaseDTO.displayName;
        }

        return resources.getString(R.string.na);
    }

    @NonNull public static String getFirstLastName(@NonNull Resources resources, @Nullable UserBaseDTO userBaseDTO)
    {
        if (userBaseDTO != null)
        {
            return String.format(resources.getString(R.string.user_profile_first_last_name_display),
                    userBaseDTO.firstName == null ? "" : userBaseDTO.firstName,
                    userBaseDTO.lastName == null ? "" : userBaseDTO.lastName).trim();
        }
        return resources.getString(R.string.na);
    }
}
