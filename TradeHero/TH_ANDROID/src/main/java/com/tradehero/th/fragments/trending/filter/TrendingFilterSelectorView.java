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
import timber.log.Timber;

public class TrendingFilterSelectorView extends RelativeLayout
{
    public ImageButton mPrevious;
    public ImageButton mNext;
    public TextView mTitle;
    public ImageView mTitleIcon;
    public TextView mDescription;
    public Spinner mExchangeSelection;
    private SpinnerIconAdapter mExchangeSelectionAdapter;

    private TrendingFilterTypeDTO trendingFilterTypeDTO;
    private ExchangeCompactSpinnerDTO[] exchangeCompactSpinnerDTOs;
    private OnFilterTypeChangedListener changedListener;
    //@Inject TrendingFilterTypeDTOUtil trendingFilterTypeDTOUtil;
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

        mPrevious = (ImageButton) findViewById(R.id.previous_filter);
        mNext = (ImageButton) findViewById(R.id.next_filter);
        mTitle = (TextView) findViewById(R.id.title);
        mTitleIcon = (ImageView) findViewById(R.id.trending_filter_title_icon);
        mDescription = (TextView) findViewById(R.id.description);
        mExchangeSelection = (Spinner) findViewById(R.id.exchange_selection);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (mPrevious != null)
        {
            mPrevious.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    handlePreviousClicked();
                }
            });
        }
        if (mNext != null)
        {
            mNext.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    handleNextClicked();
                }
            });
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        if (mPrevious != null)
        {
            mPrevious.setOnClickListener(null);
        }
        if (mNext != null)
        {
            mNext.setOnClickListener(null);
        }
        super.onDetachedFromWindow();
    }

    public void onDestroy()
    {
        mPrevious = null;
        mNext = null;

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
        int[] spinnerIcons = exchangeSpinnerDTOUtil.getSpinnerIcons(getContext(), exchangeCompactDTOs);
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
            exchangeSelection.setOnItemSelectedListener(new TrendingFilterSelectorViewSpinnerListener());
        }
    }

    public void apply(TrendingFilterTypeDTO typeDTO)
    {
        if (typeDTO == null)
        {
            Timber.e(new IllegalArgumentException("Cannot apply typeDTO null"), "");
        }
        this.trendingFilterTypeDTO = typeDTO;
        if (typeDTO != null)
        {
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
    }

    private void handlePreviousClicked()
    {
        Timber.d("Wangliang TrendingFilterSelectorViewSpinnerListener handlePreviousClicked");
        apply(trendingFilterTypeDTO.getPrevious());
        notifyListenerChanged();
    }

    private void handleNextClicked()
    {
        Timber.d("Wangliang TrendingFilterSelectorViewSpinnerListener handleNextClicked");
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

    private class TrendingFilterSelectorViewSpinnerListener implements AdapterView.OnItemSelectedListener
    {
        @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
        {
            Timber.d("Wangliang TrendingFilterSelectorViewSpinnerListener onItemSelected");
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
