package com.androidth.general.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnItemClick;
import com.androidth.general.common.api.PagedDTOKey;
import com.androidth.general.common.persistence.ContainerDTO;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.common.persistence.DTOCacheRx;
import com.androidth.general.common.widget.FlagNearEdgeScrollListener;
import com.androidth.general.R;
import com.androidth.general.adapters.PagedDTOAdapter;
import com.androidth.general.fragments.billing.BasePurchaseManagerFragment;
import com.androidth.general.widget.MultiScrollListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

abstract public class BasePagedListRxFragment<
        PagedDTOKeyType extends PagedDTOKey,
        DTOType extends DTO,
        DTOListType extends DTO & List<DTOType>,
        ContainerDTOType extends DTO & ContainerDTO<DTOType, DTOListType>>
        extends BasePurchaseManagerFragment
{
    private final static String BUNDLE_KEY_PER_PAGE = BasePagedListRxFragment.class.getName() + ".perPage";

    public final static int FIRST_PAGE = 1;
    public final static int DEFAULT_PER_PAGE = 15;

    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    @Bind(R.id.search_empty_container) protected View emptyContainer;
    @Bind(R.id.listview) protected AbsListView listView;
    @Bind(R.id.progress) protected ProgressBar mProgress;

    protected int perPage = DEFAULT_PER_PAGE;
    protected FlagNearEdgeScrollListener nearEndScrollListener;

    protected PagedDTOAdapter<DTOType> itemViewAdapter;
    @NonNull protected final Map<Integer, Subscription> pagedSubscriptions;
    @NonNull protected final Map<Integer, Subscription> pagedPastSubscriptions;
    protected DTOType selectedItem;

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

    public BasePagedListRxFragment()
    {
        pagedSubscriptions = new HashMap<>();
        pagedPastSubscriptions = new HashMap<>();
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        perPage = getPerPage(getArguments());
        perPage = getPerPage(savedInstanceState);
        itemViewAdapter = createItemViewAdapter();
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        nearEndScrollListener = createFlagNearEdgeScrollListener();
        listView.setOnScrollListener(createListViewScrollListener());
        listView.setEmptyView(emptyContainer);
        listView.setAdapter(itemViewAdapter);
    }

    @NonNull protected AbsListView.OnScrollListener createListViewScrollListener()
    {
        return new MultiScrollListener(nearEndScrollListener, fragmentElements.get().getListViewScrollListener());
    }

    @Override public void onViewStateRestored(@Nullable Bundle savedInstanceState)
    {
        super.onViewStateRestored(savedInstanceState);
        if (itemViewAdapter == null)
        {
            itemViewAdapter = createItemViewAdapter();
        }
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
        ButterKnife.unbind(this);
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
        if (nearEndScrollListener != null)
        {
            nearEndScrollListener.lowerEndFlag();
            nearEndScrollListener.activateEnd();
        }
        itemViewAdapter.clear();
        updateVisibilities();
    }

    @NonNull abstract protected PagedDTOAdapter<DTOType> createItemViewAdapter();

    @Nullable protected Integer getNextPageToRequest()
    {
        Integer potential = FIRST_PAGE;
        while (isRequesting(potential))
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
        return itemViewAdapter.getCount() == 0;
    }

    protected boolean isLast(int page)
    {
        Integer latestPage = itemViewAdapter.getLatestPage();
        if (latestPage == null || !latestPage.equals(page))
        {
            return false;
        }
        List<DTOType> latestPageContent = itemViewAdapter.getPage(latestPage);
        return latestPageContent != null && latestPageContent.size() == 0;
    }

    protected boolean isAwaiting()
    {
        return pagedSubscriptions.size() > 0;
    }

    protected boolean isRequesting(int page)
    {
        return pagedSubscriptions.containsKey(page) || pagedPastSubscriptions.containsKey(page);
    }

    @NonNull abstract protected DTOCacheRx<PagedDTOKeyType, ContainerDTOType> getCache();

    protected void unsubscribeListCache()
    {
        for (Integer page : new ArrayList<>(pagedSubscriptions.keySet()))
        {
            unsubscribe(pagedSubscriptions.get(page));
            pagedSubscriptions.remove(page);
        }
        for (Integer page : new ArrayList<>(pagedPastSubscriptions.keySet()))
        {
            unsubscribe(pagedPastSubscriptions.get(page));
            pagedPastSubscriptions.remove(page);
        }
    }

    protected void scheduleRequestData()
    {
        View view = getView();
        if (view != null)
        {
            startAnew();
            requestDtos();
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

    protected void requestPage(final int pageToLoad)
    {
        final PagedDTOKeyType pagedKey = makePagedDtoKey(pageToLoad);
        if (!isRequesting(pageToLoad))
        {
            final AtomicBoolean alreadyGotNext = new AtomicBoolean(false);
            Subscription subscription;
            subscription = getCache().get(pagedKey)
                    .debounce(1000, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.computation())
                    .doOnNext(new Action1<Pair<PagedDTOKeyType, ContainerDTOType>>()
                    {
                        @Override public void call(Pair<PagedDTOKeyType, ContainerDTOType> pair)
                        {
                            alreadyGotNext.set(true);
                            Subscription removed = pagedSubscriptions.remove(pageToLoad);
                            if (removed != null)
                            {
                                pagedPastSubscriptions.put(
                                        pageToLoad,
                                        removed);
                            }
                        }
                    })
                    .finallyDo(new Action0()
                    {
                        @Override public void call()
                        {
                            pagedSubscriptions.remove(pageToLoad);
                            pagedPastSubscriptions.remove(pageToLoad);
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            new Action1<Pair<PagedDTOKeyType, ContainerDTOType>>()
                            {
                                @Override public void call(Pair<PagedDTOKeyType, ContainerDTOType> pair)
                                {
                                    BasePagedListRxFragment.this.onNext(pair.first, pair.second);
                                }
                            },
                            new Action1<Throwable>()
                            {
                                @Override public void call(Throwable error)
                                {
                                    BasePagedListRxFragment.this.onError(pagedKey, error);
                                }
                            });
            if (alreadyGotNext.get())
            {
                pagedPastSubscriptions.put(
                        pageToLoad,
                        subscription);
            }
            else
            {
                pagedSubscriptions.put(
                        pageToLoad,
                        subscription);
            }
        }
    }

    protected void onNext(@NonNull PagedDTOKeyType key, @NonNull ContainerDTOType value)
    {
        Timber.d("Page loaded: %d", key.getPage());
        itemViewAdapter.addPage(key.getPage(), value.getList());
        updateVisibilities();

        nearEndScrollListener.lowerEndFlag();
        if (value.size() == 0)
        {
            nearEndScrollListener.deactivateEnd();
        }
    }

    protected void onError(@NonNull PagedDTOKeyType key, @NonNull Throwable error)
    {
        if (nearEndScrollListener != null)
        {
            nearEndScrollListener.lowerEndFlag();
        }
        updateVisibilities();
    }

    abstract public boolean canMakePagedDtoKey();

    @NonNull abstract public PagedDTOKeyType makePagedDtoKey(int page);

    protected void updateVisibilities()
    {
        if (mProgress != null && emptyContainer != null)
        {
            mProgress.setVisibility(isAwaiting() ? View.VISIBLE : View.INVISIBLE);

            boolean hasItems = (itemViewAdapter != null) && (itemViewAdapter.getCount() > 0);
            emptyContainer.setVisibility(hasItems ? View.GONE : View.VISIBLE);
        }
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
}
