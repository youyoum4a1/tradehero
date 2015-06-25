package com.tradehero.th.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.api.PagedDTOKey;
import com.tradehero.common.persistence.ContainerDTO;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.persistence.DTOCacheRx;
import com.tradehero.common.widget.FlagNearEdgeRecyclerScrollListener;
import com.tradehero.th.R;
import com.tradehero.th.adapters.PagedRecyclerAdapter;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.widget.MultiRecyclerScrollListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

abstract public class BasePagedRecyclerRxFragment<
        PagedDTOKeyType extends PagedDTOKey,
        DTOType extends DTO,
        DTOListType extends DTO & List<DTOType>,
        ContainerDTOType extends DTO & ContainerDTO<DTOType, DTOListType>>
        extends BasePurchaseManagerFragment
{
    private final static String BUNDLE_KEY_PER_PAGE = BasePagedRecyclerRxFragment.class.getName() + ".perPage";

    public final static int FIRST_PAGE = 1;
    public final static int DEFAULT_PER_PAGE = 15;

    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    @InjectView(R.id.search_empty_container) protected View emptyContainer;
    @InjectView(R.id.recycler_view) protected RecyclerView recyclerView;
    @InjectView(R.id.progress) protected ProgressBar mProgress;

    protected int perPage = DEFAULT_PER_PAGE;
    protected FlagNearEdgeRecyclerScrollListener nearEndScrollListener;

    protected PagedRecyclerAdapter<DTOType> itemViewAdapter;
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
        recyclerView.addOnScrollListener(createRecyclerViewScrollListener());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(onImplementAdapter(itemViewAdapter));
    }

    protected RecyclerView.Adapter onImplementAdapter(RecyclerView.Adapter adapter)
    {
        return adapter;
    }

    @NonNull protected RecyclerView.OnScrollListener createRecyclerViewScrollListener()
    {
        return new MultiRecyclerScrollListener(nearEndScrollListener, fragmentElements.get().getRecyclerViewScrollListener());
    }

    @Override public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        putPerPage(outState, perPage);
    }

    @Override public void onDestroyView()
    {
        unsubscribeListCache();
        recyclerView.clearOnScrollListeners();
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

    @NonNull abstract protected PagedRecyclerAdapter<DTOType> createItemViewAdapter();

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
        return itemViewAdapter.getItemCount() == 0;
    }

    protected boolean isLast(int page)
    {
        Integer latestPage = itemViewAdapter.getLatestPage();
        if (latestPage == null || !latestPage.equals(page))
        {
            return false;
        }
        return itemViewAdapter.getPageSize(page) == 0;
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
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new Func1<Pair<PagedDTOKeyType, ContainerDTOType>, Pair<PagedDTOKeyType, ContainerDTOType>>()
                    {
                        @Override public Pair<PagedDTOKeyType, ContainerDTOType> call(
                                Pair<PagedDTOKeyType, ContainerDTOType> pagedDTOKeyTypeContainerDTOTypePair)
                        {
                            return onMap(pagedDTOKeyTypeContainerDTOTypePair);
                        }
                    })
                    .subscribe(
                            new Action1<Pair<PagedDTOKeyType, ContainerDTOType>>()
                            {
                                @Override public void call(Pair<PagedDTOKeyType, ContainerDTOType> pair)
                                {
                                    BasePagedRecyclerRxFragment.this.onNext(pair.first, pair.second);
                                }
                            },
                            new Action1<Throwable>()
                            {
                                @Override public void call(Throwable error)
                                {
                                    BasePagedRecyclerRxFragment.this.onError(pagedKey, error);
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

    protected Pair<PagedDTOKeyType, ContainerDTOType> onMap(Pair<PagedDTOKeyType, ContainerDTOType> receivedPair)
    {
        return receivedPair;
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

            boolean hasItems = (itemViewAdapter != null) && (itemViewAdapter.getItemCount() > 0);
            emptyContainer.setVisibility(hasItems ? View.GONE : View.VISIBLE);
        }
    }

    protected FlagNearEdgeRecyclerScrollListener createFlagNearEdgeScrollListener()
    {
        return new RecyclerFlagNearEdgeScrollListener();
    }

    protected class RecyclerFlagNearEdgeScrollListener extends FlagNearEdgeRecyclerScrollListener
    {
        @Override public void raiseEndFlag()
        {
            super.raiseEndFlag();
            requestDtos();
        }
    }

    //@OnItemClick(R.id.listview)
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