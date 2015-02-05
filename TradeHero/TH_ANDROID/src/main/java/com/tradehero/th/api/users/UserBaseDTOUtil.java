package com.tradehero.th.api.users;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.R;

public class UserBaseDTOUtil
{
    @NonNull public static String getLongDisplayName(@NonNull Context context, @Nullable UserBaseDTO userBaseDTO)
    {
        if (userBaseDTO != null)
        {
            if (userBaseDTO.firstName != null &&
                !userBaseDTO.firstName.isEmpty() &&
                userBaseDTO.lastName != null &&
                !userBaseDTO.lastName.isEmpty())
            {
                return getFirstLastName(context, userBaseDTO);
            }

            return userBaseDTO.displayName;
        }

        return context.getString(R.string.na);
    }

    //return displayName in Follow and Hero ListItemView as https://www.pivotaltracker.com/story/show/76497256
    @NonNull public static String getShortDisplayName(@NonNull Context context, @Nullable UserBaseDTO userBaseDTO)
    {
        if (userBaseDTO != null)
        {
            return userBaseDTO.displayName;
        }

        return context.getString(R.string.na);
    }

    @NonNull public static String getFirstLastName(@NonNull Context context, @Nullable UserBaseDTO userBaseDTO)
    {
        if (userBaseDTO != null)
        {
            return String.format(context.getString(R.string.user_profile_first_last_name_display),
                    userBaseDTO.firstName == null ? "" : userBaseDTO.firstName,
                    userBaseDTO.lastName == null ? "" : userBaseDTO.lastName).trim();
        }
        return context.getString(R.string.na);
    }
}
