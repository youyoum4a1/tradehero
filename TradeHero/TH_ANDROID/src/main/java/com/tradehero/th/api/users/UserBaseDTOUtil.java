package com.tradehero.th.api.users;

import android.content.Context;
import com.tradehero.th.R;

/** Created with IntelliJ IDEA. User: xavier Date: 12/2/13 Time: 4:36 PM To change this template use File | Settings | File Templates. */
public class UserBaseDTOUtil
{
    public static final String TAG = UserBaseDTOUtil.class.getSimpleName();

    public static String getLongDisplayName(Context context, UserBaseDTO userBaseDTO)
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

    public static String getFirstLastName(Context context, UserBaseDTO userBaseDTO)
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
