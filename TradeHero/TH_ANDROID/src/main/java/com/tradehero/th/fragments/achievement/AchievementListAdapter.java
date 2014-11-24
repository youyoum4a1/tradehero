package com.tradehero.th.fragments.achievement;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.th.adapters.DTOAdapterNew;
import com.tradehero.th.api.achievement.AchievementCategoryDTO;

public class AchievementListAdapter extends DTOAdapterNew<AchievementCategoryDTO>
{
    public AchievementListAdapter(@NonNull Context context, int layoutResourceId)
    {
        super(context, layoutResourceId);
    }
}
