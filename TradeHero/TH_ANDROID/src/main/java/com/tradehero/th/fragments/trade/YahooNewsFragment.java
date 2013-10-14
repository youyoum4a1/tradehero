package com.tradehero.th.fragments.trade;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockFragment;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.R;
import com.tradehero.th.adapters.YahooNewsAdapter;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.api.yahoo.News;
import com.tradehero.th.persistence.yahoo.NewsCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by julien on 10/10/13
 * Display a ListView of Yahoo News object for a given SecurityId - It uses the NewsCache to get or fetch the Yahoo news
 * as needed. In case the news are not in the cache, the download is done in the background using the `fetchTask` AsyncTask.
 * The task is cancelled when the fragment is paused.
 */
public class YahooNewsFragment extends SherlockFragment implements DTOCache.Listener<SecurityId, List<News>>
{
    private final static String TAG = YahooNewsFragment.class.getSimpleName();

    private SecurityId securityId;
    private AsyncTask<Void, Void, List<News>> fetchTask;
    private List<News> news;


    @Inject protected Lazy<NewsCache> yahooNewsCache;

    private ListView listView;
    private YahooNewsAdapter adapter;


    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_yahoo_news, container, false);
        loadViews(view);
        return view;
    }

    private void loadViews(View view)
    {
        listView = (ListView)view.findViewById(R.id.list_yahooNews);
        if (listView != null)
        {
            adapter = new YahooNewsAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.yahoo_news_item);
            listView.setAdapter(adapter);
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        Bundle args = getArguments();
        if (args != null)
        {
            linkWith(new SecurityId(args), true);
        }
        else
        {
            updateAdapter();
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
            fetchTask = null;
        }
        super.onPause();
    }


    public void linkWith(SecurityId securityId, boolean andDisplay)
    {
        this.securityId = securityId;
        if (this.securityId!= null)
        {
            yahooNewsCache.get().registerListener(this);
            List<News> news = yahooNewsCache.get().get(this.securityId);
            if (news == null)
            {
                this.fetchTask = yahooNewsCache.get().getOrFetch(this.securityId, true, this);//force fetch - we know the value is not in cache
                this.fetchTask.execute();
            }
            else
            {
                linkWith(news, andDisplay);
            }


        }
    }

    @Override public void onDTOReceived(SecurityId key, List<News> value)
    {
        if (key.equals(securityId))
        {
            linkWith(value, true);
        }
    }

    public void linkWith(List<News> news, boolean andDisplay)
    {
        this.news = news;
        if (andDisplay)
        {
            updateAdapter();
        }
    }

    private void updateAdapter()
    {
        adapter.setItems(news);
        adapter.notifyDataSetChanged();
    }
}
