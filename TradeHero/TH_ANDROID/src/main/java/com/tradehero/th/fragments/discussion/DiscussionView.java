package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
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
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.social.message.PrivatePostCommentView;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.discussion.DiscussionListCacheNew;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import timber.log.Timber;

public class DiscussionView extends FrameLayout
    implements DTOView<DiscussionKey>, DiscussionListCacheNew.DiscussionKeyListListener
{
    @InjectView(android.R.id.list) protected ListView discussionList;
    protected FlagNearEdgeScrollListener scrollListener;
    @InjectView(R.id.discussion_comment_widget) @Optional protected PostCommentView postCommentView;

    private int listItemLayout;
    private int topicLayout;

    @Inject protected CurrentUserId currentUserId;
    @Inject protected DiscussionListCacheNew discussionListCache;
    @Inject protected DiscussionCache discussionCache;
    @Inject protected DiscussionListKeyFactory discussionListKeyFactory;

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
        super(context, attrs);
        init(attrs);
    }

    public DiscussionView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(attrs);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);

        inflateDiscussionTopic();
        inflateDiscussionStatus();

        DaggerUtils.inject(this);

        discussionListAdapter = createDiscussionListAdapter();
    }

    protected DiscussionSetAdapter createDiscussionListAdapter()
    {
        return new SingleViewDiscussionSetAdapter(getContext(), listItemLayout);
    }

    private void init(AttributeSet attrs)
    {
        if (attrs != null)
        {
            TypedArray styled = getContext().obtainStyledAttributes(attrs, R.styleable.DiscussionView);
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
        discussionListCache.unregister(this);
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
        if (topicView instanceof AbstractDiscussionItemView)
        {
            ((AbstractDiscussionItemView) topicView).refresh();
        }
    }

    protected void initialFetchDiscussion(boolean force)
    {
        discussionListAdapter = createDiscussionListAdapter();
        discussionList.setAdapter(discussionListAdapter);
        discussionListCache.unregister(this);
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
            return new PaginatedDiscussionListKey(discussionListKeyFactory.create(discussionKey), 1);
        }
        return null;
    }

    private void fetchStartingDiscussionListIfNecessary(boolean force)
    {
        setLoading();
        Timber.d("DiscussionListKey %s", startingDiscussionListKey);
        discussionListCache.register(startingDiscussionListKey, this);
        discussionListCache.getOrFetchAsync(startingDiscussionListKey, force);
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
            discussionListCache.register(nextDiscussionListKey, this);
            discussionListCache.getOrFetchAsync(nextDiscussionListKey, false);
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
            discussionListCache.register(prevDiscussionListKey, this);
            discussionListCache.getOrFetchAsync(prevDiscussionListKey, false);
        }
    }

    protected void displayTopicView()
    {
        if (topicView instanceof DTOView)
        {
            try
            {
                ((DTOView<DiscussionKey>) topicView).display(discussionKey);
            }
            catch (Exception ex)
            {
                Timber.e(ex, "topicView should implement DTOView<DiscussionKey>");
            }
        }
    }

    private void linkWith(DiscussionKeyList discussionKeyList, boolean andDisplay)
    {
        if (discussionKeyList != null)
        {
            // Anyway it will be reordered
            discussionListAdapter.appendTail(discussionKeyList);
            discussionListAdapter.notifyDataSetChanged();
        }

        if (andDisplay)
        {
            discussionStatus.setText(R.string.discussion_loaded);
        }
    }

    /**
     * This method is called when there is a new comment for the current discussion
     * @param newDiscussion
     */
    protected void addComment(DiscussionDTO newDiscussion)
    {
        DiscussionKey newDiscussionKey = newDiscussion.getDiscussionKey();
        updateCommentCount();

        if (discussionListAdapter != null)
        {
            discussionCache.put(newDiscussionKey, newDiscussion);
            discussionListAdapter.appendTail(newDiscussion);
            discussionListAdapter.notifyDataSetChanged();
        }
        discussionListCache.invalidateAllPagesFor(discussionKey);
    }

    private void updateCommentCount()
    {
        // TODO review in light of the stubKey
        if (discussionKey != null)
        {
            AbstractDiscussionDTO discussionDTO = discussionCache.get(discussionKey);
            if (discussionDTO != null)
            {
                ++discussionDTO.commentCount;
                displayTopicView();
            }
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

    @Override public void onDTOReceived(DiscussionListKey key, DiscussionKeyList value, boolean fromCache)
    {
        onFinish();

        linkWith(value, true);

        if (key.equals(startingDiscussionListKey))
        {
            handleStartingDTOReceived(key, value, fromCache);
        }
        else if (key.equals(prevDiscussionListKey))
        {
            postHandlePrevDTOReceived(key, value, fromCache);
        }
        else if (key.equals(nextDiscussionListKey))
        {
            postHandleNextDTOReceived(key, value, fromCache);
        }
    }

    @Override public void onErrorThrown(DiscussionListKey key, Throwable error)
    {
        onFinish();

        THToast.show(new THException(error));
    }

    private void onFinish()
    {
        setLoaded();
    }

    protected void handleStartingDTOReceived(DiscussionListKey key, DiscussionKeyList value, boolean fromCache)
    {
        fetchDiscussionListNextIfValid(value);
        fetchDiscussionListPrevIfValid(value);
    }

    protected void postHandleNextDTOReceived(final DiscussionListKey key, final DiscussionKeyList value, final boolean fromCache)
    {
        post(new Runnable()
        {
            @Override public void run()
            {
                handleNextDTOReceived(key, value, fromCache);
            }
        });
    }

    protected void handleNextDTOReceived(DiscussionListKey key, DiscussionKeyList value, boolean fromCache)
    {
        if (discussionList.getLastVisiblePosition() == discussionListAdapter.getCount() - 1)
        {
            fetchDiscussionListNextIfValid(value);
        }
    }

    protected void postHandlePrevDTOReceived(final DiscussionListKey key, final DiscussionKeyList value, final boolean fromCache)
    {
        post(new Runnable()
        {
            @Override public void run()
            {
                handlePrevDTOReceived(key, value, fromCache);
            }
        });
    }

    protected void handlePrevDTOReceived(DiscussionListKey key, DiscussionKeyList value, boolean fromCache)
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
