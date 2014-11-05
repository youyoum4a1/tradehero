package com.tradehero.th.fragments.achievement;

import android.content.Context;
import com.tradehero.th.adapters.DTOAdapterNew;
import com.tradehero.th.api.achievement.AchievementCategoryDTO;
import android.support.annotation.NonNull;

public class AchievementListAdapter extends DTOAdapterNew<AchievementCategoryDTO>
{
    public AchievementListAdapter(@NonNull Context context, int layoutResourceId)
    {
        super(context, layoutResourceId);
    }
}
