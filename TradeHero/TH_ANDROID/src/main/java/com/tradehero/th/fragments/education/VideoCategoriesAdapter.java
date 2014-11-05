package com.tradehero.th.fragments.education;

import android.content.Context;
import com.tradehero.th.adapters.PagedArrayDTOAdapterNew;
import com.tradehero.th.api.education.VideoCategoryDTO;
import android.support.annotation.NonNull;

public class VideoCategoriesAdapter extends PagedArrayDTOAdapterNew<VideoCategoryDTO, VideoCategoryView>
{
    public VideoCategoriesAdapter(@NonNull Context context, int layoutResourceId)
    {
        super(context, layoutResourceId);
    }
}
