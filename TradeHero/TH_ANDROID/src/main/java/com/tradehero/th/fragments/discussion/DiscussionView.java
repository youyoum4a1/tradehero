package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.FlagNearEdgeScrollListener;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionKeyList;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.discussion.key.DiscussionListKeyFactory;
import com.tradehero.th.api.discussion.key.PaginatedDiscussionListKey;
import com.tradehero.th.api.pagination.PaginatedDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.social.message.PrivatePostCommentView;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.discussion.DiscussionListCacheRx;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * DiscussionView is designed to show a discussion, it consists of a topic on the top for discussing about, a list of comments bellow the topic, and
 * optionally a @{link com.tradehero.th.fragments.discussion.PostCommentView} at the bottom of the view, which is float above the list of comments.
 * This view will be populated with data in the @{link com.tradehero.th.persistence.discussion.DiscussionCache}, specified by a  @{link
 * com.tradehero.th.api.discussion.key .DiscussionKey}.
 *
 * The topic layout is identified by @{link topicLayout} which is the resource id of layout resource of the topic view, in order for the topic view to
 * bind with the same data from DiscussionView, this layout class has to implement @{link com.tradehero.th.api.DTOView}
 */
public class DiscussionView extends FrameLayout
        implements DTOView<DiscussionKey>
{
    @InjectView(android.R.id.list) protected ListView discussionList;
    protected FlagNearEdgeScrollListener scrollListener;
    @InjectView(R.id.discussion_comment_widget) @Optional protected PostCommentView postCommentView;

    private int listItemLayout;
    private int topicLayout;

    @Inject protected CurrentUserId currentUserId;
    @Inject protected DiscussionListCacheRx discussionListCache;

    @NonNull private List<Subscription> discussionListCacheSubscriptions;
    protected TextView discussionStatus;
    protected DiscussionKey discussionKey;

    private PostCommentView.CommentPostedListener commentPostedListener;

    protected DiscussionSetAdapter discussionListAdapter;
    protected DiscussionListKey prevDiscussionListKey;
    protected DiscussionListKey startingDiscussionListKey;
    protected DiscussionListKey nextDiscussionListKey;
    private View topicView;

    //<editor-fold desc="Constructors">
    public DiscussionView(Context context)
    {
        super(context);
    }

    public DiscussionView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public DiscussionView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(context, attrs);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        discussionListCacheSubscriptions = new ArrayList<>();
        ButterKnife.inject(this);

        inflateDiscussionTopic();
        inflateDiscussionStatus();

        HierarchyInjector.inject(this);

        discussionListAdapter = createDiscussionListAdapter();
    }

    protected DiscussionSetAdapter createDiscussionListAdapter()
    {
        return new SingleViewDiscussionSetAdapter(getContext(), listItemLayout);
    }

    private void init(Context context, AttributeSet attrs)
    {
        if (attrs != null)
        {
            TypedArray styled = context.obtainStyledAttributes(attrs, R.styleable.DiscussionView);
            listItemLayout = styled.getResourceId(R.styleable.DiscussionView_listItemLayout, 0);
            setTopicLayout(styled.getResourceId(R.styleable.DiscussionView_topicLayout, 0));
            styled.recycle();

            ensureStyle();
        }
    }

    protected void setTopicLayout(int topicLayout)
    {
        this.topicLayout = topicLayout;
    }

    private void ensureStyle()
    {
        if (listItemLayout == 0)
        {
            throw new IllegalStateException("listItemLayout should be set to a layout");
        }
    }

    protected View inflateDiscussionTopic()
    {
        View inflated = null;
        if (topicLayout != 0)
        {
            inflated = LayoutInflater.from(getContext()).inflate(topicLayout, null);
            topicView = inflated;

            if (topicView != null)
            {
                discussionList.addHeaderView(topicView);
            }
        }
        return inflated;
    }

    private void inflateDiscussionStatus()
    {
        View commentListStatusView = LayoutInflater.from(getContext()).inflate(R.layout.discussion_load_status, null);

        if (commentListStatusView != null)
        {
            discussionStatus = (TextView) commentListStatusView.findViewById(R.id.discussion_load_status);
            discussionList.addHeaderView(commentListStatusView);
        }
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        discussionList.setAdapter(discussionListAdapter);
        scrollListener = createFlagNearEndScrollListener();
        discussionList.setOnScrollListener(scrollListener);
        discussionList.setOnTouchListener(new OnTouchListener()
        {
            @Override public boolean onTouch(View v, MotionEvent event)
            {
                if (postCommentView != null)
                {
                    postCommentView.dismissKeypad();
                }
                return false;
            }
        });
        if (postCommentView != null)
        {
            postCommentView.setCommentPostedListener(createCommentPostedListener());
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        detachDiscussionListCache();
        if (postCommentView != null)
        {
            postCommentView.setCommentPostedListener(null);
        }
        discussionList.setAdapter(null);
        discussionList.setOnScrollListener(null);
        discussionList.setOnTouchListener(null);

        ButterKnife.reset(this);
        removeCallbacks(null);
        super.onDetachedFromWindow();
    }

    @Override public void display(DiscussionKey discussionKey)
    {
        linkWith(discussionKey, true);
    }

    protected void linkWith(DiscussionKey discussionKey, boolean andDisplay)
    {
        this.discussionKey = discussionKey;
        if (postCommentView != null)
        {
            postCommentView.linkWith(discussionKey);
        }

        initialFetchDiscussion(false);

        if (andDisplay)
        {
            displayTopicView();
        }
    }

    public void refresh()
    {
        if (discussionKey != null)
        {
            discussionListCache.invalidateAllPagesFor(discussionKey);
        }
        initialFetchDiscussion(true);
        if (topicView instanceof AbstractDiscussionCompactItemViewLinear)
        {
            ((AbstractDiscussionCompactItemViewLinear) topicView).refresh();
        }
    }

    protected void initialFetchDiscussion(boolean force)
    {
        this.startingDiscussionListKey = createStartingDiscussionListKey();
        if (startingDiscussionListKey != null)
        {
            fetchStartingDiscussionListIfNecessary(force);
        }
    }

    protected DiscussionListKey createStartingDiscussionListKey()
    {
        if (discussionKey != null)
        {
            return new PaginatedDiscussionListKey(DiscussionListKeyFactory.create(discussionKey), 1);
        }
        return null;
    }

    private void fetchStartingDiscussionListIfNecessary(boolean force)
    {
        setLoading();
        Timber.d("DiscussionListKey %s", startingDiscussionListKey);
        discussionListCacheSubscriptions.add(discussionListCache.get(startingDiscussionListKey)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createDiscussionListObserver()));
    }

    protected void detachDiscussionListCache()
    {
        for (Subscription subscription : discussionListCacheSubscriptions)
        {
            subscription.unsubscribe();
        }
        discussionListCacheSubscriptions.clear();
    }

    protected DiscussionListKey getNextDiscussionListKey(DiscussionKeyList latestDiscussionKeys)
    {
        return getNextDiscussionListKey(nextDiscussionListKey != null ? nextDiscussionListKey : startingDiscussionListKey,
                latestDiscussionKeys);
    }

    protected DiscussionListKey getNextDiscussionListKey(DiscussionListKey currentNext, DiscussionKeyList latestDiscussionKeys)
    {
        DiscussionListKey next = null;
        if (latestDiscussionKeys != null && !latestDiscussionKeys.isEmpty())
        {
            next = ((PaginatedDiscussionListKey) currentNext).next();
            if (next != null && next.equals(currentNext))
            {
                // This situation where next is equal to currentNext may happen
                // when the server is still returning the same values
                next = null;
            }
        }
        return next;
    }

    protected void fetchDiscussionListNextIfValid(DiscussionKeyList latestDiscussionKeys)
    {
        DiscussionListKey next = getNextDiscussionListKey(latestDiscussionKeys);
        if (next != null)
        {
            nextDiscussionListKey = next;
            fetchDiscussionListNext();
        }
    }

    protected void fetchDiscussionListNext()
    {
        if (nextDiscussionListKey != null)
        {
            discussionListCacheSubscriptions.add(discussionListCache.get(nextDiscussionListKey)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(createDiscussionListObserver()));
        }
    }

    protected DiscussionListKey getPrevDiscussionListKey(DiscussionKeyList latestDiscussionKeys)
    {
        return getPrevDiscussionListKey(prevDiscussionListKey != null ? prevDiscussionListKey : startingDiscussionListKey,
                latestDiscussionKeys);
    }

    protected DiscussionListKey getPrevDiscussionListKey(DiscussionListKey currentPrev, DiscussionKeyList latestDiscussionKeys)
    {
        DiscussionListKey prev = null;
        if (latestDiscussionKeys != null && !latestDiscussionKeys.isEmpty() &&
                ((PaginatedDiscussionListKey) currentPrev).page > 1)
        {
            prev = ((PaginatedDiscussionListKey) currentPrev).prev();
            if (prev != null && prev.equals(currentPrev))
            {
                // This situation where next is equal to currentNext may happen
                // when the server is still returning the same values
                prev = null;
            }
        }
        return prev;
    }

    protected void fetchDiscussionListPrevIfValid(DiscussionKeyList latestDiscussionKeys)
    {
        DiscussionListKey prev = getPrevDiscussionListKey(latestDiscussionKeys);
        if (prev != null)
        {
            prevDiscussionListKey = prev;
            fetchDiscussionListPrev();
        }
    }

    protected void fetchDiscussionListPrev()
    {
        if (prevDiscussionListKey != null)
        {
            discussionListCacheSubscriptions.add(discussionListCache.get(prevDiscussionListKey)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(createDiscussionListObserver()));
        }
    }

    protected void displayTopicView()
    {
        if (topicView instanceof DTOView)
        {
            try
            {
                //noinspection unchecked
                ((DTOView<DiscussionKey>) topicView).display(discussionKey);

                if (discussionListAdapter != null)
                {
                    discussionListAdapter.remove(discussionKey);
                }
            } catch (ClassCastException ex)
            {
                Timber.e(ex, "topicView should implement DTOView<DiscussionKey>");
            }
        }
    }

    private void linkWith(DiscussionKeyList discussionKeyList)
    {
        if (discussionKeyList != null)
        {
            // Anyway it will be reordered
            discussionListAdapter.appendTail(discussionKeyList);
            discussionListAdapter.notifyDataSetChanged();
        }

        discussionStatus.setText(R.string.discussion_loaded);
    }

    /**
     * This method is called when there is a new comment for the current discussion
     */
    protected void addComment(DiscussionDTO newDiscussion)
    {
        if (discussionKey != null)
        {
            displayTopicView();
        }

        if (discussionListAdapter != null)
        {
            discussionListAdapter.appendTail(newDiscussion);
            discussionListAdapter.notifyDataSetChanged();
        }
    }

    protected void setLoading()
    {
        if (discussionStatus != null)
        {
            discussionStatus.setText(R.string.discussion_loading);
        }
    }

    protected void setLoaded()
    {
        if (discussionStatus != null)
        {
            discussionStatus.setText(R.string.discussion_loaded);
        }
    }

    public void setCommentPostedListener(PrivatePostCommentView.CommentPostedListener commentPostedListener)
    {
        this.commentPostedListener = commentPostedListener;
    }

    private void notifyCommentPostedListener(DiscussionDTO discussionDTO)
    {
        PostCommentView.CommentPostedListener listener = commentPostedListener;
        if (listener != null)
        {
            listener.success(discussionDTO);
        }
    }

    private void notifyCommentPostFailedListener(Exception exception)
    {
        PostCommentView.CommentPostedListener listener = commentPostedListener;
        if (listener != null)
        {
            listener.failure(exception);
        }
    }

    protected PostCommentView.CommentPostedListener createCommentPostedListener()
    {
        return new DiscussionViewCommentPostedListener();
    }

    protected Observer<Pair<DiscussionListKey, PaginatedDTO<DiscussionDTO>>> createDiscussionListObserver()
    {
        return new Observer<Pair<DiscussionListKey, PaginatedDTO<DiscussionDTO>>>()
        {
            @Override public void onCompleted()
            {
            }

            @Override public void onError(Throwable e)
            {
                onFinish();
                THToast.show(new THException(e));
            }

            @Override public void onNext(Pair<DiscussionListKey, PaginatedDTO<DiscussionDTO>> pair)
            {
                onFinish();

                List<DiscussionDTO> list = pair.second.getData();
                if (list != null)
                {
                    DiscussionKeyList value = new DiscussionKeyList();
                    for (AbstractDiscussionDTO abstractDiscussionDTO : list)
                    {
                        value.add(abstractDiscussionDTO.getDiscussionKey());
                    }
                    linkWith(value);

                    if (pair.first.equals(startingDiscussionListKey))
                    {
                        handleStartingDTOReceived(pair.first, value);
                    }
                    else if (pair.first.equals(prevDiscussionListKey))
                    {
                        postHandlePrevDTOReceived(pair.first, value);
                    }
                    else if (pair.first.equals(nextDiscussionListKey))
                    {
                        postHandleNextDTOReceived(pair.first, value);
                    }
                }
            }
        };
    }

    private void onFinish()
    {
        setLoaded();
    }

    protected void handleStartingDTOReceived(DiscussionListKey key, DiscussionKeyList value)
    {
        fetchDiscussionListNextIfValid(value);
        fetchDiscussionListPrevIfValid(value);
    }

    protected void postHandleNextDTOReceived(final DiscussionListKey key, final DiscussionKeyList value)
    {
        post(new Runnable()
        {
            @Override public void run()
            {
                handleNextDTOReceived(key, value);
            }
        });
    }

    protected void handleNextDTOReceived(DiscussionListKey key, DiscussionKeyList value)
    {
        if (discussionList != null && discussionList.getLastVisiblePosition() == discussionListAdapter.getCount() - 1)
        {
            fetchDiscussionListNextIfValid(value);
        }
    }

    protected void postHandlePrevDTOReceived(final DiscussionListKey key, final DiscussionKeyList value)
    {
        post(new Runnable()
        {
            @Override public void run()
            {
                handlePrevDTOReceived(key, value);
            }
        });
    }

    protected void handlePrevDTOReceived(DiscussionListKey key, DiscussionKeyList value)
    {
        if (discussionList != null && discussionList.getFirstVisiblePosition() == 0)
        {
            fetchDiscussionListPrevIfValid(value);
        }
    }

    protected class DiscussionViewCommentPostedListener implements PostCommentView.CommentPostedListener
    {
        @Override public void success(DiscussionDTO discussionDTO)
        {
            addComment(discussionDTO);
            notifyCommentPostedListener(discussionDTO);
        }

        @Override public void failure(Exception exception)
        {
            THToast.show(R.string.error_unknown);
            notifyCommentPostFailedListener(exception);
        }
    }

    protected FlagNearEdgeScrollListener createFlagNearEndScrollListener()
    {
        return new DiscussionViewFlagNearEdgeScrollListener();
    }

    protected class DiscussionViewFlagNearEdgeScrollListener extends FlagNearEdgeScrollListener
    {
        public DiscussionViewFlagNearEdgeScrollListener()
        {
            super();
        }

        @Override public void raiseStartFlag()
        {
            super.raiseStartFlag();
            fetchDiscussionListPrev();
        }

        @Override public void raiseEndFlag()
        {
            super.raiseEndFlag();
            fetchDiscussionListNext();
        }
    }
}
