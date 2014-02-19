package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.R;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.yahoo.News;
import com.tradehero.th.api.yahoo.NewsList;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.web.WebViewFragment;
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
    private ListView listView;
    private YahooNewsAdapter adapter;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_yahoo_news, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view)
    {
        adapter = new YahooNewsAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.yahoo_news_item);

        listView = (ListView) view.findViewById(R.id.list_yahooNews);
        if (listView != null)
        {
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                {
                    handleNewsClicked((News) adapterView.getItemAtPosition(position));
                }
            });
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

    @Override public void onDestroyView()
    {
        if (listView != null)
        {
            listView.setOnItemClickListener(null);
        }
        listView = null;
        adapter = null;
        super.onDestroyView();
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
        if (adapter != null)
        {
            adapter.setItems(value);
            adapter.notifyDataSetChanged();
        }
    }

    protected void handleNewsClicked(News news)
    {
        if (news != null && news.getUrl() != null)
        {
            Navigator navigator = ((DashboardNavigatorActivity) getActivity()).getDashboardNavigator();
            Bundle bundle = new Bundle();
            bundle.putString(WebViewFragment.BUNDLE_KEY_URL, news.getUrl());
            navigator.pushFragment(WebViewFragment.class, bundle);
        }
    }
}
