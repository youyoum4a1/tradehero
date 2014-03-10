package com.tradehero.th.fragments.security;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.tradehero.common.persistence.LiveDTOCache;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.models.chart.ChartDTO;
import com.tradehero.th.models.chart.ChartDTOFactory;
import com.tradehero.th.models.chart.ChartSize;
import com.tradehero.th.models.chart.ChartTimeSpan;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.news.TimeSpanButtonSet;
import javax.inject.Inject;

/**
 * Created by julien on 9/10/13
 */
public class ChartFragment extends AbstractSecurityInfoFragment<SecurityCompactDTO>
{
    private final static String TAG = ChartFragment.class.getSimpleName();
    public final static String BUNDLE_KEY_TIME_SPAN_BUTTON_SET_VISIBILITY = ChartFragment.class.getName() + ".timeSpanButtonSetVisibility";
    public final static String BUNDLE_KEY_TIME_SPAN_SECONDS_LONG = ChartFragment.class.getName() + ".timeSpanSecondsLong";
    public final static String BUNDLE_KEY_CHART_SIZE_ARRAY_INT = ChartFragment.class.getName() + ".chartSizeArrayInt";

    private ImageView chartImage;
    private TimeSpanButtonSet timeSpanButtonSet;
    private TimeSpanButtonSet.OnTimeSpanButtonSelectedListener timeSpanButtonSetListener;
    private ChartDTO chartDTO;
    private int timeSpanButtonSetVisibility = View.VISIBLE;

    private TextView mPreviousClose;
    private TextView mOpen;
    private TextView mDaysHigh;
    private TextView mDaysLow;
    private TextView mPERatio;
    private TextView mEps;
    private TextView mVolume;
    private TextView mAvgVolume;

    @Inject protected SecurityCompactCache securityCompactCache;
    @Inject protected Picasso picasso;
    @Inject protected ChartDTOFactory chartDTOFactory;
    private Runnable chooseChartImageSizeTask;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
        chartDTO = chartDTOFactory.createChartDTO();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);

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

        chartImage = (ImageView) view.findViewById(R.id.chart_imageView);
        if (chartImage != null)
        {
            chartImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            //chartImage.setOnClickListener(chartImageClickListener);//temp
        }

        this.timeSpanButtonSetListener = new TimeSpanButtonSet.OnTimeSpanButtonSelectedListener()
        {
            @Override public void onTimeSpanButtonSelected(ChartTimeSpan selected)
            {
                linkWith(selected, true);
            }
        };

        TimeSpanButtonSet timeSpanButtonSetTemp = (TimeSpanButtonSet) view.findViewById(R.id.chart_time_span_button_set);
        if (timeSpanButtonSetTemp != null)
        {
            timeSpanButtonSetTemp.addAllChildButtons();
            timeSpanButtonSetTemp.setListener(this.timeSpanButtonSetListener);
            timeSpanButtonSetTemp.setActive(chartDTO.getChartTimeSpan());
        }
        this.timeSpanButtonSet = timeSpanButtonSetTemp;

        mPreviousClose = (TextView) view.findViewById(R.id.vprevious_close);
        mOpen = (TextView) view.findViewById(R.id.vopen);
        mDaysHigh = (TextView) view.findViewById(R.id.vdays_high);
        mDaysLow = (TextView) view.findViewById(R.id.vdays_low);
        mPERatio = (TextView) view.findViewById(R.id.vpe_ratio);
        mEps = (TextView) view.findViewById(R.id.veps);
        mVolume = (TextView) view.findViewById(R.id.vvolume);
        mAvgVolume = (TextView) view.findViewById(R.id.vavg_volume);

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
        if (chartImage != null)
        {
            chartImage.setOnClickListener(null);
            chartImage.setImageDrawable(null);
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
        super.onDestroyView();
    }

    @Override LiveDTOCache<SecurityId, SecurityCompactDTO> getInfoCache()
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

    @Override public void linkWith(SecurityId securityId, boolean andDisplay)
    {
        super.linkWith(securityId, andDisplay);
        if (securityId != null)
        {
            linkWith(securityCompactCache.get(securityId), andDisplay);
            if (andDisplay)
            {
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
    }

    @Override public void linkWith(SecurityCompactDTO value, boolean andDisplay)
    {
        super.linkWith(value, andDisplay);
        if (value != null)
        {
            chartDTO.setSecurityCompactDTO(value);
        }
        if (andDisplay)
        {
            displayChartImage();
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
            this.picasso.load(imageURL).skipMemoryCache().into(image);
            //postChooseOtherSize();//temp
        }
    }

    public void postChooseOtherSize()
    {
        chooseChartImageSizeTask = getChooseOtherSizeRunnable();
        postDelayed(chooseChartImageSizeTask, 500);
    }

    protected Runnable getChooseOtherSizeRunnable()
    {
        return new Runnable()
        {
            @Override public void run()
            {
                chooseOtherSize();
            }
        };
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

    private View.OnClickListener chartImageClickListener = new View.OnClickListener()
    {
        @Override public void onClick(View v)
        {
            Intent intent = new Intent(BuySellFragment.EVENT_CHART_IMAGE_CLICKED);
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
        }
    };

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
                mPreviousClose.setText(String.format("%s %,.3f", value.currencyDisplay, value.previousClose.doubleValue()));
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
                mOpen.setText(String.format("%s %,.2f", value.currencyDisplay, value.open.doubleValue()));
            }
        }
        //double open = YUtils.parseQuoteValue(yQuotes.get("Open"));
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
                mDaysHigh.setText(String.format("%s %,.2f", value.currencyDisplay, value.high.doubleValue()));
            }
        }
        //double daysHigh = YUtils.parseQuoteValue(yQuotes.get("Day's High"));
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
                mDaysLow.setText(String.format("%s %,.2f", value.currencyDisplay, value.low.doubleValue()));
            }
        }
        //double daysLow = YUtils.parseQuoteValue(yQuotes.get("Day's Low"));
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
                mPERatio.setText(String.format("%,.2f", value.pe.doubleValue()));
            }
        }
        //double peRatio = YUtils.parseQuoteValue(yQuotes.get("P/E Ratio"));
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
                mEps.setText(String.format("%,.3f", value.eps.doubleValue()));
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
                mVolume.setText(String.format("%,.0f", value.volume.doubleValue()));
            }
        }
        //double volume = YUtils.parseQuoteValue(yQuotes.get("Volume"));
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
                mAvgVolume.setText(String.format("%,.0f", value.averageDailyVolume.doubleValue()));
            }
        }
        //double avgVolume = YUtils.parseQuoteValue(yQuotes.get("Average Daily Volume"));
    }
}



