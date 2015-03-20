package com.tradehero.th.fragments.competition;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.competition.HelpVideoDTO;
import com.tradehero.th.inject.HierarchyInjector;
import java.net.MalformedURLException;
import java.net.URL;
import javax.inject.Inject;

public class ProviderVideoListItemView extends RelativeLayout
        implements DTOView<HelpVideoDTO>
{
    @Inject Picasso picasso;

    @InjectView(R.id.help_video_thumbnail) protected ImageView thumbnail;
    @InjectView(R.id.help_video_title) protected TextView title;
    @InjectView(R.id.help_video_description) protected TextView description;
    @InjectView(R.id.help_video_url) protected TextView url;

    //<editor-fold desc="Constructors">
    public ProviderVideoListItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
        thumbnail.setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override protected void onDetachedFromWindow()
    {
        picasso.cancelRequest(thumbnail);
        super.onDetachedFromWindow();
    }

    @Override public void display(@NonNull HelpVideoDTO videoDTO)
    {
        if (this.thumbnail != null)
        {
            if (videoDTO.thumbnailUrl != null)
            {
                this.picasso.load(videoDTO.thumbnailUrl).into(this.thumbnail);
            }
            else
            {
                thumbnail.setImageDrawable(null);
            }
        }
        if (this.title != null)
        {
            this.title.setText(videoDTO.title);
        }
        if (this.description != null)
        {
            this.description.setText(videoDTO.subtitle);
        }
        if (this.url != null)
        {
            try
            {
                this.url.setText(new URL(videoDTO.videoUrl).getHost());
            } catch (MalformedURLException ignored)
            {
                this.url.setText("");
            }
        }
    }
}
