package com.tradehero.th.fragments.security;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.persistence.LiveDTOCache;
import com.tradehero.th.R;
import com.tradehero.th.api.PaginatedDTO;
import com.tradehero.th.api.PaginationDTO;
import com.tradehero.th.api.news.NewsHeadline;
import com.tradehero.th.api.news.NewsHeadlineList;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.news.NewsHeadlineAdapter;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.network.service.NewsServiceWrapper;
import com.tradehero.th.persistence.news.CommonNewsHeadlineCache;
import com.tradehero.th.persistence.news.NewsHeadlineCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.dagger.ForCertainSecurityNews;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

import javax.inject.Inject;

/**
 * Created by julien on 10/10/13
 * Display a ListView of News object for a given SecurityId - It uses the NewsHeadlineCache to get or fetch the news
 * from an abstract provider as needed. In case the news are not in the cache, the download is done in the background using the `fetchTask` AsyncTask.
 * The task is cancelled when the fragment is paused.
 */
public class NewsTitleListFragment extends AbstractSecurityInfoFragment<PaginatedDTO<NewsItemDTO>>
{
    private DTOCache.GetOrFetchTask<SecurityId, PaginatedDTO<NewsItemDTO>> fetchTask;
    @Inject @ForCertainSecurityNews
    protected CommonNewsHeadlineCache newsTitleCache;
    private ListView listView;
    private ProgressBar progressBar;
    private NewsHeadlineAdapter adapter;
    @Inject NewsServiceWrapper newsServiceWrapper;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
        Timber.d("NewsTitleListFragment onCreate");

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Timber.d("NewsTitleListFragment onActivityCreated");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Timber.d("NewsTitleListFragment onAttach");
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Timber.d("NewsTitleListFragment onCreateView");
        View view = inflater.inflate(R.layout.fragment_news_headline_list, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.d("NewsTitleListFragment onViewCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.d("NewsTitleListFragment onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Timber.d("NewsTitleListFragment onResume");
    }

    @Override
    public void onStop() {
        super.onStop();
        Timber.d("NewsTitleListFragment onStop");
    }

    @Override
    public void onPause() {
        super.onPause();
        Timber.d("NewsTitleListFragment onPause");
    }

    private void initViews(View view)
    {
        adapter = new NewsHeadlineAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.news_headline_item_view);

        listView = (ListView) view.findViewById(R.id.list_news_headline);
        progressBar = (ProgressBar) view.findViewById(R.id.list_news_headline_progressbar);
        showLoadingNews();
        if (listView != null)
        {
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
            {
                @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
                {

                    handleNewsClicked(position,(NewsItemDTO) adapterView.getItemAtPosition(position));

                }
            });
        }
    }

    private void showNewsList(){
        listView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void showLoadingNews(){
        listView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Timber.d("NewsTitleListFragment onDetach");
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
        Timber.d("NewsTitleListFragment onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Timber.d("NewsTitleListFragment onDestroy");
    }

    @Override LiveDTOCache<SecurityId, PaginatedDTO<NewsItemDTO>> getInfoCache()
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
                detachFetchTask();
                this.fetchTask = newsTitleCache.getOrFetch(this.securityId, true, this); //force fetch - we know the value is not in cache
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
    public void onErrorThrown(SecurityId key, Throwable error) {
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

    protected void handleNewsClicked(int position,NewsItemDTO news)
    {
        if (news != null)
        {
            int resId = adapter.getBackgroundRes(position);
            Navigator navigator = ((DashboardNavigatorActivity) getActivity()).getDashboardNavigator();
            Bundle bundle = news.toBundle(news.voteDirection==1);
            bundle.putInt(NewsDetailFragment.BUNDLE_KEY_TITLE_BACKGROUND_RES, resId);
            navigator.pushFragment(NewsDetailFragment.class, bundle);
        }
    }
}
