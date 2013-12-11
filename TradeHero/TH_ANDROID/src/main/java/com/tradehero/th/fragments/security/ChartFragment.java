package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.persistence.security.SecurityCompactCache;
import com.tradehero.th.utils.yahoo.*;
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

    private boolean viewCreated = false;
    private ImageView stockBgLogo;
    private TimeSpanButtonSet timeSpanButtonSet;
    private TimeSpanButtonSet.OnTimeSpanButtonSelectedListener timeSpanButtonSetListener;
    private TimeSpan timeSpan = TimeSpan.day1;
    private int timeSpanButtonSetVisibility = View.VISIBLE;
    private ChartSize chartSize = ChartSize.medium;

    @Inject protected Lazy<SecurityCompactCache> securityCompactCache;
    @Inject protected Picasso picasso;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);

        Bundle args = getArguments();
        if (args != null)
        {
            timeSpanButtonSetVisibility = args.getInt(BUNDLE_KEY_TIME_SPAN_BUTTON_SET_VISIBILITY, timeSpanButtonSetVisibility);
            timeSpan = TimeSpan.valueOf(args.getString(BUNDLE_KEY_TIME_SPAN_STRING, timeSpan.name()));
            chartSize = ChartSize.valueOf(args.getString(BUNDLE_KEY_CHART_SIZE, chartSize.name()));
        }

        // Override with saved value if any
        if (savedInstanceState != null)
        {
            timeSpanButtonSetVisibility = savedInstanceState.getInt(BUNDLE_KEY_TIME_SPAN_BUTTON_SET_VISIBILITY, timeSpanButtonSetVisibility);
            timeSpan = TimeSpan.valueOf(savedInstanceState.getString(BUNDLE_KEY_TIME_SPAN_STRING, timeSpan.name()));
            chartSize = ChartSize.valueOf(savedInstanceState.getString(BUNDLE_KEY_CHART_SIZE, chartSize.name()));
        }

        stockBgLogo = (ImageView) view.findViewById(R.id.chart_imageView);
        timeSpanButtonSet = (TimeSpanButtonSet) view.findViewById(R.id.yahoo_time_span_button_set);
        if (timeSpanButtonSet != null)
        {
            timeSpanButtonSetListener = new TimeSpanButtonSet.OnTimeSpanButtonSelectedListener()
            {
                @Override public void onTimeSpanButtonSelected(TimeSpan selected)
                {
                    linkWith(selected, true);
                }
            };

            timeSpanButtonSet.addAllChildButtons();
            timeSpanButtonSet.setListener(timeSpanButtonSetListener);
            timeSpanButtonSet.setActive(TimeSpan.month3);
        }

        this.viewCreated = true;

        return view;
    }

    @Override public void onResume()
    {
        super.onResume();
        display();
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
        outState.putString(BUNDLE_KEY_TIME_SPAN_STRING, timeSpan.name());
        outState.putString(BUNDLE_KEY_CHART_SIZE, chartSize.name());
        outState.putInt(BUNDLE_KEY_TIME_SPAN_BUTTON_SET_VISIBILITY, timeSpanButtonSetVisibility);
    }

    @Override public void onDestroyView()
    {
        this.viewCreated = false;

        if (timeSpanButtonSet != null)
        {
            timeSpanButtonSet.setListener(null);
        }
        timeSpanButtonSetListener = null;
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
        if (this.securityId != null)
        {
            securityCompactCache.get().registerListener(this);
            linkWith(securityCompactCache.get().get(this.securityId), andDisplay);
        }
    }

    public void linkWith(TimeSpan timeSpan, boolean andDisplay)
    {
        this.timeSpan = timeSpan;
        if (andDisplay)
        {
            displayBgLogo();
        }
    }

    @Override public void display()
    {
        displayBgLogo();
        displayTimeSpanButtonSet();
    }

    public void displayTimeSpanButtonSet()
    {
        if (timeSpanButtonSet != null)
        {
            timeSpanButtonSet.setVisibility(timeSpanButtonSetVisibility);
        }
    }

    public void displayBgLogo()
    {
        if (this.viewCreated && stockBgLogo != null)
        {
            if (value != null && value.yahooSymbol != null)
            {
                String imageURL = Utils.getChartURL(value.yahooSymbol, chartSize, timeSpan);
                picasso.load(imageURL).into(stockBgLogo);
            }
            postChooseOtherSize();
        }
    }

    public void postChooseOtherSize()
    {
        View view = getView();
        if (view != null)
        {
            view.postDelayed(new Runnable()
            {
                @Override public void run()
                {
                    if (ChartFragment.this.viewCreated && stockBgLogo != null)
                    {
                        ChartSize newChartSize = Utils.getPreferredSize(stockBgLogo.getWidth(), stockBgLogo.getHeight());
                        if (newChartSize != chartSize)
                        {
                            chartSize = newChartSize;
                            displayBgLogo();
                        }
                    }
                }
            }, 500);
        }
    }
}



