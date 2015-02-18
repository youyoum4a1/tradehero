package com.tradehero.th.fragments.onboarding.exchange;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import com.tradehero.th.api.market.MarketRegion;
import com.tradehero.th.rx.view.ViewArrayObservable;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.android.view.OnClickEvent;
import rx.functions.Func1;

public class MarketRegionMapView extends TableLayout
{
    //<editor-fold desc="Constructors">
    public MarketRegionMapView(Context context)
    {
        super(context);
    }

    public MarketRegionMapView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @NonNull public Observable<MarketRegion> getMarketRegionClickedObservable()
    {
        List<View> regionViews = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < getChildCount(); rowIndex++)
        {
            TableRow row = (TableRow) getChildAt(rowIndex);
            for (int regionIndex = 0; regionIndex < row.getChildCount(); regionIndex++)
            {
                regionViews.add(row.getChildAt(regionIndex));
            }
        }
        return ViewArrayObservable.clicks(regionViews, false)
                .map(new Func1<OnClickEvent, MarketRegion>()
                {
                    @Override public MarketRegion call(OnClickEvent onClickEvent)
                    {
                        return ((MarketRegionView) onClickEvent.view()).region;
                    }
                });
    }
}
