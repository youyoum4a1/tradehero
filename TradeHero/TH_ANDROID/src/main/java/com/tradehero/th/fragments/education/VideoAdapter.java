package com.tradehero.th.fragments.education;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.adapters.ViewDTOSetAdapter;
import com.tradehero.th.api.education.VideoDTO;
import java.util.Comparator;

public class VideoAdapter extends ViewDTOSetAdapter<VideoDTO, VideoView>
{
    @LayoutRes private int layoutResourceId;

    //<editor-fold desc="Constructors">
    public VideoAdapter(
            @NonNull Context context,
            @Nullable Comparator<VideoDTO> comparator,
            @LayoutRes int layoutResourceId)
    {
        super(context, comparator);
        this.layoutResourceId = layoutResourceId;
    }
    //</editor-fold>

    @Override @LayoutRes protected int getViewResId(int position)
    {
        return layoutResourceId;
    }
}
