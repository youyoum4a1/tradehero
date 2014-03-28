package com.tradehero.th.fragments.news;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.*;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.th.R;
import com.tradehero.th.api.PaginatedDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.loaders.HeadlineListLoader;
import com.tradehero.th.network.service.NewsServiceWrapper;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by wangliang on 13-3-12.
 *
 * Headline news.
 */
public class HeadlineFragment extends DashboardFragment implements LoaderManager.LoaderCallbacks<List<NewsItemDTO>>{
    public static String TAG = "HeadlineFragment";

    private NewsHeadlineAdapter newsAdapter;
    //start from 0
    private int currentPage = -1;
    private ListView newsListView;
    private ProgressBar loadingProgressBar;

    @Inject
    NewsServiceWrapper newsServiceWrapper;


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setTitle(R.string.dashboard_headline);

        //inflater.inflate(R.menu.trending_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d(TAG+" onCreate");
        //newsListView.getSe
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Timber.d(TAG+" onCreateView");
        View view = inflater.inflate(R.layout.fragment_news_headline_list, container, false);
        initViews(view);

        restoreData(savedInstanceState);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initListener();
    }

    public static final String ARG_CURRENT_PAGE = "arg_page";
    public static final String ARG_HAS_MORE = "data_has_more";
    private boolean hasMore = true;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ARG_CURRENT_PAGE, currentPage);
        outState.putBoolean(ARG_HAS_MORE, hasMore);
        super.onSaveInstanceState(outState);
    }

    private void restoreData(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
               if (savedInstanceState.containsKey(ARG_CURRENT_PAGE)) {
                   currentPage = savedInstanceState.getInt(ARG_CURRENT_PAGE, currentPage);
               }
               if (savedInstanceState.containsKey(ARG_HAS_MORE)) {
                  hasMore = savedInstanceState.getBoolean(ARG_HAS_MORE);
               }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Timber.d(TAG+" onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        restoreData(savedInstanceState);
        showLoadingNews();
        fetchNewsList();
    }

    @Override
    public void onStart() {
        Timber.d(TAG+" onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Timber.d(TAG+" onResume");
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        Timber.d(TAG+" onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Timber.d(TAG+" onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Timber.d(TAG+" onDetach");
        super.onDetach();
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Loader<List<NewsItemDTO>> onCreateLoader(int id, Bundle args) {
        return new HeadlineListLoader(getActivity(),currentPage+1);
    }

    @Override
    public void onLoadFinished(Loader<List<NewsItemDTO>> loader, List<NewsItemDTO> data) {
        if (checkFragment() && newsAdapter != null) {
            notifyAndShowLoadResult(data);
            return;
        }
        Timber.e("onLoadFinished but activity is destroy or fragment is detached");
    }

    @Override
    public void onLoaderReset(Loader<List<NewsItemDTO>> loader) {
        if (checkFragment() && newsAdapter != null) {
            newsAdapter.setItems(null);
            newsAdapter.notifyDataSetChanged();
            return;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    private void initViews(View view) {
        ViewStub viewStub = (ViewStub)view.findViewById(R.id.trending_filter_selector_view);
        viewStub.inflate();
        this.newsListView = (ListView) view.findViewById(R.id.list_news_headline);
        this.loadingProgressBar = (ProgressBar) view.findViewById(R.id.list_news_headline_progressbar);
    }


    private int getLoaderId() {
        return 1000;
    }

    private void initListener() {
        if (this.newsAdapter == null) {
            this.newsAdapter = new NewsHeadlineAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.news_headline_item_view);
        }
        if (newsListView.getAdapter() == null) {
            newsListView.setAdapter(newsAdapter);
        }
        newsListView.setOnScrollListener(onScrollListener);

        AdapterView.OnItemClickListener listener = newsListView.getOnItemClickListener();
        if (listener == null) {
            newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    handleNewsClicked(position,(NewsItemDTO) adapterView.getItemAtPosition(position));

                }
            });
        }
    }

    private void fetchNewsList() {
        //HeadlineListLoader
        //newsServiceWrapper.getGlobalNews(1,42,newsLoadCallback);
        createAndGetLoader();
    }

    private HeadlineListLoader createAndGetLoader() {
        Loader existingLoader = getLoaderManager().getLoader(getLoaderId());
       if (existingLoader != null) {
           HeadlineListLoader loader = (HeadlineListLoader)existingLoader;
           return  loader;
       }
       HeadlineListLoader loader = (HeadlineListLoader)getLoaderManager().initLoader(getLoaderId(),null,this);
       return loader;
    }



    private boolean checkFragment() {
        Activity activity = getActivity();
        if (activity == null || activity.isFinishing()) {
            return false;
        }
        if (this.isDetached()) {
            return false;
        }

        return  true;
    }


    private void fillData(List<NewsItemDTO> data) {

    }

    private void notifyAndShowLoadResult(PaginatedDTO<NewsItemDTO> PaginatedData) {
        List<NewsItemDTO> data = PaginatedData.getData();
        notifyAndShowLoadResult(data);
    }

    private void notifyAndShowLoadResult(List<NewsItemDTO> data) {
        if (data != null){
            if (newsAdapter != null) {
                newsAdapter.addItems(data);
                newsAdapter.notifyDataSetChanged();
            }
            currentPage++;
        } else {
            //TODO
            //no more
        }
        showNewsList();
    }

    private void handleLastItemVisible(){
        HeadlineListLoader loader = createAndGetLoader();
        loader.loadNext();
    }


    AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {

        boolean mLastItemVisible;
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            int lastVisiblePosition = view.getLastVisiblePosition();
            Timber.d("%s onScrollStateChanged lastVisiblePosition %d",TAG,lastVisiblePosition);
            int height = view.getHeight();
            try {
                int childCount = view.getChildCount();
                View child = view.getChildAt(childCount-1);
                Timber.d("%s onScrollStateChanged lastVisiblePosition %d,child bottom %d,list height %d",TAG,lastVisiblePosition,child.getBottom(),height);
                if(child != null && child.getBottom() >= height){
                    /**
                     * Check that the scrolling has stopped, and that the last item is
                     * visible.
                     */
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE &&  mLastItemVisible) {
                        handleLastItemVisible();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            mLastItemVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount - 1);

        }
    };

    Callback<PaginatedDTO<NewsItemDTO>> newsLoadCallback = new Callback<PaginatedDTO<NewsItemDTO>>() {

        @Override
        public void success(PaginatedDTO<NewsItemDTO> newsItemDTOPaginatedDTO, Response response) {
            if (checkFragment()) {
                Timber.d(TAG+" newsLoadCallback success data size:%d",newsItemDTOPaginatedDTO.getData().size());
                notifyAndShowLoadResult(newsItemDTOPaginatedDTO);
                return;
            }
            Timber.e("newsLoadCallback failure but activity is destroy or fragment is detached");
        }

        @Override
        public void failure(RetrofitError error) {
            Timber.e(error,TAG+" newsLoadCallback failure");
            if (checkFragment()) {
                return;
            }


        }
    };

    private void showNewsList(){
        newsListView.setVisibility(View.VISIBLE);
        loadingProgressBar.setVisibility(View.GONE);
    }

    private void showLoadingNews(){
        newsListView.setVisibility(View.GONE);
        loadingProgressBar.setVisibility(View.VISIBLE);
    }

    private void handleNewsClicked(int position,NewsItemDTO data) {

    }

    @Override
    public boolean isTabBarVisible() {
        return true;
    }


}
