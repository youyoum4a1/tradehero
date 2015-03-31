package com.tradehero.th.fragments.news;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.AnalyticsDuration;
import com.tradehero.th.utils.metrics.events.AttributesEvent;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

public class NewsWebFragment extends WebViewFragment
{
    private static final String BUNDLE_KEY_PREVIOUS_SCREEN = NewsWebFragment.class + ".previousScreen";

    public static void putPreviousScreen(@NonNull Bundle bundle, @NonNull String previousScreen)
    {
        //TODO Maybe use Enum here.
        bundle.putString(BUNDLE_KEY_PREVIOUS_SCREEN, previousScreen);
    }

    @Inject Analytics analytics;
    private String previousScreen;
    
    private long beginTime;

    private String getPreviousScreenFromBundle()
    {
        if(getArguments() != null)
        {
            return getArguments().getString(BUNDLE_KEY_PREVIOUS_SCREEN);
        }
        return null;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        previousScreen = getPreviousScreenFromBundle();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(R.string.discovery_news);
    }

    @Override public void onResume()
    {
        super.onResume();
        beginTime = System.currentTimeMillis();
    }

    @Override public void onPause()
    {
        reportAnalytics();
        super.onPause();
    }

    private void reportAnalytics()
    {
        Map<String, String> collections = new HashMap<>();
        collections.put(AnalyticsConstants.PreviousScreen, previousScreen);
        collections.put(AnalyticsConstants.TimeOnScreen, AnalyticsDuration.sinceTimeMillis(beginTime).toString());
        analytics.fireEvent(new AttributesEvent(AnalyticsConstants.NewsItem_Show, collections));
    }
}
