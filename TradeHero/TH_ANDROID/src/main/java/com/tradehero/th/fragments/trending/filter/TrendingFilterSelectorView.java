package com.tradehero.th.fragments.trending.filter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.fragments.market.ExchangeSpinner;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTO;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.ProfileEvent;
import com.tradehero.th.utils.metrics.events.TrendingFilterEvent;
import java.util.Collections;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class TrendingFilterSelectorView extends RelativeLayout
{
    @Inject Analytics analytics;

    @InjectView(R.id.title) public TextView mTitle;
    @InjectView(R.id.trending_filter_title_icon) public ImageView mTitleIcon;
    @InjectView(R.id.description) public TextView mDescription;
    @InjectView(R.id.exchange_selection) public ExchangeSpinner mExchangeSelection;

    @Nullable private SpinnerAdapter exchangeAdapter;
    @NonNull private TrendingFilterTypeDTO trendingFilterTypeDTO;
    @NonNull private BehaviorSubject<TrendingFilterTypeDTO> trendingTypeBehavior;

    //<editor-fold desc="Constructors">
    public TrendingFilterSelectorView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
        trendingFilterTypeDTO = new TrendingFilterTypeBasicDTO(getResources());
        trendingTypeBehavior = BehaviorSubject.create(trendingFilterTypeDTO);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        mExchangeSelection.setAdapter(exchangeAdapter);
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    public void setExchangeAdapter(@Nullable SpinnerAdapter exchangeAdapter)
    {
        this.exchangeAdapter = exchangeAdapter;
        mExchangeSelection.setAdapter(exchangeAdapter);
        mExchangeSelection.setSelection(trendingFilterTypeDTO.exchange);
    }

    @NonNull public Observable<TrendingFilterTypeDTO> getObservableFilter()
    {
        return trendingTypeBehavior.asObservable();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.previous_filter)
    protected void handlePreviousClicked(View view)
    {
        apply(trendingFilterTypeDTO.getPrevious());
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.next_filter)
    protected void handleNextClicked(View view)
    {
        apply(trendingFilterTypeDTO.getNext());
    }

    public void apply(@NonNull TrendingFilterTypeDTO typeDTO)
    {
        boolean hasChanged = !typeDTO.equals(this.trendingFilterTypeDTO);
        this.trendingFilterTypeDTO = typeDTO;
        mTitle.setText(typeDTO.titleResId);
        mTitleIcon.setImageResource(typeDTO.titleIconResId);
        mDescription.setText(typeDTO.descriptionResId);
        mExchangeSelection.setSelection(trendingFilterTypeDTO.exchange);
        if (hasChanged)
        {
            notifyObserver();
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnItemSelected(value = R.id.exchange_selection, callback = OnItemSelected.Callback.ITEM_SELECTED)
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        trendingFilterTypeDTO = trendingFilterTypeDTO.getByExchange((ExchangeCompactSpinnerDTO) adapterView.getItemAtPosition(i));
        notifyObserver();
        reportAnalytics();
    }

    private void notifyObserver()
    {
        trendingTypeBehavior.onNext(trendingFilterTypeDTO);
    }

    private void trackChangeEvent(TrendingFilterTypeDTO trendingFilterTypeDTO)
    {
        analytics.fireEvent(new TrendingFilterEvent(trendingFilterTypeDTO));
    }

    private void reportAnalytics()
    {
        trackChangeEvent(trendingFilterTypeDTO);
        if (Constants.RELEASE)
        {
            analytics.localytics().setProfileAttribute(new ProfileEvent(
                    AnalyticsConstants.InterestedExchange,
                    Collections.singletonList(trendingFilterTypeDTO.exchange.name)));
        }
    }
}
