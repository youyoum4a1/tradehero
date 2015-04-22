package com.tradehero.th.fragments.onboarding.exchange;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.TextView;
import com.tradehero.th.R;
import com.tradehero.th.api.market.MarketRegion;
import com.tradehero.th.models.market.MarketRegionDisplayUtil;

public class MarketRegionView extends TextView
{
    @NonNull public final MarketRegion region;

    //<editor-fold desc="Constructors">
    public MarketRegionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.region = getMarketRegion(context, attrs);
    }

    public MarketRegionView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        this.region = getMarketRegion(context, attrs);
    }
    //</editor-fold>

    @NonNull static MarketRegion getMarketRegion(@NonNull Context context, @NonNull AttributeSet attrs)
    {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MarketRegionView);
        MarketRegion region = MarketRegion.valueOf(a.getString(R.styleable.MarketRegionView_region));
        a.recycle();
        return region;
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        setText(MarketRegionDisplayUtil.getLabelRes(region));
        //setBackground(getResources().getColor(MarketRegionDisplayUtil.getColorRes(region)));
        setBackgroundResource(MarketRegionDisplayUtil.getBgDrawableRes(region));
    }
}
