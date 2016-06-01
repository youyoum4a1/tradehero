package com.ayondo.academy.fragments.discussion;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.tradehero.common.fragment.HasSelectedItem;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.widget.FlagNearEdgeScrollListener;
import com.ayondo.academy.R;
import com.ayondo.academy.api.discussion.AbstractDiscussionCompactDTO;
import com.ayondo.academy.api.discussion.DiscussionDTO;
import com.ayondo.academy.api.discussion.key.DiscussionKey;
import com.ayondo.academy.api.discussion.key.DiscussionKeyFactory;
import com.ayondo.academy.api.discussion.key.DiscussionListKey;
import com.ayondo.academy.api.discussion.key.DiscussionListKeyFactory;
import com.ayondo.academy.api.discussion.key.PaginatedDiscussionListKey;
import com.ayondo.academy.api.pagination.PaginatedDTO;
import com.ayondo.academy.fragments.OnMovableBottomTranslateListener;
import com.ayondo.academy.fragments.base.BaseFragment;
import com.ayondo.academy.fragments.base.FragmentOuterElements;
import com.ayondo.academy.inject.HierarchyInjector;
import com.ayondo.academy.models.discussion.UserDiscussionAction;
import com.ayondo.academy.persistence.discussion.DiscussionCacheRx;
import com.ayondo.academy.persistence.discussion.DiscussionListCacheRx;
import com.ayondo.academy.rx.TimberAndToastOnErrorAction1;
import com.ayondo.academy.rx.TimberOnErrorAction1;
import com.ayondo.academy.rx.ToastOnErrorAction1;
import com.ayondo.academy.widget.MultiScrollListener;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import timber.log.Timber;

abstract public class AbstractDiscussionFragment extends BaseFragment
{
    private static final String DISCUSSION_KEY_BUNDLE_KEY = AbstractDiscussionFragment.class.getName() + ".discussionKey";

    @Bind(android.R.id.list) protected ListView discussionList;
    @Bind(R.id.post_comment_text) @Nullable protected EditText postCommentText;
    @Bind(R.id.mention_widget) @Nullable protected MentionActionButtonsView mentionActionButtonsView;
    @Bind(R.id.discussion_comment_widget) @Nullable protected PostCommentView postCommentView;

    @Inject protected DiscussionCacheRx discussionCache;
    @Inject protected DiscussionListCacheRx discussionListCache;
    @Inject protected MentionTaggedStockHandler mentionTaggedStockHandler;
    @Inject protected AbstractDiscussionCompactItemViewLinearDTOFactory viewDTOFactory;
    @Inject protected DiscussionFragmentUtil discussionFragmentUtil;
    @Inject protected FragmentOuterElements fragmentElements;

    protected DiscussionSetAdapter discussionListAdapter;
    protected DiscussionKey discussionKey;
    private Subscription hasSelectedSubscription;
    private PublishSubject<Boolean> nearEdgeSubject; // True means start reached, False means end reached

    //region Inflow bundling
    public static void putDiscussionKey(@NonNull Bundle args, @NonNull DiscussionKey discussionKey)
    {
        args.putBundle(DISCUSSION_KEY_BUNDLE_KEY, discussionKey.getArgs());
    }

    @Nullable protected static DiscussionKey getDiscussionKey(@NonNull Bundle args)
    {
        Bundle discussionKeyBundle = args.getBundle(DISCUSSION_KEY_BUNDLE_KEY);
        if (discussionKeyBundle != null)
        {
            return DiscussionKeyFactory.fromBundle(discussionKeyBundle);
        }
        return null;
    }
    //endregion

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.discussionKey = getDiscussionKey(getArguments());
        nearEdgeSubject = PublishSubject.create();
        discussionListAdapter = createDiscussionListAdapter();
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        discussionList.setOnScrollListener(new MultiScrollListener(fragmentElements.getListViewScrollListener(), createListScrollListener()));

        onDestroyViewSubscriptions.add(getTopicViewObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<View>()
                        {
                            @Override public void call(@Nullable View topicView)
                            {
                                if (topicView != null)
                                {
                                    discussionList.addHeaderView(topicView, null, false);
                                }
                                discussionList.setAdapter(discussionListAdapter);
                            }
                        },
                        new TimberOnErrorAction1("Failed to get topic view")));

        mentionTaggedStockHandler.setDiscussionPostContent(postCommentText);
        subscribeHasSelected();
        if (postCommentView != null)
        {
            postCommentView.linkWith(discussionKey);
            postCommentView.setCommentPostedListener(createCommentPostedListener());
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) postCommentView.getLayoutParams();
            params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, fragmentElements.getMovableBottom().getHeight());
            postCommentView.setLayoutParams(params);
        }

        if (mentionActionButtonsView != null)
        {
            mentionActionButtonsView.setReturnFragmentName(getClass().getName());
        }
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchDiscussionList();
        registerUserActions();
    }

    @Override public void onResume()
    {
        super.onResume();
        if (fragmentElements == null)
        {
            Timber.e(new NullPointerException(), "Re-injecting");
            HierarchyInjector.inject(this);
        }
        mentionTaggedStockHandler.collectSelection();
        fragmentElements.getMovableBottom().setOnMovableBottomTranslateListener(new OnMovableBottomTranslateListener()
        {
            @Override public void onTranslate(float x, float y)
            {
                if (postCommentView != null)
                {
                    postCommentView.setTranslationY(y);
                }
            }
        });
    }

    @Override public void onPause()
    {
        fragmentElements.getMovableBottom().setOnMovableBottomTranslateListener(null);
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        unsubscribe(hasSelectedSubscription);
        hasSelectedSubscription = null;
        mentionTaggedStockHandler.setDiscussionPostContent(null);
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        mentionTaggedStockHandler.setHasSelectedItemFragment(null);
        mentionTaggedStockHandler = null;
        super.onDestroy();
    }

    @NonNull abstract protected DiscussionSetAdapter createDiscussionListAdapter();

    @NonNull protected AbsListView.OnScrollListener createListScrollListener()
    {
        return new MultiScrollListener(
                new FlagNearEdgeScrollListener()
                {
                    @Override public void raiseStartFlag()
                    {
                        super.raiseStartFlag();
                        nearEdgeSubject.onNext(true);
                    }

                    @Override public void raiseEndFlag()
                    {
                        super.raiseEndFlag();
                        nearEdgeSubject.onNext(false);
                    }
                });
    }

    @Nullable abstract protected View inflateTopicView(@NonNull AbstractDiscussionCompactDTO topicDiscussion);

    public DiscussionKey getDiscussionKey()
    {
        return discussionKey;
    }

    protected void linkWith(DiscussionKey discussionKey, boolean andDisplay)
    {
        this.discussionKey = discussionKey;
    }

    //<editor-fold desc="Topic">
    @NonNull protected Observable<AbstractDiscussionCompactDTO> getTopicObservable()
    {
        return discussionCache.getOne(discussionKey)
                .map(new PairGetSecond<DiscussionKey, AbstractDiscussionCompactDTO>())
                .share();
    }

    @NonNull protected Observable<View> getTopicViewObservable() // It can pass null values
    {
        return getTopicObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<AbstractDiscussionCompactDTO, View>()
                {
                    @Override public View call(AbstractDiscussionCompactDTO topicDiscussion)
                    {
                        return inflateTopicView(topicDiscussion);
                    }
                })
                .share();
    }
    //</editor-fold>

    protected void fetchDiscussionList()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                fetchAndCreateDTOs(createTopicDiscussionListKey()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Pair<DiscussionListKey, List<AbstractDiscussionCompactItemViewLinear.DTO>>>()
                        {
                            @Override public void call(
                                    Pair<DiscussionListKey, List<AbstractDiscussionCompactItemViewLinear.DTO>> viewDTOsPair)
                            {
                                discussionListAdapter.appendTail(viewDTOsPair.second);
                                discussionListAdapter.notifyDataSetChanged();
                                fetchNextDiscussionList(
                                        viewDTOsPair.first,
                                        viewDTOsPair.second);
                                fetchMostRecentDiscussionList(
                                        viewDTOsPair.first,
                                        viewDTOsPair.second);
                            }
                        },
                        new TimberAndToastOnErrorAction1("Failed to load discussion")));
    }

    @NonNull protected Observable<DiscussionListKey> createTopicDiscussionListKey()
    {
        if (discussionKey != null)
        {
            return Observable.just((DiscussionListKey) new PaginatedDiscussionListKey(DiscussionListKeyFactory.create(discussionKey), 1));
        }
        return Observable.empty();
    }

    protected void fetchNextDiscussionList(
            @NonNull DiscussionListKey latestKey,
            @NonNull List<AbstractDiscussionCompactItemViewLinear.DTO> latestDtos)
    {
        if (!latestDtos.isEmpty())
        {
            DiscussionListKey next = getNextKey(latestKey, latestDtos);
            if (next != null)
            {
                onStopSubscriptions.add(AppObservable.bindSupportFragment(
                        this,
                        fetchAndCreateDTOs(
                                Observable.just(next)
                                        .delay(new Func1<DiscussionListKey, Observable<Boolean>>()
                                        {
                                            @Override public Observable<Boolean> call(DiscussionListKey paginatedDiscussionListKey)
                                            {
                                                // Waiting for the flag to raise
                                                return nearEdgeSubject.filter(
                                                        new Func1<Boolean, Boolean>()
                                                        {
                                                            @Override public Boolean call(Boolean startReached)
                                                            {
                                                                return !startReached;
                                                            }
                                                        });
                                            }
                                        })))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<Pair<DiscussionListKey, List<AbstractDiscussionCompactItemViewLinear.DTO>>>()
                                {
                                    @Override
                                    public void call(
                                            Pair<DiscussionListKey, List<AbstractDiscussionCompactItemViewLinear.DTO>> viewDTOsPair)
                                    {
                                        discussionListAdapter.appendTail(viewDTOsPair.second);
                                        discussionListAdapter.notifyDataSetChanged();
                                        fetchNextDiscussionList(
                                                viewDTOsPair.first,
                                                viewDTOsPair.second);
                                    }
                                },
                                new ToastOnErrorAction1()));
            }
        }
    }

    @Nullable protected DiscussionListKey getNextKey(
            @NonNull DiscussionListKey latestKey,
            @NonNull List<AbstractDiscussionCompactItemViewLinear.DTO> latestDtos)
    {
        return ((PaginatedDiscussionListKey) latestKey).next();
    }

    protected void fetchMostRecentDiscussionList(@NonNull DiscussionListKey latestKey,
            @NonNull List<AbstractDiscussionCompactItemViewLinear.DTO> newestDtos)
    {
        DiscussionListKey prev = getMostRecentKey(latestKey, newestDtos);
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                fetchAndCreateDTOs(
                        Observable.just(prev)
                                .delay(new Func1<DiscussionListKey, Observable<Boolean>>()
                                {
                                    @Override public Observable<Boolean> call(DiscussionListKey paginatedDiscussionListKey)
                                    {
                                        // Waiting for the flag to raise
                                        return nearEdgeSubject.filter(
                                                new Func1<Boolean, Boolean>()
                                                {
                                                    @Override public Boolean call(Boolean startReached)
                                                    {
                                                        return !startReached;
                                                    }
                                                });
                                    }
                                })))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Pair<DiscussionListKey, List<AbstractDiscussionCompactItemViewLinear.DTO>>>()
                        {
                            @Override
                            public void call(
                                    Pair<DiscussionListKey, List<AbstractDiscussionCompactItemViewLinear.DTO>> viewDTOsPair)
                            {
                                discussionListAdapter.appendHead(viewDTOsPair.second);
                                discussionListAdapter.notifyDataSetChanged();
                                fetchMostRecentDiscussionList(viewDTOsPair.first, viewDTOsPair.second);
                            }
                        },
                        new ToastOnErrorAction1()));
    }

    @Nullable protected DiscussionListKey getMostRecentKey(
            @NonNull DiscussionListKey latestKey,
            @NonNull List<AbstractDiscussionCompactItemViewLinear.DTO> newestDtos)
    {
        return new PaginatedDiscussionListKey(latestKey, 1);
    }

    @NonNull protected Observable<Pair<DiscussionListKey, List<AbstractDiscussionCompactItemViewLinear.DTO>>>
    fetchAndCreateDTOs(@NonNull Observable<DiscussionListKey> keyObservable)
    {
        return keyObservable
                .flatMap(new Func1<DiscussionListKey, Observable<Pair<DiscussionListKey, PaginatedDTO<DiscussionDTO>>>>()
                {
                    @Override
                    public Observable<Pair<DiscussionListKey, PaginatedDTO<DiscussionDTO>>> call(DiscussionListKey discussionListKey)
                    {
                        return discussionListCache.get(discussionListKey).subscribeOn(Schedulers.computation());
                    }
                })
                .flatMap(new Func1<
                        Pair<DiscussionListKey, PaginatedDTO<DiscussionDTO>>,
                        Observable<Pair<DiscussionListKey, List<AbstractDiscussionCompactItemViewLinear.DTO>>>>()
                {
                    @Override
                    public Observable<Pair<DiscussionListKey, List<AbstractDiscussionCompactItemViewLinear.DTO>>> call(
                            final Pair<DiscussionListKey, PaginatedDTO<DiscussionDTO>> pair)
                    {
                        return createViewDTOs(pair.second.getData())
                                .map(new Func1<List<AbstractDiscussionCompactItemViewLinear.DTO>,
                                        Pair<DiscussionListKey,
                                                List<AbstractDiscussionCompactItemViewLinear.DTO>>>()
                                {
                                    @Override
                                    public Pair<DiscussionListKey, List<AbstractDiscussionCompactItemViewLinear.DTO>> call(
                                            List<AbstractDiscussionCompactItemViewLinear.DTO> dtos)
                                    {
                                        return Pair.create(pair.first, dtos);
                                    }
                                });
                    }
                });
    }

    @NonNull protected Observable<List<AbstractDiscussionCompactItemViewLinear.DTO>> createViewDTOs(
            @NonNull List<DiscussionDTO> discussions)
    {
        return Observable.from(discussions)
                .flatMap(new Func1<DiscussionDTO, Observable<AbstractDiscussionCompactItemViewLinear.DTO>>()
                {
                    @Override
                    public Observable<AbstractDiscussionCompactItemViewLinear.DTO> call(final DiscussionDTO discussion)
                    {
                        return createViewDTO(discussion);
                    }
                })
                .toList();
    }

    @NonNull protected Observable<AbstractDiscussionCompactItemViewLinear.DTO> createViewDTO(
            @NonNull final AbstractDiscussionCompactDTO discussion)
    {
        return viewDTOFactory.createAbstractDiscussionCompactItemViewLinearDTO(discussion);
    }

    /**
     * This method is called when there is a new comment for the current discussion
     */
    protected void addComment(AbstractDiscussionCompactDTO newDiscussion)
    {
        if (discussionListAdapter != null)
        {
            onStopSubscriptions.add(AppObservable.bindSupportFragment(
                    this,
                    createViewDTO(newDiscussion))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            new Action1<AbstractDiscussionCompactItemViewLinear.DTO>()
                            {
                                @Override public void call(AbstractDiscussionCompactItemViewLinear.DTO dto)
                                {
                                    discussionListAdapter.appendTail(dto);
                                    discussionListAdapter.notifyDataSetChanged();
                                }
                            },
                            new TimberOnErrorAction1("Failed to add comment " + newDiscussion +
                                    " in " + AbstractDiscussionFragment.this.getClass().getSimpleName())));
        }
    }

    @NonNull protected Observable<UserDiscussionAction> getUserActionObservable()
    {
        return Observable.merge(
                discussionListAdapter.getUserActionObservable(),
                getTopicViewObservable()
                        .flatMap(new Func1<View, Observable<UserDiscussionAction>>()
                        {
                            @Override public Observable<UserDiscussionAction> call(@Nullable View topicView)
                            {
                                if (topicView instanceof AbstractDiscussionCompactItemViewLinear)
                                {
                                    return ((AbstractDiscussionCompactItemViewLinear) topicView).getUserActionObservable();
                                }
                                return Observable.empty();
                            }
                        }))
                .share();
    }

    protected void registerUserActions()
    {
        onStopSubscriptions.add(getUserActionObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<UserDiscussionAction>()
                        {
                            @Override public void call(UserDiscussionAction userDiscussionAction)
                            {
                                discussionFragmentUtil.handleUserAction(getActivity(), userDiscussionAction);
                            }
                        },
                        new ToastOnErrorAction1()));
    }

    private void subscribeHasSelected()
    {
        unsubscribe(hasSelectedSubscription);
        if (mentionActionButtonsView != null)
        {
            hasSelectedSubscription = mentionActionButtonsView.getSelectedItemObservable()
                    .subscribe(
                            new Action1<HasSelectedItem>()
                            {
                                @Override public void call(HasSelectedItem hasSelectedItem)
                                {
                                    mentionTaggedStockHandler.setHasSelectedItemFragment(hasSelectedItem);
                                }
                            },
                            new ToastOnErrorAction1());
        }
    }

    //<editor-fold desc="Comment Post">
    protected PostCommentView.CommentPostedListener createCommentPostedListener()
    {
        return new AbstractDiscussionCommentPostedListener();
    }

    protected class AbstractDiscussionCommentPostedListener implements PostCommentView.CommentPostedListener
    {
        @Override public void success(DiscussionDTO discussionDTO)
        {
            handleCommentPosted(discussionDTO);
        }

        @Override public void failure(Exception exception)
        {
            // Nothing to do
        }
    }

    abstract protected void handleCommentPosted(DiscussionDTO discussionDTO);
    //</editor-fold>
}
