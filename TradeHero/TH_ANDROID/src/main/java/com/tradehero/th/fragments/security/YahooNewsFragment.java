package com.tradehero.th.fragments.security;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.tradehero.th.R;
import com.tradehero.th.adapters.YahooNewsAdapter;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.yahoo.NewsList;
import com.tradehero.th.persistence.yahoo.NewsCache;
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

    private AsyncTask<Void, Void, NewsList> fetchTask;

    @Inject protected Lazy<NewsCache> yahooNewsCache;

    private ListView listView;
    private YahooNewsAdapter adapter;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_yahoo_news, container, false);
        loadViews(view);
        return view;
    }

    private void loadViews(View view)
    {
        listView = (ListView) view.findViewById(R.id.list_yahooNews);
        if (listView != null)
        {
            adapter = new YahooNewsAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.yahoo_news_item);
            listView.setAdapter(adapter);
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
        adapter.setItems(value);
        getView().post(new Runnable()
        {
            @Override public void run()
            {
                adapter.notifyDataSetChanged();
            }
        });
    }
}
