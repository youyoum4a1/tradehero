package com.tradehero.th.fragments.achievement;

import android.content.Context;
import com.tradehero.th.adapters.DTOAdapterNew;
import com.tradehero.th.api.achievement.AchievementCategoryDTO;
import org.jetbrains.annotations.NotNull;

public class AchievementListAdapter extends DTOAdapterNew<AchievementCategoryDTO>
{
    public AchievementListAdapter(@NotNull Context context, int layoutResourceId)
    {
        super(context, layoutResourceId);
    }
}
