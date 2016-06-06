package com.androidth.general.fragments.education;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import com.androidth.general.adapters.SingleViewDTOSetAdapter;
import com.androidth.general.api.education.VideoDTO;
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
