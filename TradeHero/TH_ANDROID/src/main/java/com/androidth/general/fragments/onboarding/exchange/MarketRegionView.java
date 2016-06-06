package com.androidth.general.fragments.onboarding.exchange;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.TextView;
import com.androidth.general.R;
import com.androidth.general.api.market.MarketRegion;
import com.androidth.general.models.market.MarketRegionDisplayUtil;

public class MarketRegionView extends TextView
{
    @NonNull final Params params;

    //<editor-fold desc="Constructors">
    public MarketRegionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        params = new Params(context, attrs);
        setSelected(false);
    }

    public MarketRegionView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        this.params = new Params(context, attrs);
        setSelected(false);
    }
    //</editor-fold>

    @NonNull static MarketRegion getMarketRegion(@NonNull Context context, @NonNull AttributeSet attrs)
    {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MarketRegionView);
        MarketRegion region = MarketRegion.valueOf(a.getString(R.styleable.MarketRegionView_region));
        a.recycle();
        return region;
    }

    static float getAlpha(@NonNull Context context, @NonNull AttributeSet attrs)
    {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.OnBoardSelectableViewLinear);
        float region = a.getFloat(R.styleable.OnBoardSelectableViewLinear_alphaUnSelected, 1f);
        a.recycle();
        return region;
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        setText(MarketRegionDisplayUtil.getLabelRes(params.region));
        //setBackground(getResources().getColor(MarketRegionDisplayUtil.getColorRes(params.region)));
        setBackgroundResource(MarketRegionDisplayUtil.getBgDrawableRes(params.region));
    }

    @Override public void setSelected(boolean selected)
    {
        setAlpha(selected ? 1f : params.alphaUnSelected);
    }

    static class Params
    {
        @NonNull final MarketRegion region;
        final float alphaUnSelected;

        //<editor-fold desc="Constructors">
        Params(@NonNull Context context, @NonNull AttributeSet attrs)
        {
            this(context.obtainStyledAttributes(attrs, R.styleable.MarketRegionView),
                    context.obtainStyledAttributes(attrs, R.styleable.OnBoardSelectableViewLinear));
        }

        Params(@NonNull TypedArray marketRegionAttrs, @NonNull TypedArray selectableAttrs)
        {
            this(MarketRegion.valueOf(marketRegionAttrs.getString(R.styleable.MarketRegionView_region)),
                    selectableAttrs.getFloat(R.styleable.OnBoardSelectableViewLinear_alphaUnSelected, 1f));
            marketRegionAttrs.recycle();
            selectableAttrs.recycle();
        }

        Params(@NonNull MarketRegion region, float alphaUnSelected)
        {
            this.region = region;
            this.alphaUnSelected = alphaUnSelected;
        }
        //</editor-fold>
    }
}
