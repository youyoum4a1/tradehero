package com.tradehero.th.fragments.competition;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.competition.HelpVideoDTO;
import com.tradehero.th.api.competition.key.HelpVideoId;
import com.tradehero.th.persistence.competition.HelpVideoCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

/**
 * Created by xavier on 1/16/14.
 */
public class ProviderVideoListItem extends RelativeLayout implements DTOView<HelpVideoId>
{
    public static final String TAG = ProviderVideoListItem.class.getSimpleName();

    private HelpVideoId videoId;
    private HelpVideoDTO videoDTO;
    @Inject protected HelpVideoCache helpVideoCache;
    @Inject Picasso picasso;

    private ImageView thumbnail;
    private TextView title;
    private TextView description;

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

        this.thumbnail = (ImageView) findViewById(R.id.help_video_thumbnail);
        this.title = (TextView) findViewById(R.id.help_video_title);
        this.description = (TextView) findViewById(R.id.help_video_description);
    }

    @Override public void display(HelpVideoId videoId1)
    {
        if (videoId1 == null)
        {
            this.linkWith(null, true);
        }
        else
        {
            this.linkWith(helpVideoCache.get(videoId1), true);
        }
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
