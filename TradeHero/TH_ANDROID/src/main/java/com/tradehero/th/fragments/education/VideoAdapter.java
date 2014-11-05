package com.tradehero.th.fragments.education;

import android.content.Context;
import com.tradehero.th.adapters.DTOAdapterNew;
import com.tradehero.th.api.education.VideoDTO;
import android.support.annotation.NonNull;

public class VideoAdapter extends DTOAdapterNew<VideoDTO>
{
    public VideoAdapter(@NonNull Context context, int layoutResourceId)
    {
        super(context, layoutResourceId);
    }
}
