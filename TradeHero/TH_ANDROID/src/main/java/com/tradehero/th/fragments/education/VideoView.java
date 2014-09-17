package com.tradehero.th.fragments.education;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.education.VideoDTO;
import com.tradehero.th.inject.HierarchyInjector;
import javax.inject.Inject;

public class VideoView extends RelativeLayout implements DTOView<VideoDTO>
{
    @InjectView(R.id.video_thumbnail) ImageView thumbnail;
    @InjectView(R.id.video_title) TextView title;
    @InjectView(R.id.video_padlock) ImageView padlock;

    @Inject Picasso picasso;

    public VideoView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override public void display(VideoDTO dto)
    {
        title.setText(dto.name);
        picasso.load(dto.thumbnail).into(thumbnail);
        if (dto.locked)
        {
            padlock.setVisibility(View.VISIBLE);
        }
        else
        {
            padlock.setVisibility(View.GONE);
        }
    }
}
