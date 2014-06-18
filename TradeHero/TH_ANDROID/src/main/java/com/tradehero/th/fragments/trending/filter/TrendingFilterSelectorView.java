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
import com.tradehero.common.adapter.SpinnerIconAdapter;
import com.tradehero.th.R;
import com.tradehero.th.api.market.ExchangeCompactDTO;
import com.tradehero.th.models.market.ExchangeCompactSpinnerDTO;
import com.tradehero.th.models.market.ExchangeSpinnerDTOUtil;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import com.tradehero.th.utils.metrics.localytics.THLocalyticsSession;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class TrendingFilterSelectorView extends RelativeLayout
{
    @InjectView(R.id.previous_filter) public ImageButton mPrevious;
    @InjectView(R.id.next_filter) public ImageButton mNext;
    @InjectView(R.id.title) public TextView mTitle;
    @InjectView(R.id.trending_filter_title_icon) public ImageView mTitleIcon;
    @InjectView(R.id.description) public TextView mDescription;
    @InjectView(R.id.exchange_selection) public Spinner mExchangeSelection;
    private SpinnerIconAdapter mExchangeSelectionAdapter;

    private TrendingFilterTypeDTO trendingFilterTypeDTO;
    @Nullable private ExchangeCompactSpinnerDTO[] exchangeCompactSpinnerDTOs;
    private OnFilterTypeChangedListener changedListener;
    @Inject ExchangeSpinnerDTOUtil exchangeSpinnerDTOUtil;
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
        trendingFilterTypeDTO = new TrendingFilterTypeBasicDTO();
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

    public void setUpExchangeSpinner(List<ExchangeCompactDTO> exchangeCompactDTOs)
    {
        Timber.d("Filter setUpExchangeSpinner");
        this.exchangeCompactSpinnerDTOs = exchangeSpinnerDTOUtil.getSpinnerDTOs(getContext(), exchangeCompactDTOs);
        @Nullable int[] spinnerIcons = exchangeSpinnerDTOUtil.getSpinnerIcons(getContext(), exchangeCompactDTOs);
        Spinner exchangeSelection = mExchangeSelection;
        if (exchangeSelection != null)
        {
            //trendingFilterTypeDTOUtil.createDropDownTextsAndIcons(getContext(), exchangeCompactDTOs);
            mExchangeSelectionAdapter = new TrendingFilterSpinnerIconAdapter(
                    getContext(),
                    this.exchangeCompactSpinnerDTOs,
                    spinnerIcons,
                    spinnerIcons);
            mExchangeSelectionAdapter.setDropDownViewResource(R.layout.trending_filter_spinner_dropdown_item);
            exchangeSelection.setAdapter(mExchangeSelectionAdapter);
            if (this.exchangeCompactSpinnerDTOs == null)
            {
                Timber.e(new IllegalArgumentException("exchangeSpinnerDTOs null"), "exchangeSpinnerDTOs null");
            }
            if (trendingFilterTypeDTO == null)
            {
                Timber.e(new IllegalArgumentException("trendingFilterTypeDTO null"), "trendingFilterTypeDTO null");
                trendingFilterTypeDTO = new TrendingFilterTypeBasicDTO();
            }
            exchangeSelection.setSelection(exchangeSpinnerDTOUtil.indexOf(this.exchangeCompactSpinnerDTOs, trendingFilterTypeDTO.exchange));
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
            if (trendingFilterTypeDTO == null)
            {
                trendingFilterTypeDTO = new TrendingFilterTypeBasicDTO();
            }
            if (exchangeCompactSpinnerDTOs != null && exchangeCompactSpinnerDTOs.length > i)
            {
                trendingFilterTypeDTO.exchange = exchangeCompactSpinnerDTOs[i];
            }
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
