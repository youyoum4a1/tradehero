package com.tradehero.th.fragments.education;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.education.VideoDTO;
import com.tradehero.th.inject.HierarchyInjector;
import javax.inject.Inject;

public class VideoView extends RelativeLayout implements DTOView<VideoDTO>
{
    @Inject Picasso picasso;
    @Bind(R.id.video_thumbnail) ImageView thumbnail;
    @Bind(R.id.video_title) TextView title;
    @Bind(R.id.video_padlock) View padlock;

    public VideoView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    @Override public void display(@NonNull VideoDTO dto)
    {
        title.setText(dto.name);
        picasso.load(dto.thumbnail)
                .into(thumbnail);
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
