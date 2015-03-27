package com.tradehero.th.fragments;

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
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.tradehero.common.api.PagedDTOKey;
import com.tradehero.common.persistence.ContainerDTO;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.common.widget.FlagNearEdgeScrollListener;
import com.tradehero.th.R;
import com.tradehero.th.adapters.PagedDTOAdapter;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.widget.MultiScrollListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.app.AppObservable;
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

    @InjectView(R.id.search_empty_container) protected View emptyContainer;
    @InjectView(R.id.listview) protected AbsListView listView;
    @InjectView(R.id.progress) protected ProgressBar mProgress;

    protected int perPage = DEFAULT_PER_PAGE;
    protected FlagNearEdgeScrollListener nearEndScrollListener;

    protected PagedDTOAdapter<DTOType> itemViewAdapter;
    @NonNull protected Map<Integer, Subscription> pagedSubscriptions;
    @NonNull protected Map<Integer, Subscription> pagedPastSubscriptions;
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

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        pagedSubscriptions = new HashMap<>();
        pagedPastSubscriptions = new HashMap<>();
        perPage = getPerPage(getArguments());
        perPage = getPerPage(savedInstanceState);
        itemViewAdapter = createItemViewAdapter();
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
            final boolean[] alreadyGotNext = new boolean[] {false};
            Subscription subscription;
            subscription = AppObservable.bindFragment(
                    this,
                    getCache().get(pagedKey)
                            .subscribeOn(Schedulers.computation())
                            .doOnNext(new Action1<Pair<PagedDTOKeyType, ContainerDTOType>>()
                            {
                                @Override public void call(Pair<PagedDTOKeyType, ContainerDTOType> pair)
                                {
                                    Subscription removed = pagedSubscriptions.remove(pageToLoad);
                                    if (removed != null)
                                    {
                                        pagedPastSubscriptions.put(
                                                pageToLoad,
                                                removed);
                                    }
                                    alreadyGotNext[0] = true;
                                }
                            })
                            .finallyDo(new Action0()
                            {
                                @Override public void call()
                                {
                                    pagedSubscriptions.remove(pageToLoad);
                                    pagedPastSubscriptions.remove(pageToLoad);
                                }
                            }))
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
            if (alreadyGotNext[0])
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
        nearEndScrollListener.lowerEndFlag();
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
