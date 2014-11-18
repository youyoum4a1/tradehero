package com.tradehero.th.fragments.discovery;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.games.GamesDTO;
import com.tradehero.th.inject.HierarchyInjector;
import javax.inject.Inject;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class GamesItemView extends LinearLayout implements DTOView<GamesDTO>
{
    @InjectView(R.id.title) TextView titleView;
    @InjectView(R.id.image) ImageView imageView;
    @Inject Picasso picasso;

    //<editor-fold desc="Constructors">
    public GamesItemView(Context context)
    {
        super(context);
    }

    public GamesItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public GamesItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        HierarchyInjector.inject(this);
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
    }

    @Override
    public void display(GamesDTO dto) {
        if (dto.title != null && !dto.title.isEmpty())
        {
            titleView.setText(dto.title);
            titleView.setVisibility(VISIBLE);
            imageView.setVisibility(GONE);
        }
        else
        {
            titleView.setVisibility(GONE);
            imageView.setVisibility(VISIBLE);
        }
        if (dto.image != null && !dto.image.isEmpty())
        {
            picasso.load(dto.image)
                    .placeholder(R.drawable.lb_competitions_bg)
                    .into(imageView);
        }
    }

}
