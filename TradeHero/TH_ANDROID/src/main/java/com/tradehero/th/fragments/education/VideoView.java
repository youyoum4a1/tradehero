package com.tradehero.th.fragments.education;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.education.VideoDTO;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

public class VideoView extends RelativeLayout implements DTOView<VideoDTO>
{
    @InjectView(R.id.video_thumbnail) ImageView thumbnail;
    @InjectView(R.id.video_title) TextView title;

    @Inject Picasso picasso;

    public VideoView(Context context)
    {
        super(context);
    }

    public VideoView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public VideoView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        ButterKnife.inject(this);
    }

    @Override public void display(VideoDTO dto)
    {
        title.setText(dto.name);
        picasso.load(dto.thumbnail).into(thumbnail);
    }
}
