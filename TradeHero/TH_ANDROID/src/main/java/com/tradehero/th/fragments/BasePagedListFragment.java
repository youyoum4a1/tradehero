package com.tradehero.th.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.tradehero.common.api.PagedDTOKey;
import com.tradehero.common.persistence.ContainerDTO;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.DTOKey;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.FlagNearEdgeScrollListener;
import com.tradehero.th.R;
import com.tradehero.th.adapters.PagedArrayDTOAdapterNew;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.utils.route.THRouter;
import com.tradehero.th.widget.MultiScrollListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import android.support.annotation.NonNull;
import timber.log.Timber;

abstract public class BasePagedListFragment<
        PagedDTOKeyType extends DTOKey, // But it also needs to be a PagedDTOKey
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

    @Inject protected Analytics analytics;
    @Inject protected THRouter thRouter;

    @InjectView(R.id.search_empty_container) protected View emptyContainer;
    @InjectView(R.id.listview) protected ListView listView;
    @InjectView(R.id.progress) protected ProgressBar mProgress;

    protected int perPage = DEFAULT_PER_PAGE;
    protected FlagNearEdgeScrollListener nearEndScrollListener;

    protected PagedArrayDTOAdapterNew<DTOType, ViewType> itemViewAdapter;
    protected Map<Integer, DTOListType> pagedDtos;
    protected Map<Integer, DTOCacheNew.Listener<PagedDTOKeyType, ContainerDTOType>> dtoListeners;
    protected DTOType selectedItem;

    protected Runnable requestDataTask;

    public static void putPerPage(Bundle args, int perPage)
    {
        args.putInt(BUNDLE_KEY_PER_PAGE, perPage);
    }

    public static int getPerPage(Bundle args)
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
        dtoListeners = new HashMap<>();
        perPage = getPerPage(getArguments());
        perPage = getPerPage(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(getFragmentLayoutResId(), container, false);
        initViews(view);
        return view;
    }

    abstract protected int getFragmentLayoutResId();

    protected void initViews(View view)
    {
        ButterKnife.inject(this, view);
        nearEndScrollListener = createFlagNearEdgeScrollListener();

        if (listView != null)
        {
            listView.setOnScrollListener(new MultiScrollListener(nearEndScrollListener, dashboardBottomTabsListViewScrollListener.get()));
            listView.setEmptyView(emptyContainer);
            listView.setAdapter(itemViewAdapter);
        }
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
        detachDtoListCache();

        if (listView != null)
        {
            listView.setOnScrollListener(null);
        }
        listView = null;
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
        detachDtoListCache();
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

    abstract protected PagedArrayDTOAdapterNew<DTOType, ViewType> createItemViewAdapter();

    protected void loadAdapterWithAvailableData()
    {
        if (itemViewAdapter == null)
        {
            itemViewAdapter = createItemViewAdapter();
            listView.setAdapter(itemViewAdapter);
        }

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
        return  firstPage == null || firstPage.size() == 0;
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
        return dtoListeners.size() > 0;
    }

    protected boolean isRequesting(int page)
    {
        return dtoListeners.containsKey(page);
    }

    abstract protected void unregisterCache(DTOCacheNew.Listener<PagedDTOKeyType, ContainerDTOType> listener);
    abstract protected void registerCache(PagedDTOKeyType key, DTOCacheNew.Listener<PagedDTOKeyType, ContainerDTOType> listener);
    abstract protected void requestCache(PagedDTOKeyType key);

    protected void detachDtoListCache()
    {
        for (DTOCacheNew.Listener<PagedDTOKeyType, ContainerDTOType> listener : dtoListeners.values())
        {
            unregisterCache(listener);
        }
        dtoListeners.clear();
    }

    protected void detachDtoListCache(int page)
    {
        DTOCacheNew.Listener<PagedDTOKeyType, ContainerDTOType> listener = dtoListeners.get(page);
        if (listener != null)
        {
            unregisterCache(listener);
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
            PagedDTOKeyType pagedKey = makePagedDtoKey(pageToLoad);
            detachDtoListCache(pageToLoad);
            DTOCacheNew.Listener<PagedDTOKeyType, ContainerDTOType> listener = createListCacheListener();
            registerCache(pagedKey, listener);
            dtoListeners.put(pageToLoad, listener);
            requestCache(pagedKey);
        }
        updateVisibilities();
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

    protected DTOCacheNew.Listener<PagedDTOKeyType, ContainerDTOType> createListCacheListener()
    {
        return new ListCacheListener();
    }

    protected class ListCacheListener
            implements DTOCacheNew.Listener<PagedDTOKeyType, ContainerDTOType>
    {
        @Override
        public void onDTOReceived(@NonNull PagedDTOKeyType key, @NonNull ContainerDTOType value)
        {
            PagedDTOKey properKey = (PagedDTOKey) key;
            Timber.d("Page loaded: %d", properKey.getPage());
            pagedDtos.put(properKey.getPage(), value.getList());
            dtoListeners.remove(properKey.getPage());

            loadAdapterWithAvailableData();

            nearEndScrollListener.lowerEndFlag();
            if (value.size() == 0)
            {
                nearEndScrollListener.deactivateEnd();
                if (properKey.getPage() == FIRST_PAGE)
                {
                    itemViewAdapter.clear();
                }
            }
            else if (canMakePagedDtoKey())
            {
                // Prefetch next
                requestCache(makePagedDtoKey(((PagedDTOKey) key).getPage() + 1));
            }
        }

        @Override public void onErrorThrown(@NonNull PagedDTOKeyType key, @NonNull Throwable error)
        {
            PagedDTOKey properKey = (PagedDTOKey) key;
            dtoListeners.remove(properKey.getPage());
            nearEndScrollListener.lowerEndFlag();
            THToast.show(getString(R.string.error_fetch_people_list_info));
            Timber.e("Error fetching the list of securities " + key, error);
        }
    }
}