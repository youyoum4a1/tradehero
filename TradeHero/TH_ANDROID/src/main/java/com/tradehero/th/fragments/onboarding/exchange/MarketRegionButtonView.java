package com.ayondo.academy.fragments.onboarding.exchange;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import com.ayondo.academy.api.market.MarketRegion;
import com.ayondo.academy.rx.view.ViewArrayObservable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import rx.Observable;
import rx.android.view.OnClickEvent;
import rx.functions.Func1;

public class MarketRegionButtonView extends TableLayout
{
    //<editor-fold desc="Constructors">
    public MarketRegionButtonView(Context context)
    {
        super(context);
    }

    public MarketRegionButtonView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @NonNull public Observable<MarketRegion> getMarketRegionClickedObservable()
    {
        final List<MarketRegionView> regionViews = getRegionViews();
        return ViewArrayObservable.clicks(new ArrayList<View>(regionViews), false)
                .map(new Func1<OnClickEvent, MarketRegion>()
                {
                    @Override public MarketRegion call(OnClickEvent onClickEvent)
                    {
                        MarketRegionView regionView = (MarketRegionView) onClickEvent.view();
                        for (MarketRegionView candidate : regionViews)
                        {
                            if (candidate == regionView)
                            {
                                candidate.setSelected(true);
                            }
                            else
                            {
                                candidate.setSelected(false);
                            }
                        }
                        return regionView.params.region;
                    }
                });
    }

    @NonNull protected List<MarketRegionView> getRegionViews()
    {
        List<MarketRegionView> regionViews = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < getChildCount(); rowIndex++)
        {
            TableRow row = (TableRow) getChildAt(rowIndex);
            for (int regionIndex = 0; regionIndex < row.getChildCount(); regionIndex++)
            {
                regionViews.add((MarketRegionView) row.getChildAt(regionIndex));
            }
        }
        return regionViews;
    }

    public void showClicked(@NonNull MarketRegion marketRegion)
    {
        for (MarketRegionView view : getRegionViews())
        {
            if (view.params.region.equals(marketRegion))
            {
                view.setSelected(true);
            }
        }
    }

    public void enable(@NonNull Collection<? extends MarketRegion> enabledRegions)
    {
        for (MarketRegionView view : getRegionViews())
        {
            view.setEnabled(enabledRegions.contains(view.params.region));
        }
    }
}
