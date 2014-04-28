package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionKeyList;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionKeyFactory;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.discussion.key.DiscussionListKeyFactory;
import com.tradehero.th.api.discussion.key.PaginatedDiscussionListKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.social.message.PrivatePostCommentView;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.discussion.DiscussionListCache;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.DeviceUtil;
import java.util.ArrayList;
import javax.inject.Inject;
import timber.log.Timber;

public class DiscussionView extends FrameLayout
    implements DTOView<DiscussionKey>
{
    @InjectView(android.R.id.list) protected ListView discussionList;
    @InjectView(R.id.discussion_comment_widget) protected PostCommentView postCommentView;

    private int listItemLayout;
    private int topicLayout;

    @Inject protected CurrentUserId currentUserId;
    @Inject protected DiscussionListCache discussionListCache;
    @Inject protected DiscussionCache discussionCache;
    @Inject protected DiscussionKeyFactory discussionKeyFactory;
    @Inject protected DiscussionListKeyFactory discussionListKeyFactory;

    protected TextView discussionStatus;
    private DiscussionKey discussionKey;

    private PostCommentView.CommentPostedListener commentPostedListener;

    private DTOCache.GetOrFetchTask<DiscussionListKey, DiscussionKeyList> discussionFetchTask;
    protected DiscussionSetAdapter discussionListAdapter;
    private DiscussionListKey discussionListKey;
    private int nextPageDelta;
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
        return new SingleViewDiscussionSetAdapter(getContext(), LayoutInflater.from(getContext()), listItemLayout);
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
        postCommentView.setCommentPostedListener(createCommentPostedListener());
        DeviceUtil.showKeyboard(getContext());
    }

    @Override protected void onDetachedFromWindow()
    {
        detachDiscussionFetchTask();
        postCommentView.setCommentPostedListener(null);
        discussionList.setAdapter(null);

        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @Override public void display(DiscussionKey discussionKey)
    {
        linkWith(discussionKey, true);
    }

    protected void linkWith(DiscussionKey discussionKey, boolean andDisplay)
    {
        this.discussionKey = discussionKey;
        postCommentView.linkWith(discussionKey);

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
        this.discussionListKey = createListKey();
        if (discussionListKey != null)
        {
            fetchDiscussionListIfNecessary(force);
        }
    }

    public int getNextPageDelta()
    {
        return nextPageDelta;
    }

    public DiscussionListKey getDiscussionListKey()
    {
        return discussionListKey;
    }

    protected void setDiscussionListKey(DiscussionListKey discussionListKey)
    {
        this.discussionListKey = discussionListKey;
    }

    protected DiscussionListKey createListKey()
    {
        if (discussionKey != null)
        {
            return new PaginatedDiscussionListKey(discussionListKeyFactory.create(discussionKey), 1);
        }
        return null;
    }

    private void fetchDiscussionListIfNecessary(boolean force)
    {
        prepareDiscussionListKey();
        setLoading();
        detachDiscussionFetchTask();
        Timber.d("DiscussionListKey %s", discussionListKey);
        discussionFetchTask = discussionListCache.getOrFetch(discussionListKey, force, createDiscussionListListener());
        discussionFetchTask.execute();
    }

    protected DiscussionListKey getNextDiscussionListKey(DiscussionKeyList latest)
    {
        if (discussionListKey != null && latest != null && !latest.isEmpty())
        {
            return ((PaginatedDiscussionListKey) discussionListKey).next(1);
        }
        return null;
    }

    protected DiscussionListKey getPrevDiscussionListKey(DiscussionKeyList latest)
    {
        if (discussionListKey != null && latest != null && !latest.isEmpty() &&
                ((PaginatedDiscussionListKey) discussionListKey).page > 1)
        {
            return ((PaginatedDiscussionListKey) discussionListKey).next(-1);
        }
        return null;
    }

    protected void prepareDiscussionListKey()
    {
        if (discussionListKey instanceof PaginatedDiscussionListKey && nextPageDelta >= 0)
        {
            discussionListKey = ((PaginatedDiscussionListKey) discussionListKey).next(nextPageDelta);
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
            nextPageDelta = discussionKeyList.isEmpty() ? -1 : 1;

            // Most recent at bottom
            discussionListAdapter.appendHead(discussionKeyList);
            discussionListAdapter.notifyDataSetChanged();
            if (nextPageDelta > 0)
            {
                //fetchDiscussionListIfNecessary(false);
            }
        }

        if (andDisplay)
        {
            discussionStatus.setText(R.string.discussion_loaded);
        }
    }

    protected DTOCache.Listener<DiscussionListKey, DiscussionKeyList> createDiscussionListListener()
    {
        return new DiscussionFetchListener();
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
            ArrayList<DiscussionKey> newElement = new ArrayList<>();
            newElement.add(newDiscussionKey);
            discussionListAdapter.appendTail(newElement);
            discussionCache.put(newDiscussionKey, newDiscussion);
            discussionListAdapter.notifyDataSetChanged();
        }
        discussionListCache.invalidateAllPagesFor(discussionKey);
    }

    private void updateCommentCount()
    {
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

    private void detachDiscussionFetchTask()
    {
        if (discussionFetchTask != null)
        {
            discussionFetchTask.setListener(null);
        }
        discussionFetchTask = null;
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

    private class DiscussionFetchListener implements DTOCache.Listener<DiscussionListKey, DiscussionKeyList>
    {
        @Override public void onDTOReceived(DiscussionListKey key, DiscussionKeyList value, boolean fromCache)
        {
            onFinish();

            linkWith(value, true);
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
}
