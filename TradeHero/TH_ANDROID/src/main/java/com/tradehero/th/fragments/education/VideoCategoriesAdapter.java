package com.tradehero.th.fragments.education;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.th.adapters.PagedArrayDTOAdapterNew;
import com.tradehero.th.api.education.VideoCategoryDTO;

public class VideoCategoriesAdapter extends PagedArrayDTOAdapterNew<VideoCategoryDTO, VideoCategoryView>
{
    public VideoCategoriesAdapter(@NonNull Context context, int layoutResourceId)
    {
        super(context, layoutResourceId);
    }
}
