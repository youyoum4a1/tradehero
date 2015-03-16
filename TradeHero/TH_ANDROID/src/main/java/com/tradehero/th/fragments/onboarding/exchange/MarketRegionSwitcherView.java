package com.tradehero.th.fragments.onboarding.exchange;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ViewSwitcher;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.market.MarketRegion;
import java.util.Collection;
import rx.Observable;
import rx.internal.util.SubscriptionList;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public class MarketRegionSwitcherView extends ViewSwitcher
{
    private static final int INDEX_CHILD_MAP = 0;
    private static final int INDEX_CHILD_BUTTON = 1;

    @InjectView(R.id.market_map_selector) MarketRegionMapView mapView;
    @InjectView(R.id.market_button_selector) MarketRegionButtonView buttonView;

    @NonNull private BehaviorSubject<MarketRegion> clickedMarketRegionBehavior;
    @NonNull private SubscriptionList childSubscriptions;

    //<editor-fold desc="Constructors">
    public MarketRegionSwitcherView(Context context)
    {
        super(context);
        this.clickedMarketRegionBehavior = BehaviorSubject.create();
    }

    public MarketRegionSwitcherView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.clickedMarketRegionBehavior = BehaviorSubject.create();
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
        try
        {
            mapView.loadBackMap();
            setDisplayedChild(INDEX_CHILD_MAP);
        } catch (OutOfMemoryError e)
        {
            Timber.e(e, "Failed to load map");
            setDisplayedChild(INDEX_CHILD_BUTTON);
        }
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        childSubscriptions = new SubscriptionList();
        childSubscriptions.add(mapView.getMarketRegionClickedObservable().subscribe(clickedMarketRegionBehavior));
        childSubscriptions.add(buttonView.getMarketRegionClickedObservable().subscribe(clickedMarketRegionBehavior));
    }

    @Override protected void onDetachedFromWindow()
    {
        childSubscriptions.unsubscribe();
        super.onDetachedFromWindow();
    }

    public void enable(@NonNull Collection<? extends MarketRegion> enabledRegions)
    {
        try
        {
            mapView.enable(enabledRegions);
        } catch (OutOfMemoryError e)
        {
            Timber.e(e, "Failed to load hitbox");
            setDisplayedChild(INDEX_CHILD_BUTTON);
        }
        buttonView.enable(enabledRegions);
    }

    @NonNull public Observable<MarketRegion> getMarketRegionClickedObservable()
    {
        return clickedMarketRegionBehavior.asObservable();
    }
}
