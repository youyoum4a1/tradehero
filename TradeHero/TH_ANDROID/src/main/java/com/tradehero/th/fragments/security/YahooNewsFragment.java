package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.yahoo.NewsList;
import com.tradehero.th.persistence.yahoo.NewsCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

/**
 * Created by julien on 10/10/13
 * Display a ListView of Yahoo News object for a given SecurityId - It uses the NewsCache to get or fetch the Yahoo news
 * as needed. In case the news are not in the cache, the download is done in the background using the `fetchTask` AsyncTask.
 * The task is cancelled when the fragment is paused.
 */
public class YahooNewsFragment extends AbstractSecurityInfoFragment<NewsList>
{
    private final static String TAG = YahooNewsFragment.class.getSimpleName();

    private DTOCache.GetOrFetchTask<SecurityId, NewsList> fetchTask;
    @Inject protected Lazy<NewsCache> yahooNewsCache;
    private YahooNewsListView listView;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_yahoo_news, container, false);
        loadViews(view);
        return view;
    }

    private void loadViews(View view)
    {
        listView = (YahooNewsListView) view.findViewById(R.id.list_yahooNews);
        if (listView != null)
        {
            listView.setAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.yahoo_news_item);
        }
    }

    @Override public void onPause()
    {
        if (securityId != null)
        {
            yahooNewsCache.get().unRegisterListener(this);
        }

        if (fetchTask != null)
        {
            fetchTask.cancel(false);
        }
        fetchTask = null;
        super.onPause();
    }


    public void linkWith(SecurityId securityId, boolean andDisplay)
    {
        super.linkWith(securityId, andDisplay);
        if (this.securityId != null)
        {
            yahooNewsCache.get().registerListener(this);
            NewsList news = yahooNewsCache.get().get(this.securityId);
            if (news == null)
            {
                this.fetchTask = yahooNewsCache.get().getOrFetch(this.securityId, true, this); //force fetch - we know the value is not in cache
                this.fetchTask.execute();
            }
            else
            {
                linkWith(news, andDisplay);
            }
        }
    }

    @Override public void display()
    {
        displayYahooNewsListView();
    }

    public void displayYahooNewsListView()
    {
        if (listView != null)
        {
            listView.display(value);
        }
    }
}
