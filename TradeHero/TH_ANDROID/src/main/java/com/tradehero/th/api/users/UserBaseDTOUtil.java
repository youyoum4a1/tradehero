package com.tradehero.th.api.users;

import android.content.Context;
import com.tradehero.th.R;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UserBaseDTOUtil
{
    //<editor-fold desc="Constructors">
    @Inject public UserBaseDTOUtil()
    {
        super();
    }
    //</editor-fold>

    @NotNull public String getLongDisplayName(@NotNull Context context, @Nullable UserBaseDTO userBaseDTO)
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
    @NotNull public String getShortDisplayName(@NotNull Context context, @Nullable UserBaseDTO userBaseDTO)
    {
        if (userBaseDTO != null)
        {
            return userBaseDTO.displayName;
        }

        return context.getString(R.string.na);
    }

    @NotNull public String getFirstLastName(@NotNull Context context, @Nullable UserBaseDTO userBaseDTO)
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
