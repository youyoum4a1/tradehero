package com.tradehero.th.fragments.settings.photo;

import android.content.res.Resources;
import com.tradehero.thm.R;

public class ChooseImageFromDTO
{
    public final int titleResId;

    public ChooseImageFromDTO(int titleResId)
    {
        this.titleResId = titleResId;
    }

    public String getTitle(Resources resources)
    {
        if (titleResId == 0)
        {
            return resources.getString(R.string.na);
        }
        return resources.getString(titleResId);
    }
}
