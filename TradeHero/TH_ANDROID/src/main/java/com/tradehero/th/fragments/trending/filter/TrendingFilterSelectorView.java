package com.tradehero.th.fragments.trending.filter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.thm.R;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTO;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTOList;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import com.tradehero.th.utils.metrics.localytics.THLocalyticsSession;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;

public class TrendingFilterSelectorView extends RelativeLayout
{
    @InjectView(R.id.previous_filter) public ImageButton mPrevious;
    @InjectView(R.id.next_filter) public ImageButton mNext;
    @InjectView(R.id.title) public TextView mTitle;
    @InjectView(R.id.trending_filter_title_icon) public ImageView mTitleIcon;
    @InjectView(R.id.description) public TextView mDescription;
    @InjectView(R.id.exchange_selection) public Spinner mExchangeSelection;
    private TrendingFilterSpinnerIconAdapterNew mExchangeSelectionAdapter;

    @NotNull private TrendingFilterTypeDTO trendingFilterTypeDTO;
    private OnFilterTypeChangedListener changedListener;
    @Inject THLocalyticsSession localyticsSession;

    //<editor-fold desc="Constructors">
    public TrendingFilterSelectorView(Context context)
    {
        super(context);
        init();
    }

    public TrendingFilterSelectorView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public TrendingFilterSelectorView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }
    //</editor-fold>

    protected void init()
    {
        DaggerUtils.inject(this);
        trendingFilterTypeDTO = new TrendingFilterTypeBasicDTO(getResources());
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    public void onDestroy()
    {
        if (mExchangeSelection != null)
        {
            mExchangeSelection.setOnItemSelectedListener(null);
            mExchangeSelection.setAdapter(null);
        }
        mExchangeSelection = null;
    }

    public void setUpExchangeSpinner(@NotNull ExchangeCompactSpinnerDTOList items)
    {
        Spinner exchangeSelection = mExchangeSelection;
        if (exchangeSelection != null)
        {
            mExchangeSelectionAdapter = new TrendingFilterSpinnerIconAdapterNew(
                    getContext(),
                    R.layout.trending_filter_spinner_item);
            mExchangeSelectionAdapter.setDropDownViewResource(R.layout.trending_filter_spinner_dropdown_item);
            mExchangeSelectionAdapter.addAll(items);
            exchangeSelection.setAdapter(mExchangeSelectionAdapter);
            exchangeSelection.setSelection(items.indexOf(
                    new ExchangeCompactSpinnerDTO(
                            getResources(),
                            trendingFilterTypeDTO.exchange)));
            exchangeSelection.setOnItemSelectedListener(createTrendingFilterSelectorViewSpinnerListener());
        }
    }

    public void apply(@NotNull TrendingFilterTypeDTO typeDTO)
    {
        this.trendingFilterTypeDTO = typeDTO;
        if (mTitle != null)
        {
            mTitle.setText(typeDTO.titleResId);
        }

        if (mTitleIcon != null)
        {
            mTitleIcon.setImageResource(typeDTO.titleIconResId);
        }

        if (mDescription != null)
        {
            mDescription.setText(typeDTO.descriptionResId);
        }
    }

    @OnClick(R.id.previous_filter)
    protected void handlePreviousClicked(View view)
    {
        apply(trendingFilterTypeDTO.getPrevious());
        notifyListenerChanged();
    }

    @OnClick(R.id.next_filter)
    protected void handleNextClicked(View view)
    {
        apply(trendingFilterTypeDTO.getNext());
        notifyListenerChanged();
    }

    public void setChangedListener(OnFilterTypeChangedListener listener)
    {
        this.changedListener = listener;
    }

    private void notifyListenerChanged()
    {
        trackChangeEvent(trendingFilterTypeDTO);
        if (changedListener != null)
        {
            changedListener.onFilterTypeChanged(trendingFilterTypeDTO);
        }
    }

    private void trackChangeEvent(TrendingFilterTypeDTO trendingFilterTypeDTO)
    {
        localyticsSession.tagEvent(LocalyticsConstants.TabBar_Trending, trendingFilterTypeDTO);
    }

    protected AdapterView.OnItemSelectedListener createTrendingFilterSelectorViewSpinnerListener()
    {
        return new TrendingFilterSelectorViewSpinnerListener();
    }

    protected class TrendingFilterSelectorViewSpinnerListener implements AdapterView.OnItemSelectedListener
    {
        @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
        {
            trendingFilterTypeDTO.exchange = (ExchangeCompactSpinnerDTO) adapterView.getItemAtPosition(i);
            notifyListenerChanged();
        }

        @Override public void onNothingSelected(AdapterView<?> adapterView)
        {
            // Nothing to do
        }
    }

    public static interface OnFilterTypeChangedListener
    {
        void onFilterTypeChanged(TrendingFilterTypeDTO trendingFilterTypeDTO);
    }
}
