package com.tradehero.th.fragments.discussion;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import com.tradehero.common.fragment.HasSelectedItem;
import com.tradehero.common.widget.FlagNearEdgeScrollListener;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionKeyFactory;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.discussion.key.DiscussionListKeyFactory;
import com.tradehero.th.api.discussion.key.PaginatedDiscussionListKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.fragments.OnMovableBottomTranslateListener;
import com.tradehero.th.fragments.base.BaseFragment;
import com.tradehero.th.fragments.base.FragmentOuterElements;
import com.tradehero.th.models.discussion.UserDiscussionAction;
import com.tradehero.th.persistence.discussion.DiscussionCacheRx;
import com.tradehero.th.persistence.discussion.DiscussionListCacheRx;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import com.tradehero.th.rx.ToastOnErrorAction;
import com.tradehero.th.widget.MultiScrollListener;
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

abstract public class AbstractDiscussionFragment extends BaseFragment
{
    private static final String DISCUSSION_KEY_BUNDLE_KEY = AbstractDiscussionFragment.class.getName() + ".discussionKey";

    @InjectView(android.R.id.list) protected ListView discussionList;
    @InjectView(R.id.post_comment_text) @Optional protected EditText postCommentText;
    @InjectView(R.id.mention_widget) @Optional protected MentionActionButtonsView mentionActionButtonsView;
    @InjectView(R.id.discussion_comment_widget) @Optional protected PostCommentView postCommentView;
    protected View topicView;
    protected TextView discussionStatus;

    @Inject protected DiscussionCacheRx discussionCache;
    @Inject protected DiscussionListCacheRx discussionListCache;
    @Inject protected MentionTaggedStockHandler mentionTaggedStockHandler;
    @Inject protected AbstractDiscussionCompactItemViewLinearDTOFactory viewDTOFactory;
    @Inject protected DiscussionFragmentUtil discussionFragmentUtil;
    @Inject protected FragmentOuterElements fragmentElements;

    protected DiscussionSetAdapter discussionListAdapter;
    protected DiscussionKey discussionKey;
    protected AbstractDiscussionCompactDTO topicDiscussion;
    private Subscription hasSelectedSubscription;
    private PublishSubject<Boolean> nearEdgeSubject; // True means start reached, False means end reached

    //region Inflow bundling
    public static void putDiscussionKey(@NonNull Bundle args, @NonNull DiscussionKey discussionKey)
    {
        args.putBundle(DISCUSSION_KEY_BUNDLE_KEY, discussionKey.getArgs());
    }

    @Nullable protected static DiscussionKey getDiscussionKey(@NonNull Bundle args)
    {
        if (args.containsKey(DISCUSSION_KEY_BUNDLE_KEY))
        {
            return DiscussionKeyFactory.fromBundle(args.getBundle(DISCUSSION_KEY_BUNDLE_KEY));
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
        ButterKnife.inject(this, view);
        discussionList.setOnScrollListener(new MultiScrollListener(fragmentElements.getListViewScrollListener(), createListScrollListener()));
        topicView = inflateTopicView();
        if (topicView != null)
        {
            discussionList.addHeaderView(topicView, null, false);
        }
        discussionList.setAdapter(discussionListAdapter);
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
    }

    @Override public void onStart()
    {
        super.onStart();
        fetchTopic();
        fetchDiscussionList();
        registerUserActions();
    }

    @Override public void onResume()
    {
        super.onResume();
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
        ButterKnife.reset(this);
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

    @Nullable abstract protected View inflateTopicView();

    public DiscussionKey getDiscussionKey()
    {
        return discussionKey;
    }

    protected void linkWith(DiscussionKey discussionKey, boolean andDisplay)
    {
        this.discussionKey = discussionKey;
    }

    //<editor-fold desc="Topic">
    protected void fetchTopic()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                discussionCache.get(discussionKey))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Pair<DiscussionKey, AbstractDiscussionCompactDTO>>()
                        {
                            @Override
                            public void call(Pair<DiscussionKey, AbstractDiscussionCompactDTO> pair)
                            {
                                displayTopic(pair.second);
                            }
                        },
                        new ToastAndLogOnErrorAction(getString(R.string.error_fetch_private_message_initiating_discussion), "Initial message")
                ));
    }

    protected void displayTopic(@NonNull AbstractDiscussionCompactDTO discussionDTO)
    {
        this.topicDiscussion = discussionDTO;
    }
    //</editor-fold>

    protected void fetchDiscussionList()
    {
        onStopSubscriptions.add(AppObservable.bindFragment(
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
                        new ToastAndLogOnErrorAction("Failed to load discussion")));
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
                onStopSubscriptions.add(AppObservable.bindFragment(
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
                                new ToastOnErrorAction()));
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
        onStopSubscriptions.add(AppObservable.bindFragment(
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
                        new ToastOnErrorAction()));
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
            onStopSubscriptions.add(AppObservable.bindFragment(
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
                            }
                    ));
        }
    }

    protected void registerUserActions()
    {
        Observable<UserDiscussionAction> userActionObservable = discussionListAdapter.getUserActionObservable();
        if (topicView instanceof AbstractDiscussionCompactItemViewLinear)
        {
            userActionObservable.mergeWith(((AbstractDiscussionCompactItemViewLinear) topicView).getUserActionObservable());
        }
        onStopSubscriptions.add(AppObservable.bindFragment(
                this,
                userActionObservable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<UserDiscussionAction>()
                        {
                            @Override public void call(UserDiscussionAction userDiscussionAction)
                            {
                                discussionFragmentUtil.handleUserAction(getActivity(), userDiscussionAction);
                            }
                        },
                        new ToastOnErrorAction()));
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
                            new ToastOnErrorAction());
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
