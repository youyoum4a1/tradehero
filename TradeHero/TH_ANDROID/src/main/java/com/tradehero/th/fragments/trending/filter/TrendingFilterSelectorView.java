package com.tradehero.th.fragments.trending.filter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import com.tradehero.th.R;
import com.tradehero.th.fragments.market.ExchangeSpinner;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTO;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTOList;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.events.TrendingFilterEvent;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class TrendingFilterSelectorView extends RelativeLayout
{
    @Inject Analytics analytics;

    @InjectView(R.id.previous_filter) public ImageButton mPrevious;
    @InjectView(R.id.next_filter) public ImageButton mNext;
    @InjectView(R.id.title) public TextView mTitle;
    @InjectView(R.id.trending_filter_title_icon) public ImageView mTitleIcon;
    @InjectView(R.id.description) public TextView mDescription;
    @InjectView(R.id.exchange_selection) public ExchangeSpinner mExchangeSelection;
    private TrendingFilterSpinnerIconAdapterNew mExchangeSelectionAdapter;

    private ExchangeCompactSpinnerDTOList exchangeCompactSpinnerDTOs;
    @NonNull private TrendingFilterTypeDTO trendingFilterTypeDTO;
    @NonNull private BehaviorSubject<TrendingFilterTypeDTO> trendingTypeBehavior;

    //<editor-fold desc="Constructors">
    public TrendingFilterSelectorView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
        trendingFilterTypeDTO = new TrendingFilterTypeBasicDTO(getResources());
        trendingTypeBehavior = BehaviorSubject.create(trendingFilterTypeDTO);
        mExchangeSelectionAdapter = new TrendingFilterSpinnerIconAdapterNew(
                getContext(),
                R.layout.trending_filter_spinner_item);
        mExchangeSelectionAdapter.setDropDownViewResource(R.layout.trending_filter_spinner_dropdown_item);
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
        mExchangeSelection.setAdapter(mExchangeSelectionAdapter);
        if (exchangeCompactSpinnerDTOs != null)
        {
            mExchangeSelectionAdapter.addAll(exchangeCompactSpinnerDTOs);
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @NonNull public Observable<TrendingFilterTypeDTO> getObservableFilter()
    {
        return trendingTypeBehavior.asObservable();
    }

    public void setUpExchangeSpinner(@NonNull ExchangeCompactSpinnerDTOList items)
    {
        exchangeCompactSpinnerDTOs = items;
        mExchangeSelectionAdapter.addAll(items);
        mExchangeSelection.setSelection(trendingFilterTypeDTO.exchange);
    }

    public void apply(@NonNull TrendingFilterTypeDTO typeDTO)
    {
        this.trendingFilterTypeDTO = typeDTO;
        mTitle.setText(typeDTO.titleResId);
        mTitleIcon.setImageResource(typeDTO.titleIconResId);
        mDescription.setText(typeDTO.descriptionResId);
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.previous_filter)
    protected void handlePreviousClicked(View view)
    {
        apply(trendingFilterTypeDTO.getPrevious());
        notifyObserver();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.next_filter)
    protected void handleNextClicked(View view)
    {
        apply(trendingFilterTypeDTO.getNext());
        notifyObserver();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnItemSelected(value = R.id.exchange_selection, callback = OnItemSelected.Callback.ITEM_SELECTED)
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        trendingFilterTypeDTO.exchange = (ExchangeCompactSpinnerDTO) adapterView.getItemAtPosition(i);
        notifyObserver();
    }

    private void notifyObserver()
    {
        trackChangeEvent(trendingFilterTypeDTO);
        trendingTypeBehavior.onNext(trendingFilterTypeDTO);
    }

    private void trackChangeEvent(TrendingFilterTypeDTO trendingFilterTypeDTO)
    {
        analytics.fireEvent(new TrendingFilterEvent(trendingFilterTypeDTO));
    }
}
