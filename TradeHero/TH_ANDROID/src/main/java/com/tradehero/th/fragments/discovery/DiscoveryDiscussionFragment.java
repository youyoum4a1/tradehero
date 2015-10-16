package com.tradehero.th.fragments.discovery;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.Bind;
import com.tradehero.th.BottomTabsQuickReturnListViewListener;
import com.tradehero.th.R;
import com.tradehero.th.api.pagination.RangeDTO;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.timeline.TimelineSection;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.fragments.base.FragmentOuterElements;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewLinearDTOFactory;
import com.tradehero.th.fragments.discussion.DiscussionEditPostFragment;
import com.tradehero.th.fragments.discussion.DiscussionFragmentUtil;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.discussion.UserDiscussionAction;
import com.tradehero.th.network.service.UserTimelineServiceWrapper;
import com.tradehero.th.rx.PaginationObservable;
import com.tradehero.th.rx.RxLoaderManager;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.rx.TimberAndToastOnErrorAction1;
import com.tradehero.th.rx.ToastOnErrorAction1;
import com.tradehero.th.widget.LiveWidgetScrollListener;
import com.tradehero.th.widget.MultiScrollListener;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.internal.util.SubscriptionList;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

import static com.tradehero.th.rx.view.list.ListViewObservable.createNearEndScrollOperator;
import static com.tradehero.th.utils.Constants.TIMELINE_ITEM_PER_PAGE;

public class DiscoveryDiscussionFragment extends Fragment
{
    private static final String DISCOVERY_LIST_LOADER_ID = DiscoveryDiscussionFragment.class.getName() + ".discoveryList";

    @Bind(android.R.id.progress) ProgressBar progressBar;
    @Bind(R.id.swipe_container) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.timeline_list_view) ListView mTimelineListView;
    @Inject @BottomTabsQuickReturnListViewListener AbsListView.OnScrollListener dashboardBottomTabsScrollListener;
    @Inject RxLoaderManager rxLoaderManager;
    @Inject CurrentUserId currentUserId;
    @Inject UserTimelineServiceWrapper userTimelineServiceWrapper;
    @Inject Lazy<DashboardNavigator> navigator;
    @Inject DiscussionFragmentUtil discussionFragmentUtil;
    @Inject AbstractDiscussionCompactItemViewLinearDTOFactory viewDTOFactory;
    @Inject Lazy<FragmentOuterElements> fragmentOuterElements;

    private ProgressBar mBottomLoadingView;

    private DiscussionArrayAdapter discoveryDiscussionAdapter;
    @NonNull private CompositeSubscription timelineSubscriptions;
    protected SubscriptionList onStopSubscriptions;

    private RangeDTO currentRangeDTO = new RangeDTO(TIMELINE_ITEM_PER_PAGE, null, null);

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        HierarchyInjector.inject(this);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        discoveryDiscussionAdapter = new DiscussionArrayAdapter(getActivity(), R.layout.timeline_item_view);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.discovery_discussion, container, false);
        initView(view);
        return view;
    }

    @Override public void onStart()
    {
        super.onStart();
        onStopSubscriptions = new SubscriptionList();
        registerUserActions();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_create_post_discussion, menu);
        MenuItem postMenuButton = menu.findItem(R.id.discussion_edit_post);
        if (postMenuButton != null)
        {
            postMenuButton.setVisible(true);
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.discussion_edit_post)
        {
            DiscussionEditPostFragment discussionEditPostFragment = navigator.get().pushFragment(DiscussionEditPostFragment.class);
            discussionEditPostFragment.setCommentPostedListener(new DiscussionEditPostFragment.DiscussionPostedListener()
            {
                @Override public void onDiscussionPosted()
                {
                    DiscoveryDiscussionFragment.this.refresh();
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onStop()
    {
        onStopSubscriptions.unsubscribe();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        timelineSubscriptions.unsubscribe();
        rxLoaderManager.remove(DISCOVERY_LIST_LOADER_ID);
        timelineSubscriptions = null;
        super.onDestroyView();
    }

    protected void registerUserActions()
    {
        onStopSubscriptions.add(
                discoveryDiscussionAdapter.getUserActionObservable()
                        .subscribe(
                                new Action1<UserDiscussionAction>()
                                {
                                    @Override public void call(UserDiscussionAction userDiscussionAction)
                                    {
                                        discussionFragmentUtil.handleUserAction(getActivity(), userDiscussionAction);
                                    }
                                },
                                new TimberAndToastOnErrorAction1("Failed to listen to user actions")));
    }

    private void refresh()
    {
        currentRangeDTO = new RangeDTO(TIMELINE_ITEM_PER_PAGE, null, null);
        subscribes();
    }

    private Observable<RangeDTO> createPaginationObservable()
    {
        Observable<RangeDTO> pullFromStartObservable = Observable.create(new Observable.OnSubscribe<RangeDTO>()
        {
            @Override public void call(final Subscriber<? super RangeDTO> subscriber)
            {
                swipeRefreshLayout.setOnRefreshListener(
                        new SwipeRefreshLayout.OnRefreshListener()
                        {
                            @Override public void onRefresh()
                            {
                                subscriber.onNext(
                                        RangeDTO.create(currentRangeDTO.maxCount, null, currentRangeDTO.maxId));
                            }
                        }
                );
            }
        });
        Observable<RangeDTO> pullFromBottomObservable = Observable.create(
                new Observable.OnSubscribe<RangeDTO>()
                {
                    @Override public void call(Subscriber<? super RangeDTO> subscriber)
                    {
                        if (getParentFragment() instanceof DiscoveryMainFragment)
                        {
                            mTimelineListView.setOnScrollListener(
                                    new MultiScrollListener(
                                            dashboardBottomTabsScrollListener,
                                            new LiveWidgetScrollListener(fragmentOuterElements.get(),
                                                    ((DiscoveryMainFragment) getParentFragment()).getLiveFragmentUtil()),
                                            createNearEndScrollOperator(
                                                    subscriber,
                                                    new Func0<RangeDTO>()
                                                    {
                                                        @Override public RangeDTO call()
                                                        {
                                                            return RangeDTO.create(currentRangeDTO.maxCount, currentRangeDTO.minId, null);
                                                        }
                                                    })));
                        }
                        else
                        {
                            mTimelineListView.setOnScrollListener(
                                    new MultiScrollListener(
                                            dashboardBottomTabsScrollListener,
                                            createNearEndScrollOperator(
                                                    subscriber,
                                                    new Func0<RangeDTO>()
                                                    {
                                                        @Override public RangeDTO call()
                                                        {
                                                            return RangeDTO.create(currentRangeDTO.maxCount, currentRangeDTO.minId, null);
                                                        }
                                                    })));
                        }
                    }
                })
                .doOnNext(new Action1<RangeDTO>()
                {
                    @Override public void call(RangeDTO o)
                    {
                        mBottomLoadingView.setVisibility(View.VISIBLE);
                    }
                });
        return Observable.merge(pullFromBottomObservable, pullFromStartObservable)
                .subscribeOn(AndroidSchedulers.mainThread())
                .startWith(RangeDTO.create(TIMELINE_ITEM_PER_PAGE, null, null));
    }

    private void initView(View view)
    {
        ButterKnife.bind(this, view);

        mBottomLoadingView = new ProgressBar(getActivity());
        mBottomLoadingView.setVisibility(View.INVISIBLE);
        mTimelineListView.addFooterView(mBottomLoadingView);
        mTimelineListView.setAdapter(discoveryDiscussionAdapter);

        subscribes();
    }

    private void subscribes()
    {
        timelineSubscriptions = new CompositeSubscription();

        PublishSubject<List<TimelineItemDTO>> timelineSubject = PublishSubject.create();
        timelineSubscriptions.add(timelineSubject.subscribe(new RefreshCompleteObserver()));
        timelineSubscriptions.add(timelineSubject
                .flatMap(new Func1<List<TimelineItemDTO>, Observable<List<AbstractDiscussionCompactItemViewLinear.DTO>>>()
                {
                    @Override public Observable<List<AbstractDiscussionCompactItemViewLinear.DTO>> call(final List<TimelineItemDTO> timelineItemDTOs)
                    {
                        return viewDTOFactory.createTimelineItemViewLinearDTOs(timelineItemDTOs);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<List<AbstractDiscussionCompactItemViewLinear.DTO>>()
                        {
                            @Override public void call(List<AbstractDiscussionCompactItemViewLinear.DTO> list)
                            {
                                discoveryDiscussionAdapter.setNotifyOnChange(false);
                                discoveryDiscussionAdapter.clear();
                                discoveryDiscussionAdapter.addAll(list);
                                discoveryDiscussionAdapter.setNotifyOnChange(true);
                                discoveryDiscussionAdapter.notifyDataSetChanged();
                            }
                        },
                        new TimberOnErrorAction1("Gotcha timelineSubject in DiscoveryDiscussion gave error")));
        timelineSubscriptions.add(timelineSubject.subscribe(new UpdateRangeObserver()));

        Observable<RangeDTO> timelineRefreshRangeObservable = createPaginationObservable();
        progressBar.setVisibility(View.VISIBLE);
        timelineSubscriptions.add(rxLoaderManager.create(
                DISCOVERY_LIST_LOADER_ID,
                PaginationObservable.createFromRange(
                        timelineRefreshRangeObservable,
                        new Func1<RangeDTO, Observable<List<TimelineItemDTO>>>()
                        {
                            @Override public Observable<List<TimelineItemDTO>> call(RangeDTO rangeDTO)
                            {
                                return userTimelineServiceWrapper.getTimelineBySectionRx(TimelineSection.Hot, currentUserId.toUserBaseKey(), rangeDTO)
                                        .flatMapIterable(new Func1<TimelineDTO, Iterable<? extends TimelineItemDTO>>()
                                        {
                                            @Override public Iterable<? extends TimelineItemDTO> call(TimelineDTO timeline)
                                            {
                                                return timeline.getEnhancedItems();
                                            }
                                        })
                                        .cast(TimelineItemDTO.class)
                                        .toList();
                            }
                        }))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new ToastOnErrorAction1())
                .onErrorResumeNext(Observable.<List<TimelineItemDTO>>empty())
                .doOnUnsubscribe(new Action0()
                {
                    @Override public void call()
                    {
                        if (progressBar != null) progressBar.setVisibility(View.INVISIBLE);
                    }
                })
                .subscribe(timelineSubject));
    }

    private class RefreshCompleteObserver implements Observer<List<TimelineItemDTO>>
    {
        private void refreshComplete()
        {
            progressBar.setVisibility(View.INVISIBLE);
            mBottomLoadingView.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override public void onCompleted()
        {
            refreshComplete();
        }

        @Override public void onError(Throwable e)
        {
            refreshComplete();
        }

        @Override public void onNext(List<TimelineItemDTO> timelineItemDTOKeys)
        {
            refreshComplete();
        }
    }

    private class UpdateRangeObserver implements Observer<List<TimelineItemDTO>>
    {
        @Override public void onNext(List<TimelineItemDTO> timelineItemDTOKeys)
        {
            if (timelineItemDTOKeys != null && !timelineItemDTOKeys.isEmpty())
            {
                Integer max = timelineItemDTOKeys.get(0).id;
                Integer min = timelineItemDTOKeys.get(timelineItemDTOKeys.size() - 1).id;
                currentRangeDTO = RangeDTO.create(TIMELINE_ITEM_PER_PAGE, max, min);
            }
            else
            {
                currentRangeDTO = RangeDTO.create(TIMELINE_ITEM_PER_PAGE, null, null);
            }
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
        }
    }
}
