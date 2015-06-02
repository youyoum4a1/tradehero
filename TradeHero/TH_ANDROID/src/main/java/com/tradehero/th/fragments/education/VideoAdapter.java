package com.tradehero.th.fragments.education;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import com.tradehero.th.adapters.SingleViewDTOSetAdapter;
import com.tradehero.th.api.education.VideoDTO;
import java.util.Comparator;

public class VideoAdapter extends SingleViewDTOSetAdapter<VideoDTO, VideoView>
{
    //<editor-fold desc="Constructors">
    public VideoAdapter(
            @NonNull Context context,
            @LayoutRes int layoutResourceId)
    {
        super(context,
                new Comparator<VideoDTO>()
                {
                    @Override public int compare(VideoDTO lhs, VideoDTO rhs)
                    {
                        return lhs.getVideoId().compareTo(rhs.getVideoId());
                    }
                },
                layoutResourceId);
    }
    //</editor-fold>
}
