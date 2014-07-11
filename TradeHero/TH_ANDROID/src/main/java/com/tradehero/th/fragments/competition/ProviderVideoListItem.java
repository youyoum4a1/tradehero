package com.tradehero.th.fragments.competition;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import com.tradehero.thm.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.competition.HelpVideoDTO;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

public class ProviderVideoListItem extends RelativeLayout implements DTOView<HelpVideoDTO>
{
    private HelpVideoDTO videoDTO;
    @Inject Picasso picasso;

    @InjectView(R.id.help_video_thumbnail) protected ImageView thumbnail;
    @InjectView(R.id.help_video_title) protected TextView title;
    @InjectView(R.id.help_video_description) protected TextView description;

    //<editor-fold desc="Constructors">
    public ProviderVideoListItem(Context context)
    {
        super(context);
    }

    public ProviderVideoListItem(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ProviderVideoListItem(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        thumbnail.setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override protected void onDetachedFromWindow()
    {
        thumbnail.setImageDrawable(null);
        super.onDetachedFromWindow();
    }

    @Override public void display(HelpVideoDTO helpVideoDTO)
    {
        this.linkWith(helpVideoDTO, true);
    }

    public void linkWith(HelpVideoDTO videoDto, boolean andDisplay)
    {
        this.videoDTO = videoDto;
        if (andDisplay)
        {
            this.displayThumbnail();
            displayTitle();
            displayDescription();
        }
    }

    public void displayThumbnail()
    {
        if (this.thumbnail != null)
        {
            if (this.videoDTO != null && this.videoDTO.thumbnailUrl != null)
            {
                this.picasso.load(this.videoDTO.thumbnailUrl).into(this.thumbnail);
            }
        }
    }

    public void displayTitle()
    {
        if (this.title != null)
        {
            if (this.videoDTO != null)
            {
                this.title.setText(this.videoDTO.title);
            }
        }
    }

    public void displayDescription()
    {
        if (this.description != null)
        {
            if (this.videoDTO != null)
            {
                this.description.setText(this.videoDTO.subtitle);
            }
        }
    }
}
