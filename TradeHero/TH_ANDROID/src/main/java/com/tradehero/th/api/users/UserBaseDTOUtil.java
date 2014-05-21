package com.tradehero.th.api.users;

import android.content.Context;
import com.tradehero.th.R;
import javax.inject.Inject;

public class UserBaseDTOUtil
{
    @Inject public UserBaseDTOUtil()
    {
        super();
    }

    public String getLongDisplayName(Context context, UserBaseDTO userBaseDTO)
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

    public String getFirstLastName(Context context, UserBaseDTO userBaseDTO)
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
