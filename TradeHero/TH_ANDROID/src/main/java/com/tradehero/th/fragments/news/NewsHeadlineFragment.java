package com.tradehero.th.fragments.news;

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
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.discussion.NewsDiscussionFragment;
import com.tradehero.th.fragments.security.AbstractSecurityInfoFragment;
import com.tradehero.th.persistence.news.SecurityNewsCache;
import com.tradehero.th.utils.DaggerUtils;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by julien on 10/10/13 Display a ListView of News object for a given SecurityId - It uses
 * the NewsHeadlineCache to get or fetch the news from an abstract provider as needed. In case the
 * news are not in the cache, the download is done in the background using the `fetchNewsTask`
 * AsyncTask. The task is cancelled when the fragment is paused.
 */
public class NewsHeadlineFragment extends AbstractSecurityInfoFragment<PaginatedDTO<NewsItemDTO>>
{
    @Inject SecurityNewsCache newsTitleCache;

    @InjectView(R.id.list_news_headline_wrapper) BetterViewAnimator listViewWrapper;
    @InjectView(R.id.list_news_headline) ListView listView;
    @InjectView(R.id.list_news_headline_progressbar) ProgressBar progressBar;

    private DTOCache.GetOrFetchTask<SecurityId, PaginatedDTO<NewsItemDTO>> fetchNewsTask;
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
        listViewWrapper.setDisplayedChildByLayoutId(listView.getId());
    }

    private void showLoadingNews()
    {
        listViewWrapper.setDisplayedChildByLayoutId(progressBar.getId());
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

    @Override protected LiveDTOCache<SecurityId, PaginatedDTO<NewsItemDTO>> getInfoCache()
    {
        return newsTitleCache;
    }

    @Override public void linkWith(SecurityId securityId, boolean andDisplay)
    {
        super.linkWith(securityId, andDisplay);

        if (this.securityId != null)
        {
            fetchSecurityNews();
        }
    }

    protected void detachFetchTask()
    {
        if (fetchNewsTask != null)
        {
            fetchNewsTask.setListener(null);
        }
        fetchNewsTask = null;
    }

    private void fetchSecurityNews()
    {
        detachFetchTask();

        fetchNewsTask = newsTitleCache.getOrFetch(securityId, true, this);
        fetchNewsTask.execute();
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
            List<NewsItemDTO> data = value.getData();
            List<NewsItemDTOKey> newsItemDTOKeyList = new ArrayList<>();

            if (data != null)
            {
                for (NewsItemDTO newsItemDTO: data)
                {
                    newsItemDTOKeyList.add(newsItemDTO.getDiscussionKey());
                }
            }

            adapter.setItems(newsItemDTOKeyList);
            adapter.notifyDataSetChanged();
        }
    }

    protected void handleNewsClicked(int position, NewsItemDTO news)
    {
        if (news != null)
        {
            int resId = adapter.getBackgroundRes(position);
            NewsItemDTOKey newsItemDTOKey = news.getDiscussionKey();
            Bundle bundle = new Bundle();
            bundle.putInt(NewsDiscussionFragment.BUNDLE_KEY_TITLE_BACKGROUND_RES, resId);
            bundle.putBundle(NewsDiscussionFragment.DISCUSSION_KEY_BUNDLE_KEY, newsItemDTOKey.getArgs());
            getNavigator().pushFragment(NewsDiscussionFragment.class, bundle);
        }
    }

    private Navigator getNavigator()
    {
        return ((DashboardNavigatorActivity) getActivity()).getDashboardNavigator();
    }
}
