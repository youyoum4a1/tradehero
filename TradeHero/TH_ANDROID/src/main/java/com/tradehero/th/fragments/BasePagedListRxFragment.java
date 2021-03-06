package com.tradehero.th.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.tradehero.common.api.PagedDTOKey;
import com.tradehero.common.persistence.ContainerDTO;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.common.widget.FlagNearEdgeScrollListener;
import com.tradehero.th.R;
import com.tradehero.th.adapters.PagedArrayDTOAdapterNew;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.widget.MultiScrollListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import timber.log.Timber;

abstract public class BasePagedListRxFragment<
        PagedDTOKeyType extends PagedDTOKey,
        DTOType extends DTO,
        DTOListType extends DTO & List<DTOType>,
        ContainerDTOType extends DTO & ContainerDTO<DTOType, DTOListType>,
        ViewType extends View & DTOView<DTOType>>
        extends BasePurchaseManagerFragment
{
    private final static String BUNDLE_KEY_PER_PAGE = BasePagedListFragment.class.getName() + ".perPage";

    public final static int FIRST_PAGE = 1;
    public final static int DEFAULT_PER_PAGE = 15;
    public final static long DELAY_REQUEST_DATA_MILLI_SEC = 1000;

    @InjectView(R.id.search_empty_container) protected View emptyContainer;
    @InjectView(R.id.listview) protected AbsListView listView;
    @InjectView(R.id.progress) protected ProgressBar mProgress;

    protected int perPage = DEFAULT_PER_PAGE;
    protected FlagNearEdgeScrollListener nearEndScrollListener;

    protected PagedArrayDTOAdapterNew<DTOType, ViewType> itemViewAdapter;
    protected Map<Integer, DTOListType> pagedDtos;
    @NonNull protected Map<Integer, Subscription> pagedSubscriptions;
    protected DTOType selectedItem;

    protected Runnable requestDataTask;

    public static void putPerPage(@NonNull Bundle args, int perPage)
    {
        args.putInt(BUNDLE_KEY_PER_PAGE, perPage);
    }

    public static int getPerPage(@Nullable Bundle args)
    {
        if (args != null && args.containsKey(BUNDLE_KEY_PER_PAGE))
        {
            return args.getInt(BUNDLE_KEY_PER_PAGE);
        }
        return DEFAULT_PER_PAGE;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        pagedDtos = new HashMap<>();
        pagedSubscriptions = new HashMap<>();
        perPage = getPerPage(getArguments());
        perPage = getPerPage(savedInstanceState);
        itemViewAdapter = createItemViewAdapter();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(getFragmentLayoutResId(), container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        nearEndScrollListener = createFlagNearEdgeScrollListener();
        listView.setOnScrollListener(createListViewScrollListener());
        listView.setEmptyView(emptyContainer);
        listView.setAdapter(itemViewAdapter);
    }

    @NonNull protected AbsListView.OnScrollListener createListViewScrollListener()
    {
        return new MultiScrollListener(nearEndScrollListener, dashboardBottomTabsListViewScrollListener.get());
    }

    abstract protected int getFragmentLayoutResId();

    protected void initViews(View view)
    {
    }

    @Override public void onResume()
    {
        super.onResume();
        loadAdapterWithAvailableData();
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        putPerPage(outState, perPage);
    }

    @Override public void onDestroyView()
    {
        unsubscribeListCache();

        listView.setOnScrollListener(null);
        nearEndScrollListener = null;

        View rootView = getView();
        if (rootView != null && requestDataTask != null)
        {
            rootView.removeCallbacks(requestDataTask);
        }

        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        itemViewAdapter = null;
        super.onDestroy();
    }

    protected void startAnew()
    {
        unsubscribeListCache();
        this.pagedDtos.clear();
        if (nearEndScrollListener != null)
        {
            nearEndScrollListener.lowerEndFlag();
            nearEndScrollListener.activateEnd();
        }
        if (itemViewAdapter != null)
        {
            itemViewAdapter.clear();
            itemViewAdapter.notifyDataSetChanged();
        }
        updateVisibilities();
    }

    @NonNull abstract protected PagedArrayDTOAdapterNew<DTOType, ViewType> createItemViewAdapter();

    protected void loadAdapterWithAvailableData()
    {
        Integer lastPageInAdapter = itemViewAdapter.getLastPageLoaded();
        if ((lastPageInAdapter == null && pagedDtos.containsKey(FIRST_PAGE)) ||
                lastPageInAdapter != null)
        {
            if (lastPageInAdapter == null)
            {
                lastPageInAdapter = FIRST_PAGE - 1;
            }

            while (pagedDtos.containsKey(++lastPageInAdapter))
            {
                itemViewAdapter.addPage(lastPageInAdapter, pagedDtos.get(lastPageInAdapter));
            }
            itemViewAdapter.notifyDataSetChanged();
        }
        updateVisibilities();
    }

    protected Integer getNextPageToRequest()
    {
        Integer potential = FIRST_PAGE;
        while (isBeingHandled(potential) && !isLast(potential))
        {
            potential++;
        }
        if (isLast(potential))
        {
            potential = null;
        }
        return potential;
    }

    protected boolean hasEmptyResult()
    {
        if (!pagedDtos.containsKey(FIRST_PAGE))
        {
            return false;
        }
        List<DTOType> firstPage = pagedDtos.get(FIRST_PAGE);
        return firstPage == null || firstPage.size() == 0;
    }

    protected boolean isBeingHandled(int page)
    {
        return hasData(page) || isRequesting(page);
    }

    protected boolean hasData(int page)
    {
        return pagedDtos.containsKey(page);
    }

    protected boolean isLast(int page)
    {
        return hasData(page) && pagedDtos.get(page) == null;
    }

    protected boolean isRequesting()
    {
        return pagedSubscriptions.size() > 0;
    }

    protected boolean isRequesting(int page)
    {
        return pagedSubscriptions.containsKey(page);
    }

    @NonNull abstract protected DTOCacheRx<PagedDTOKeyType, ContainerDTOType> getCache();

    protected void unsubscribeListCache()
    {
        for (Integer page : pagedSubscriptions.keySet())
        {
            unsubscribeListCache(page);
        }
        pagedSubscriptions.clear();
    }

    protected void unsubscribeListCache(int page)
    {
        Subscription subscription = pagedSubscriptions.get(page);
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
    }

    protected void scheduleRequestData()
    {
        View view = getView();
        if (view != null)
        {
            if (requestDataTask != null)
            {
                view.removeCallbacks(requestDataTask);
            }

            requestDataTask = new Runnable()
            {
                @Override public void run()
                {
                    startAnew();
                    requestDtos();
                }
            };
            view.postDelayed(requestDataTask, DELAY_REQUEST_DATA_MILLI_SEC);
        }
    }

    protected void requestDtos()
    {
        Integer pageToLoad = getNextPageToRequest();
        if (pageToLoad != null && canMakePagedDtoKey())
        {
            requestPage(pageToLoad);
        }
        updateVisibilities();
    }

    protected void requestPage(int pageToLoad)
    {
        PagedDTOKeyType pagedKey = makePagedDtoKey(pageToLoad);
        unsubscribeListCache(pageToLoad);
        pagedSubscriptions.put(
                pageToLoad,
                AndroidObservable.bindFragment(
                        this,
                        getCache().get(pagedKey))
                        .subscribe(createListCacheObserver(pagedKey)));
    }

    abstract public boolean canMakePagedDtoKey();
    @NonNull abstract public PagedDTOKeyType makePagedDtoKey(int page);

    protected void updateVisibilities()
    {
        mProgress.setVisibility(isRequesting() ? View.VISIBLE : View.INVISIBLE);

        boolean hasItems = (itemViewAdapter != null) && (itemViewAdapter.getCount() > 0);
        emptyContainer.setVisibility(hasItems ? View.GONE : View.VISIBLE);
    }

    protected FlagNearEdgeScrollListener createFlagNearEdgeScrollListener()
    {
        return new ListFlagNearEdgeScrollListener();
    }

    protected class ListFlagNearEdgeScrollListener extends FlagNearEdgeScrollListener
    {
        @Override public void raiseEndFlag()
        {
            super.raiseEndFlag();
            requestDtos();
        }
    }

    @OnItemClick(R.id.listview)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        //noinspection unchecked
        handleDtoClicked((DTOType) parent.getItemAtPosition(position));
    }

    protected void handleDtoClicked(DTOType clicked)
    {
        this.selectedItem = clicked;
    }

    @NonNull protected Observer<Pair<PagedDTOKeyType, ContainerDTOType>> createListCacheObserver(@NonNull PagedDTOKeyType key)
    {
        return new ListCacheObserver(key);
    }

    protected class ListCacheObserver
            implements Observer<Pair<PagedDTOKeyType, ContainerDTOType>>
    {
        @NonNull protected final PagedDTOKeyType key;

        protected ListCacheObserver(@NonNull PagedDTOKeyType key)
        {
            this.key = key;
        }

        @Override public void onNext(Pair<PagedDTOKeyType, ContainerDTOType> pair)
        {
            Timber.d("Page loaded: %d", key.getPage());
            BasePagedListRxFragment.this.onNext(pair.first, pair.second);
        }

        @Override public void onCompleted()
        {
            pagedSubscriptions.remove(key.getPage());
        }

        @Override public void onError(Throwable error)
        {
            pagedSubscriptions.remove(key.getPage());
            nearEndScrollListener.lowerEndFlag();
        }
    }

    protected void onNext(PagedDTOKeyType key, ContainerDTOType value)
    {
        pagedDtos.put(key.getPage(), value.getList());
        pagedSubscriptions.remove(key.getPage());

        loadAdapterWithAvailableData();

        nearEndScrollListener.lowerEndFlag();
        if (value.size() == 0)
        {
            nearEndScrollListener.deactivateEnd();
            if (key.getPage() == FIRST_PAGE)
            {
                itemViewAdapter.clear();
            }
        }
    }
}
