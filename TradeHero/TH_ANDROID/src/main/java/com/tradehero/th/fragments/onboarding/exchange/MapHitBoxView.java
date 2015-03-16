package com.tradehero.th.fragments.onboarding.exchange;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.tradehero.th.R;
import com.tradehero.th.api.market.MarketRegion;

public class MapHitBoxView extends ImageView
{
    @NonNull final Params sizeParams;

    //<editor-fold desc="Constructors">
    public MapHitBoxView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        sizeParams = new Params(context, attrs);
    }

    public MapHitBoxView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        sizeParams = new Params(context, attrs);
    }
    //</editor-fold>

    /**
     * Provides the opportunity to catch an OutOfMemoryError
     */
    public void loadImage()
    {
        setImageResource(sizeParams.imageRes);
    }

    static class Params
    {
        @NonNull final MarketRegion region;
        @DrawableRes final int imageRes;

        Params(@NonNull Context context, @NonNull AttributeSet attrs)
        {
            this(context.obtainStyledAttributes(attrs, R.styleable.MapHitBoxView));
        }

        Params(@NonNull TypedArray a)
        {
            this(MarketRegion.valueOf(a.getString(R.styleable.MapHitBoxView_hitBoxRegion)),
                    a.getResourceId(R.styleable.MapHitBoxView_hitBoxImage, R.drawable.map_hitboxes)
            );
            a.recycle();
        }

        Params(@NonNull MarketRegion region, @DrawableRes int imageRes)
        {
            this.region = region;
            this.imageRes = imageRes;
        }
    }
}
