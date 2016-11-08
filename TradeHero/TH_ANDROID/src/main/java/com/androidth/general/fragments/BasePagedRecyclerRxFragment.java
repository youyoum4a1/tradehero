package com.androidth.general.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.ProgressBar;

import com.androidth.general.R;
import com.androidth.general.adapters.PagedRecyclerAdapter;
import com.androidth.general.common.api.PagedDTOKey;
import com.androidth.general.common.persistence.ContainerDTO;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.common.persistence.DTOCacheRx;
import com.androidth.general.common.widget.FlagNearEdgeRecyclerScrollListener;
import com.androidth.general.fragments.billing.BasePurchaseManagerFragment;
import com.androidth.general.widget.MultiRecyclerScrollListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Subscription;
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
    public final static int DEFAULT_PER_PAGE = 20;

    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    @Bind(R.id.search_empty_container) protected View emptyContainer;
    @Bind(R.id.recycler_view) protected RecyclerView recyclerView;
    @Bind(R.id.progress) protected ProgressBar mProgress;

    protected int perPage = DEFAULT_PER_PAGE;
    protected FlagNearEdgeRecyclerScrollListener nearEndScrollListener;

    protected PagedRecyclerAdapter<DTOType> itemViewAdapter;
    @NonNull protected final Map<Integer, Subscription> pagedSubscriptions;
    @NonNull protected final Map<Integer, Subscription> pagedPastSubscriptions;

    public static void putPerPage(@NonNull Bundle args, int perPage)
    {
        args.putInt(BUNDLE_KEY_PER_PAGE, perPage);
    }

    public static int getPerPage(@Nullable Bundle args, int defaultPerPage)
    {
        if (args != null && args.containsKey(BUNDLE_KEY_PER_PAGE))
        {
            return args.getInt(BUNDLE_KEY_PER_PAGE);
        }
        return defaultPerPage;
    }

    public BasePagedRecyclerRxFragment()
    {
        pagedSubscriptions = new HashMap<>();
        pagedPastSubscriptions = new HashMap<>();
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        perPage = getPerPage(getArguments(), DEFAULT_PER_PAGE);
        perPage = getPerPage(savedInstanceState, perPage);
        itemViewAdapter = createItemViewAdapter();
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
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
            final AtomicBoolean alreadyGotNext = new AtomicBoolean(false);
            Subscription subscription;
            subscription = getCache().get(pagedKey)
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

    protected Pair<PagedDTOKeyType, ContainerDTOType> onMap(Pair<PagedDTOKeyType, ContainerDTOType> receivedPair)
    {
        return receivedPair;
    }

    protected void onNext(@NonNull PagedDTOKeyType key, @NonNull ContainerDTOType value)
    {
        Timber.d("Page loaded: %d", key.getPage());
        itemViewAdapter.addPage(key.getPage(), value.getList());
        itemViewAdapter.notifyDataSetChanged();
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
}
