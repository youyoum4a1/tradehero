package com.tradehero.th.fragments.security;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
            chartImage.setOnClickListener(chartImageClickListener);
            chartImage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
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
            postChooseOtherSize();
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
}



