package com.tradehero.th.fragments.security;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import com.etiennelawlor.quickreturn.library.views.NotifyingScrollView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.widgets.AspectRatioImageViewCallback;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.BottomTabsQuickReturnScrollViewListener;
import com.tradehero.th.R;
import com.tradehero.th.activities.StockChartActivity;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.security.compact.WarrantDTO;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.chart.ChartDTO;
import com.tradehero.th.models.chart.ChartDTOFactory;
import com.tradehero.th.models.chart.ChartSize;
import com.tradehero.th.models.chart.ChartTimeSpan;
import com.tradehero.th.models.number.THSignedMoney;
import com.tradehero.th.models.number.THSignedNumber;
import com.tradehero.th.persistence.security.SecurityCompactCacheRx;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.events.ChartTimeEvent;
import com.tradehero.th.widget.news.TimeSpanButtonSet;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.inject.Inject;
import android.support.annotation.Nullable;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class ChartFragment extends AbstractSecurityInfoFragment<SecurityCompactDTO>
{
    public final static String BUNDLE_KEY_TIME_SPAN_BUTTON_SET_VISIBILITY = ChartFragment.class.getName() + ".timeSpanButtonSetVisibility";
    public final static String BUNDLE_KEY_TIME_SPAN_SECONDS_LONG = ChartFragment.class.getName() + ".timeSpanSecondsLong";
    public final static String BUNDLE_KEY_CHART_SIZE_ARRAY_INT = ChartFragment.class.getName() + ".chartSizeArrayInt";
    public final static String BUNDLE_KEY_ARGUMENTS = ChartFragment.class.getName() + ".arguments";

    @InjectView(R.id.chart_imageView) protected ChartImageView chartImage;
    private TimeSpanButtonSet timeSpanButtonSet;
    private TimeSpanButtonSet.OnTimeSpanButtonSelectedListener timeSpanButtonSetListener;
    private ChartDTO chartDTO;
    @Nullable private WarrantDTO warrantDTO;
    private int timeSpanButtonSetVisibility = View.VISIBLE;

    @InjectView(R.id.chart_scroll_view) @Optional NotifyingScrollView scrollView;

    @InjectView(R.id.close) @Optional protected Button mCloseButton;

    @InjectView(R.id.chart_image_wrapper) @Optional protected BetterViewAnimator chartImageWrapper;

    // Warrant specific
    @InjectView(R.id.row_warrant_type) @Optional protected View rowWarrantType;
    @InjectView(R.id.vwarrant_type) @Optional protected TextView mWarrantType;
    @InjectView(R.id.row_warrant_code) @Optional protected View rowWarrantCode;
    @InjectView(R.id.vwarrant_code) @Optional protected TextView mWarrantCode;
    @InjectView(R.id.row_warrant_expiry) @Optional protected View rowWarrantExpiry;
    @InjectView(R.id.vwarrant_expiry) @Optional protected TextView mWarrantExpiry;
    @InjectView(R.id.row_warrant_strike_price) @Optional protected View rowStrikePrice;
    @InjectView(R.id.vwarrant_strike_price) @Optional protected TextView mStrikePrice;
    @InjectView(R.id.row_warrant_underlying) @Optional protected View rowUnderlying;
    @InjectView(R.id.vwarrant_underlying) @Optional protected TextView mUnderlying;
    @InjectView(R.id.row_warrant_issuer) @Optional protected View rowIssuer;
    @InjectView(R.id.vwarrant_issuer) @Optional protected TextView mIssuer;

    @InjectView(R.id.vprevious_close) @Optional protected TextView mPreviousClose;
    @InjectView(R.id.vopen) @Optional protected TextView mOpen;
    @InjectView(R.id.vdays_high) @Optional protected TextView mDaysHigh;
    @InjectView(R.id.vdays_low) @Optional protected TextView mDaysLow;
    @InjectView(R.id.vpe_ratio) @Optional protected TextView mPERatio;
    @InjectView(R.id.veps) @Optional protected TextView mEps;
    @InjectView(R.id.vvolume) @Optional protected TextView mVolume;
    @InjectView(R.id.vavg_volume) @Optional protected TextView mAvgVolume;

    @Inject SecurityCompactCacheRx securityCompactCache;
    @Nullable Subscription securityCompactCacheSubscription;
    @Inject Picasso picasso;
    @Inject ChartDTOFactory chartDTOFactory;
    @Inject Analytics analytics;
    @Inject @BottomTabsQuickReturnScrollViewListener protected NotifyingScrollView.OnScrollChangedListener
            dashboardBottomTabScrollViewScrollListener;
    private Runnable chooseChartImageSizeTask;
    private Callback chartImageCallback;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        HierarchyInjector.inject(this);
        chartDTO = chartDTOFactory.createChartDTO();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);

        ButterKnife.inject(this, view);

        Bundle args = getArguments();
        if (args != null)
        {
            timeSpanButtonSetVisibility = args.getInt(BUNDLE_KEY_TIME_SPAN_BUTTON_SET_VISIBILITY, timeSpanButtonSetVisibility);
            if (args.containsKey(BUNDLE_KEY_TIME_SPAN_SECONDS_LONG))
            {
                chartDTO.setChartTimeSpan(new ChartTimeSpan(args.getLong(BUNDLE_KEY_TIME_SPAN_SECONDS_LONG)));
            }
            if (args.containsKey(BUNDLE_KEY_CHART_SIZE_ARRAY_INT))
            {
                chartDTO.setChartSize(new ChartSize(args.getIntArray(BUNDLE_KEY_CHART_SIZE_ARRAY_INT)));
            }
        }

        chartDTO.setIncludeVolume(chartImage.includeVolume);

        // Override with saved value if any
        if (savedInstanceState != null)
        {
            timeSpanButtonSetVisibility = savedInstanceState.getInt(BUNDLE_KEY_TIME_SPAN_BUTTON_SET_VISIBILITY, timeSpanButtonSetVisibility);
            if (savedInstanceState.containsKey(BUNDLE_KEY_TIME_SPAN_SECONDS_LONG))
            {
                chartDTO.setChartTimeSpan(new ChartTimeSpan(savedInstanceState.getLong(BUNDLE_KEY_TIME_SPAN_SECONDS_LONG)));
            }
            if (savedInstanceState.containsKey(BUNDLE_KEY_CHART_SIZE_ARRAY_INT))
            {
                chartDTO.setChartSize(new ChartSize(savedInstanceState.getIntArray(BUNDLE_KEY_CHART_SIZE_ARRAY_INT)));
            }
        }

        if (chartImage != null)
        {
            chartImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            if (getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            {
                chartImage.setOnClickListener(createChartImageClickListener());
            }
        }

        this.timeSpanButtonSetListener = selected -> {
            analytics.fireEvent(new ChartTimeEvent(securityId, selected));
            linkWith(selected, true);
        };

        TimeSpanButtonSet timeSpanButtonSetTemp = (TimeSpanButtonSet) view.findViewById(R.id.chart_time_span_button_set);
        if (timeSpanButtonSetTemp != null)
        {
            timeSpanButtonSetTemp.addAllChildButtons();
            timeSpanButtonSetTemp.setListener(this.timeSpanButtonSetListener);
            timeSpanButtonSetTemp.setActive(chartDTO.getChartTimeSpan());
        }
        this.timeSpanButtonSet = timeSpanButtonSetTemp;

        if (mCloseButton != null)
        {
            mCloseButton.setOnClickListener(v -> getActivity().finish());
        }

        chartImageCallback = new AspectRatioImageViewCallback(chartImage)
        {
            @Override public void onSuccess()
            {
                super.onSuccess();
                if (chartImageWrapper != null)
                {
                    chartImageWrapper.setDisplayedChildByLayoutId(chartImage.getId());
                }
            }

            @Override public void onError()
            {
                super.onError();
                Timber.d("Load chartImage error");
            }
        };

        if(scrollView != null)
        {
            scrollView.setOnScrollChangedListener(dashboardBottomTabScrollViewScrollListener);
        }

        return view;
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putLong(BUNDLE_KEY_TIME_SPAN_SECONDS_LONG, chartDTO.getChartTimeSpan().duration);
        outState.putIntArray(BUNDLE_KEY_CHART_SIZE_ARRAY_INT, chartDTO.getChartSize().getSizeArray());
        outState.putInt(BUNDLE_KEY_TIME_SPAN_BUTTON_SET_VISIBILITY, timeSpanButtonSetVisibility);
    }

    @Override public void onDestroyView()
    {
        detachSubscription(securityCompactCacheSubscription);
        securityCompactCacheSubscription = null;
        if (chartImage != null)
        {
            chartImage.setOnClickListener(null);
        }
        this.chartImage = null;

        TimeSpanButtonSet buttonSet = this.timeSpanButtonSet;
        if (buttonSet != null)
        {
            buttonSet.setListener(null);
        }
        this.timeSpanButtonSet = null;
        this.timeSpanButtonSetListener = null;
        View rootView = getView();
        if (chooseChartImageSizeTask != null && rootView != null)
        {
            rootView.removeCallbacks(chooseChartImageSizeTask);
            chooseChartImageSizeTask = null;
        }
        if (mCloseButton != null)
        {
            mCloseButton.setOnClickListener(null);
            mCloseButton = null;
        }
        if(scrollView != null)
        {
            scrollView.setOnScrollChangedListener(null);
        }
        chartImageCallback = null;
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override protected SecurityCompactCacheRx getInfoCache()
    {
        return securityCompactCache;
    }

    public int getTimeSpanButtonSetVisibility()
    {
        return timeSpanButtonSetVisibility;
    }

    public void setTimeSpanButtonSetVisibility(int timeSpanButtonSetVisibility)
    {
        this.timeSpanButtonSetVisibility = timeSpanButtonSetVisibility;
        displayTimeSpanButtonSet();
    }

    @Override public void linkWith(final SecurityId securityId, final boolean andDisplay)
    {
        super.linkWith(securityId, andDisplay);
        if (securityId != null)
        {
            detachSubscription(securityCompactCacheSubscription);
            securityCompactCacheSubscription = AndroidObservable.bindFragment(this, securityCompactCache.get(securityId))
                    .subscribe(new Observer<Pair<SecurityId, SecurityCompactDTO>>()
                    {
                        @Override public void onCompleted()
                        {
                        }

                        @Override public void onError(Throwable e)
                        {
                            THToast.show(R.string.error_fetch_security_info);
                        }

                        @Override public void onNext(Pair<SecurityId, SecurityCompactDTO> pair)
                        {
                            linkWith(pair.second, andDisplay);
                        }
                    });
        }
        if (andDisplay)
        {
        }
    }

    @Override public void linkWith(SecurityCompactDTO value, boolean andDisplay)
    {
        super.linkWith(value, andDisplay);
        if (value != null)
        {
            ChartDTO chartDTOCopy = chartDTO;
            if (chartDTOCopy != null)
            {
                chartDTOCopy.setSecurityCompactDTO(value);
            }
            linkWith((value instanceof WarrantDTO) ? (WarrantDTO) value : null, andDisplay);
        }
        if (andDisplay)
        {
            displayChartImage();
            displayPreviousClose();
            displayOpen();
            displayDaysHigh();
            displayDaysLow();
            displayPERatio();
            displayEps();
            displayVolume();
            displayAvgVolume();
        }
    }

    public void linkWith(ChartTimeSpan timeSpan, boolean andDisplay)
    {
        chartDTO.setChartTimeSpan(timeSpan);
        if (andDisplay)
        {
            displayChartImage();
        }
    }

    public void linkWith(@Nullable WarrantDTO warrantDTO, boolean andDisplay)
    {
        this.warrantDTO = warrantDTO;
        if (andDisplay)
        {
            displayWarrantRows();
            displayWarrantType();
            displayWarrantCode();
            displayExpiry();
            displayStrikePrice();
            displayUnderlying();
            displayIssuer();
        }
    }

    @Override public void display()
    {
        displayChartImage();
        displayTimeSpanButtonSet();
    }

    public void displayTimeSpanButtonSet()
    {
        TimeSpanButtonSet buttonSet = this.timeSpanButtonSet;
        if (!isDetached() && buttonSet != null)
        {
            buttonSet.setVisibility(timeSpanButtonSetVisibility);
        }
    }

    public void displayChartImage()
    {
        ImageView image = this.chartImage;
        if (!isDetached() && image != null)
        {
            String imageURL = chartDTO.getChartUrl();
            // HACK TODO find something better than skipCache to avoid OutOfMemory
            this.picasso
                    .load(imageURL)
                    .skipMemoryCache()
                    .into(image, chartImageCallback);

            if (getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            {
                postChooseOtherSize();
            }
        }
    }

    public void postChooseOtherSize()
    {
        chooseChartImageSizeTask = getChooseOtherSizeRunnable();
        postDelayed(chooseChartImageSizeTask, 500);
    }

    protected Runnable getChooseOtherSizeRunnable()
    {
        return () -> chooseOtherSize();
    }

    protected void chooseOtherSize()
    {
        ImageView image = chartImage;
        if (image != null)
        {
            ChartSize currentSize = chartDTO.getChartSize();
            chartDTO.setChartSize(new ChartSize(image.getWidth(), image.getHeight()));
            if (!chartDTO.getChartSize().equals(currentSize))
            {
                displayChartImage();
            }
        }
    }

    protected void postDelayed(Runnable runnable, long delayMillis)
    {
        View view = getView();
        if (view != null)
        {
            view.postDelayed(runnable, delayMillis);
        }
    }

    private View.OnClickListener createChartImageClickListener()
    {
        return v -> {
            //Intent intent = new Intent(BuySellFragment.EVENT_CHART_IMAGE_CLICKED);
            //LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            Intent intent = new Intent(getActivity().getApplicationContext(), StockChartActivity.class);
            Bundle args = new Bundle();
            args.putBundle(BUNDLE_KEY_ARGUMENTS, getArguments());
            intent.putExtras(args);
            getActivity().startActivity(intent);
        };
    }

    public void displayWarrantRows()
    {
        if (rowWarrantType != null)
        {
            rowWarrantType.setVisibility(getWarrantVisibility());
        }
        if (rowWarrantCode != null)
        {
            rowWarrantCode.setVisibility(getWarrantVisibility());
        }
        if (rowWarrantExpiry != null)
        {
            rowWarrantExpiry.setVisibility(getWarrantVisibility());
        }
        if (rowStrikePrice != null)
        {
            rowStrikePrice.setVisibility(getWarrantVisibility());
        }
        if (rowUnderlying != null)
        {
            rowUnderlying.setVisibility(getWarrantVisibility());
        }
        if (rowIssuer != null)
        {
            rowIssuer.setVisibility(getWarrantVisibility());
        }
    }

    private int getWarrantVisibility()
    {
        return (warrantDTO == null) ? View.GONE : View.VISIBLE;
    }

    public void displayWarrantType()
    {
        if (!isDetached() && mWarrantType != null)
        {
            if (warrantDTO == null || warrantDTO.getWarrantType() == null)
            {
                mWarrantType.setText(R.string.na);
            }
            else
            {
                int warrantTypeStringResId;
                switch (warrantDTO.getWarrantType())
                {
                    case CALL:
                        warrantTypeStringResId = R.string.warrant_type_call;
                        break;
                    case PUT:
                        warrantTypeStringResId = R.string.warrant_type_put;
                        break;
                    default:
                        throw new IllegalArgumentException("Unhandled warrant type " + warrantDTO.getWarrantType());
                }
                mWarrantType.setText(warrantTypeStringResId);
            }
        }
    }

    public void displayWarrantCode()
    {
        if (!isDetached() && mWarrantCode != null)
        {
            if (value == null || value.symbol == null)
            {
                mWarrantCode.setText(R.string.na);
            }
            else
            {
                mWarrantCode.setText(value.symbol);
            }
        }
    }

    public void displayExpiry()
    {
        if (!isDetached() && mWarrantExpiry != null)
        {
            if (warrantDTO == null || warrantDTO.expiryDate == null)
            {
                mWarrantExpiry.setText(R.string.na);
            }
            else
            {
                SimpleDateFormat df = new SimpleDateFormat("d MMM yy", Locale.US);
                mWarrantExpiry.setText(df.format(warrantDTO.expiryDate));
            }
        }
    }

    public void displayStrikePrice()
    {
        if (!isDetached() && mStrikePrice != null)
        {
            if (warrantDTO == null || warrantDTO.strikePrice == null || warrantDTO.strikePriceCcy == null)
            {
                mStrikePrice.setText(R.string.na);
            }
            else
            {
                mStrikePrice.setText(THSignedMoney.builder(warrantDTO.strikePrice)
                        .currency(warrantDTO.strikePriceCcy)
                        .build().toString());
            }
        }
    }

    public void displayUnderlying()
    {
        if (!isDetached() && mUnderlying != null)
        {
            if (warrantDTO == null || warrantDTO.underlyingName == null)
            {
                mUnderlying.setText(R.string.na);
            }
            else
            {
                mUnderlying.setText(warrantDTO.underlyingName);
            }
        }
    }

    public void displayIssuer()
    {
        if (!isDetached() && mIssuer != null)
        {
            if (warrantDTO == null || warrantDTO.issuerName == null)
            {
                mIssuer.setText(R.string.na);
            }
            else
            {
                mIssuer.setText(warrantDTO.issuerName.toUpperCase()); // HACK upperCase
            }
        }
    }

    public void displayPreviousClose()
    {
        if (!isDetached() && mPreviousClose != null)
        {
            if (value == null || value.previousClose == null)
            {
                mPreviousClose.setText(R.string.na);
            }
            else
            {
                mPreviousClose.setText(THSignedMoney.builder(value.previousClose)
                        .currency(value.currencyDisplay)
                        .build().toString());
            }
        }
    }

    public void displayOpen()
    {
        if (!isDetached() && mOpen != null)
        {
            if (value == null || value.open == null)
            {
                mOpen.setText(R.string.na);
            }
            else
            {
                mOpen.setText(THSignedMoney.builder(value.open)
                        .currency(value.currencyDisplay)
                        .build().toString());
            }
        }
    }

    public void displayDaysHigh()
    {
        if (!isDetached() && mDaysHigh != null)
        {
            if (value == null || value.high == null)
            {
                mDaysHigh.setText(R.string.na);
            }
            else
            {
                mDaysHigh.setText(THSignedMoney.builder(value.high)
                        .currency(value.currencyDisplay)
                        .build().toString());
            }
        }
    }

    public void displayDaysLow()
    {
        if (!isDetached() && mDaysLow != null)
        {
            if (value == null || value.low == null)
            {
                mDaysLow.setText(R.string.na);
            }
            else
            {
                mDaysLow.setText(THSignedMoney.builder(value.low)
                        .currency(value.currencyDisplay)
                        .build().toString());
            }
        }
    }

    public void displayPERatio()
    {
        if (!isDetached() && mPERatio != null)
        {
            if (value == null || value.pe == null)
            {
                mPERatio.setText(R.string.na);
            }
            else
            {
                mPERatio.setText(THSignedNumber.builder(value.pe)
                        .build().toString());
            }
        }
    }

    public void displayEps()
    {
        if (!isDetached() && mEps != null)
        {
            if (value == null || value.eps == null)
            {
                mEps.setText(R.string.na);
            }
            else
            {
                mEps.setText(THSignedNumber.builder(value.eps)
                        .build().toString());
            }
        }
    }

    public void displayVolume()
    {
        if (!isDetached() && mVolume != null)
        {
            if (value == null || value.volume == null)
            {
                mVolume.setText(R.string.na);
            }
            else
            {
                mVolume.setText(THSignedNumber.builder(value.volume)
                        .build().toString());
            }
        }
    }

    public void displayAvgVolume()
    {
        if (!isDetached() && mAvgVolume != null)
        {
            if (value == null || value.averageDailyVolume == null)
            {
                mAvgVolume.setText(R.string.na);
            }
            else
            {
                mAvgVolume.setText(THSignedNumber.builder(value.averageDailyVolume)
                        .build().toString());
            }
        }
    }
}


