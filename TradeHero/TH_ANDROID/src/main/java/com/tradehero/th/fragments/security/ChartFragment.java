package com.tradehero.th.fragments.security;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.models.chart.yahoo.YahooChartDTO;
import com.tradehero.th.models.chart.yahoo.YahooChartSize;
import com.tradehero.th.models.chart.yahoo.YahooTimeSpan;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.news.TimeSpanButtonSet;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Created by julien on 9/10/13
 */
public class ChartFragment extends AbstractSecurityInfoFragment<SecurityCompactDTO>
{
    private final static String TAG = ChartFragment.class.getSimpleName();
    public final static String BUNDLE_KEY_TIME_SPAN_BUTTON_SET_VISIBILITY = ChartFragment.class.getName() + ".timeSpanButtonSetVisibility";
    public final static String BUNDLE_KEY_TIME_SPAN_STRING = ChartFragment.class.getName() + ".timeSpanString";
    public final static String BUNDLE_KEY_CHART_SIZE = ChartFragment.class.getName() + ".chartSize";

    private ImageView chartImage;
    private TimeSpanButtonSet timeSpanButtonSet;
    private TimeSpanButtonSet.OnTimeSpanButtonSelectedListener timeSpanButtonSetListener;
    private YahooChartDTO yahooChartDTO;
    private int timeSpanButtonSetVisibility = View.VISIBLE;

    @Inject protected Lazy<SecurityCompactCache> securityCompactCache;
    @Inject protected Picasso picasso;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
        yahooChartDTO = new YahooChartDTO("", YahooChartSize.medium, YahooTimeSpan.day1);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);

        Bundle args = getArguments();
        if (args != null)
        {
            timeSpanButtonSetVisibility = args.getInt(BUNDLE_KEY_TIME_SPAN_BUTTON_SET_VISIBILITY, timeSpanButtonSetVisibility);
            yahooChartDTO.timeSpan = YahooTimeSpan.valueOf(args.getString(BUNDLE_KEY_TIME_SPAN_STRING, yahooChartDTO.timeSpan.name()));
            yahooChartDTO.size = YahooChartSize.valueOf(args.getString(BUNDLE_KEY_CHART_SIZE, yahooChartDTO.size.name()));
        }

        // Override with saved value if any
        if (savedInstanceState != null)
        {
            timeSpanButtonSetVisibility = savedInstanceState.getInt(BUNDLE_KEY_TIME_SPAN_BUTTON_SET_VISIBILITY, timeSpanButtonSetVisibility);
            yahooChartDTO.timeSpan = YahooTimeSpan.valueOf(savedInstanceState.getString(BUNDLE_KEY_TIME_SPAN_STRING, yahooChartDTO.timeSpan.name()));
            yahooChartDTO.size = YahooChartSize.valueOf(savedInstanceState.getString(BUNDLE_KEY_CHART_SIZE, yahooChartDTO.size.name()));
        }

        chartImage = (ImageView) view.findViewById(R.id.chart_imageView);
        if (chartImage != null)
        {
            chartImage.setOnClickListener(chartImageClickListener);
        }

        this.timeSpanButtonSetListener = new TimeSpanButtonSet.OnTimeSpanButtonSelectedListener()
        {
            @Override public void onTimeSpanButtonSelected(YahooTimeSpan selected)
            {
                linkWith(selected, true);
            }
        };

        TimeSpanButtonSet timeSpanButtonSetTemp = (TimeSpanButtonSet) view.findViewById(R.id.yahoo_time_span_button_set);
        if (timeSpanButtonSetTemp != null)
        {
            timeSpanButtonSetTemp.addAllChildButtons();
            timeSpanButtonSetTemp.setListener(this.timeSpanButtonSetListener);
            timeSpanButtonSetTemp.setActive(YahooTimeSpan.month3);
        }
        this.timeSpanButtonSet = timeSpanButtonSetTemp;

        return view;
    }

    @Override public void onPause()
    {
        if (securityId != null)
        {
            securityCompactCache.get().unRegisterListener(this);
        }
        super.onPause();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString(BUNDLE_KEY_TIME_SPAN_STRING, yahooChartDTO.timeSpan.name());
        outState.putString(BUNDLE_KEY_CHART_SIZE, yahooChartDTO.size.name());
        outState.putInt(BUNDLE_KEY_TIME_SPAN_BUTTON_SET_VISIBILITY, timeSpanButtonSetVisibility);
    }

    @Override public void onDestroyView()
    {
        if (chartImage != null)
        {
            chartImage.setOnClickListener(null);
            this.chartImage = null;
        }
        TimeSpanButtonSet buttonSet = this.timeSpanButtonSet;
        if (buttonSet != null)
        {
            buttonSet.setListener(null);
        }
        this.timeSpanButtonSet = null;
        this.timeSpanButtonSetListener = null;
        super.onDestroyView();
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
            securityCompactCache.get().registerListener(this);
            linkWith(securityCompactCache.get().get(securityId), andDisplay);
        }
    }

    @Override public void linkWith(SecurityCompactDTO value, boolean andDisplay)
    {
        super.linkWith(value, andDisplay);
        if (value != null)
        {
            yahooChartDTO.yahooSymbol = value.yahooSymbol;
        }
        if (andDisplay)
        {
            displayChartImage();
        }
    }

    public void linkWith(YahooTimeSpan timeSpan, boolean andDisplay)
    {
        yahooChartDTO.timeSpan = timeSpan;
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
        if (buttonSet != null)
        {
            buttonSet.setVisibility(timeSpanButtonSetVisibility);
        }
    }

    public void displayChartImage()
    {
        ImageView image = this.chartImage;
        if (image != null)
        {
            String imageURL = yahooChartDTO.getChartUrl();
            this.picasso.load(imageURL).into(image);
            postChooseOtherSize();
        }
    }

    public void postChooseOtherSize()
    {
        postDelayed(getChooseOtherSizeRunnable(), 500);
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
            YahooChartSize newChartSize = YahooChartSize.getPreferredSize(image.getWidth(), image.getHeight());
            if (newChartSize != yahooChartDTO.size)
            {
                yahooChartDTO.size = newChartSize;
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



