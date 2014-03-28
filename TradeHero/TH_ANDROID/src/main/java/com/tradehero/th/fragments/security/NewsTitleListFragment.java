package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.persistence.LiveDTOCache;
import com.tradehero.th.R;
import com.tradehero.th.api.PaginatedDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.news.NewsItemDTOKey;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.news.NewsHeadlineAdapter;
import com.tradehero.th.network.service.NewsServiceWrapper;
import com.tradehero.th.persistence.news.SecurityNewsCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

/**
 * Created by julien on 10/10/13 Display a ListView of News object for a given SecurityId - It uses
 * the NewsHeadlineCache to get or fetch the news from an abstract provider as needed. In case the
 * news are not in the cache, the download is done in the background using the `fetchTask`
 * AsyncTask. The task is cancelled when the fragment is paused.
 */
public class NewsTitleListFragment extends AbstractSecurityInfoFragment<PaginatedDTO<NewsItemDTO>>
{
    @Inject SecurityNewsCache newsTitleCache;
    @Inject NewsServiceWrapper newsServiceWrapper;

    @InjectView(R.id.list_news_headline) ListView listView;
    @InjectView(R.id.list_news_headline_progressbar) ProgressBar progressBar;

    private DTOCache.GetOrFetchTask<SecurityId, PaginatedDTO<NewsItemDTO>> fetchTask;
    private NewsHeadlineAdapter adapter;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_news_headline_list, container, false);

        ButterKnife.inject(this, view);
        initViews(view);
        return view;
    }

    private void initViews(View view)
    {
        adapter = new NewsHeadlineAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.news_headline_item_view);

        showLoadingNews();
        if (listView != null)
        {
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                {
                    Object o = adapterView.getItemAtPosition(position);
                    if (o instanceof NewsItemDTO)
                    {
                        handleNewsClicked(position, (NewsItemDTO) o);
                    }
                }
            });
        }
    }

    private void showNewsList()
    {
        listView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void showLoadingNews()
    {
        listView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
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

    @Override LiveDTOCache<SecurityId, PaginatedDTO<NewsItemDTO>> getInfoCache()
    {
        return newsTitleCache;
    }

    protected void detachFetchTask()
    {
        if (fetchTask != null)
        {
            fetchTask.setListener(null);
        }
        fetchTask = null;
    }

    @Override
    public void linkWith(SecurityId securityId, boolean andDisplay)
    {
        super.linkWith(securityId, andDisplay);
        if (this.securityId != null)
        {
            //NewsHeadlineList news = newsTitleCache.get(this.securityId);
            PaginatedDTO<NewsItemDTO> news = newsTitleCache.get(this.securityId);
            if (news == null)
            {
                //force fetch - we know the value is not in cache
                detachFetchTask();
                this.fetchTask = newsTitleCache.getOrFetch(this.securityId, true, this);
                this.fetchTask.execute();
            }
            else
            {   //already cached
                linkWith(news, andDisplay);
                showNewsList();
            }
        }
    }

    @Override public void display()
    {
        displayNewsListView();
        showNewsList();
    }

    @Override
    public void onErrorThrown(SecurityId key, Throwable error)
    {
        super.onErrorThrown(key, error);
        showNewsList();
    }

    public void displayNewsListView()
    {
        if (!isDetached() && adapter != null)
        {
            adapter.setItems(value.getData());
            adapter.notifyDataSetChanged();
        }
    }

    protected void handleNewsClicked(int position, NewsItemDTO news)
    {
        if (news != null)
        {
            int resId = adapter.getBackgroundRes(position);
            NewsItemDTOKey newsItemDTOKey = news.getDiscussionKey();
            Bundle bundle = newsItemDTOKey.getArgs();
            bundle.putInt(NewsDetailFragment.BUNDLE_KEY_TITLE_BACKGROUND_RES, resId);
            getNavigator().pushFragment(NewsDetailFragment.class, bundle);
        }
    }

    private Navigator getNavigator()
    {
        return ((DashboardNavigatorActivity) getActivity()).getDashboardNavigator();
    }
}
