package com.tradehero.th.fragments.discussion;

import android.content.Context;
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
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

/**
 * Created by thonguyen on 10/4/14.
 */
public class DiscussionView extends FrameLayout
    implements DTOView<DiscussionKey>
{
    @InjectView(android.R.id.list) ListView discussionList;
    @InjectView(R.id.discussion_comment_widget) PostCommentView postCommentView;

    @Inject DiscussionCache discussionCache;

    private TextView discussionStatus;

    private DiscussionKey discussionKey;
    private AbstractDiscussionDTO discussionDTO;

    private DTOCache.Listener<DiscussionKey, AbstractDiscussionDTO> discussionFetchTaskListener;
    private DTOCache.GetOrFetchTask<DiscussionKey, AbstractDiscussionDTO> discussionFetchTask;

    //<editor-fold desc="Constructors">
    public DiscussionView(Context context)
    {
        super(context);
    }

    public DiscussionView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DiscussionView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();

        ButterKnife.inject(this);
        inflateDiscussionStatus();

        DaggerUtils.inject(this);

        discussionFetchTaskListener = new DiscussionFetchListener();
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
    }

    @Override protected void onDetachedFromWindow()
    {
        detachDiscussionFetchTask();

        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    @Override public void display(DiscussionKey discussionKey)
    {
        this.discussionKey = discussionKey;

        linkWith(discussionKey, true);
    }

    private void linkWith(DiscussionKey discussionKey, boolean andDisplay)
    {
        postCommentView.display(discussionKey);

        detachDiscussionFetchTask();
        discussionFetchTask = discussionCache.getOrFetch(discussionKey, false, discussionFetchTaskListener);

        if (andDisplay)
        {
        }
    }


    private void linkWith(AbstractDiscussionDTO abstractDiscussionDTO, boolean andDisplay)
    {
        this.discussionDTO = abstractDiscussionDTO;
    }

    private void detachDiscussionFetchTask()
    {
        if (discussionFetchTask != null)
        {
            discussionFetchTask.setListener(null);
        }
        discussionFetchTask = null;
    }


    private class DiscussionFetchListener implements DTOCache.Listener<DiscussionKey,AbstractDiscussionDTO>
    {
        @Override public void onDTOReceived(DiscussionKey key, AbstractDiscussionDTO value, boolean fromCache)
        {
            linkWith(value, true);
        }

        @Override public void onErrorThrown(DiscussionKey key, Throwable error)
        {
            THToast.show(new THException(error));
        }
    }
}
