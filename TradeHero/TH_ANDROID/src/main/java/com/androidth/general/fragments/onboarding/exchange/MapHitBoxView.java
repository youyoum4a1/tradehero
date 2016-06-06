package com.androidth.general.fragments.onboarding.exchange;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.androidth.general.R;
import com.androidth.general.api.market.MarketRegion;

public class MapHitBoxView extends ImageView
{
    @NonNull final Params params;

    //<editor-fold desc="Constructors">
    public MapHitBoxView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        params = new Params(context, attrs);
        setSelected(false);
    }

    public MapHitBoxView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        params = new Params(context, attrs);
        setSelected(false);
    }
    //</editor-fold>

    /**
     * Provides the opportunity to catch an OutOfMemoryError
     */
    public void loadImage()
    {
        setImageResource(params.imageRes);
    }

    @Override public void setSelected(boolean selected)
    {
        setAlpha(selected ? params.alphaSelected : 0f);
    }

    static class Params
    {
        @NonNull final MarketRegion region;
        @DrawableRes final int imageRes;
        final float alphaSelected;

        //<editor-fold desc="Constructors">
        Params(@NonNull Context context, @NonNull AttributeSet attrs)
        {
            this(context.obtainStyledAttributes(attrs, R.styleable.MarketRegionView),
                    context.obtainStyledAttributes(attrs, R.styleable.OnBoardSelectableViewLinear));
        }

        Params(@NonNull TypedArray marketRegionAttrs, @NonNull TypedArray selectableAttrs)
        {
            this(MarketRegion.valueOf(marketRegionAttrs.getString(R.styleable.MarketRegionView_region)),
                    marketRegionAttrs.getResourceId(R.styleable.MarketRegionView_hitBoxImage, R.drawable.map_hitboxes),
                    selectableAttrs.getFloat(R.styleable.OnBoardSelectableViewLinear_alphaSelected, 0f));
            marketRegionAttrs.recycle();
            selectableAttrs.recycle();
        }

        Params(@NonNull MarketRegion region, @DrawableRes int imageRes, float alphaSelected)
        {
            this.region = region;
            this.imageRes = imageRes;
            this.alphaSelected = alphaSelected;
        }
        //</editor-fold>
    }
}
