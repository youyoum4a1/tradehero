package com.tradehero.th.fragments.trending;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.tradehero.th.R;
import com.tradehero.th.models.graphics.ForExtraTileBackground;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: tho Date: 2/7/14 Time: 5:36 PM Copyright (c) TradeHero
 */
public class ResetPortfolioTileView extends ImageView
{
    @Inject protected Lazy<Picasso> picasso;
    @Inject @ForExtraTileBackground Transformation backgroundTransformation;

    //<editor-fold desc="Constructors">
    public ResetPortfolioTileView(Context context)
    {
        super(context);
    }

    public ResetPortfolioTileView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ResetPortfolioTileView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        DaggerUtils.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        // no nid to clean on detach, coz this view's content will never change
        picasso.get()
                .load(R.drawable.tile_trending_reset_portfolio)
                .placeholder(R.drawable.white_rounded_background_xml)
                .transform(backgroundTransformation)
                .fit()
                .into(this);
    }
}
