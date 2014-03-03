package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.persistence.LiveDTOCache;
import com.tradehero.th.R;
import com.tradehero.th.api.news.NewsHeadline;
import com.tradehero.th.api.news.NewsHeadlineList;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.news.NewsHeadlineAdapter;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.persistence.news.NewsHeadlineCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

/**
 * Created by julien on 10/10/13
 * Display a ListView of News object for a given SecurityId - It uses the NewsHeadlineCache to get or fetch the news
 * from an abstract provider as needed. In case the news are not in the cache, the download is done in the background using the `fetchTask` AsyncTask.
 * The task is cancelled when the fragment is paused.
 */
public class NewsTitleListFragment extends AbstractSecurityInfoFragment<NewsHeadlineList>
{
    private final static String TAG = NewsTitleListFragment.class.getSimpleName();

    private DTOCache.GetOrFetchTask<SecurityId, NewsHeadlineList> fetchTask;
    @Inject protected NewsHeadlineCache newsTitleCache;
    private ListView listView;
    private NewsHeadlineAdapter adapter;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_news_headline_list, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view)
    {
        adapter = new NewsHeadlineAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.news_headline_item_view);

        listView = (ListView) view.findViewById(R.id.list_news_headline);
        if (listView != null)
        {
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                {
                    handleNewsClicked((NewsHeadline) adapterView.getItemAtPosition(position));
                }
            });
        }
    }

    @Override public void onDestroyView()
    {
        detachFetchTask();
        if (listView != null)
        {
            listView.setOnItemClickListener(null);
        }
        listView = null;
        adapter = null;
        super.onDestroyView();
    }

    @Override LiveDTOCache<SecurityId, NewsHeadlineList> getInfoCache()
    {
        return newsTitleCache;
    }

    protected void detachFetchTask()
    {
        if (fetchTask != null)
        {
            fetchTask.cancel(false);
        }
        fetchTask = null;
    }

    public void linkWith(SecurityId securityId, boolean andDisplay)
    {
        super.linkWith(securityId, andDisplay);
        if (this.securityId != null)
        {
            NewsHeadlineList news = newsTitleCache.get(this.securityId);
            if (news == null)
            {
                detachFetchTask();
                this.fetchTask = newsTitleCache.getOrFetch(this.securityId, true, this); //force fetch - we know the value is not in cache
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
        displayNewsListView();
    }

    public void displayNewsListView()
    {
        if (!isDetached() && adapter != null)
        {
            adapter.setItems(value);
            adapter.notifyDataSetChanged();
        }
    }

    protected void handleNewsClicked(NewsHeadline news)
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
