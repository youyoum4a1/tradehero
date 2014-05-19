package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.discussion.AbstractDiscussionDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.utils.AlertDialogUtil;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import javax.inject.Provider;
import org.ocpsoft.prettytime.PrettyTime;

public class AbstractDiscussionItemView<T extends DiscussionKey>
        extends LinearLayout
        implements DTOView<T>
{
    @Inject DiscussionCache discussionCache;
    @Inject Provider<PrettyTime> prettyTime;
    @Inject protected AlertDialogUtil alertDialogUtil;
    protected AbstractDiscussionItemViewHolder viewHolder;
    protected T discussionKey;
    protected AbstractDiscussionDTO abstractDiscussionDTO;

    private DTOCache.GetOrFetchTask<DiscussionKey, AbstractDiscussionDTO> discussionFetchTask;

    //<editor-fold desc="Constructors">
    public AbstractDiscussionItemView(Context context)
    {
        super(context);
    }

    public AbstractDiscussionItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AbstractDiscussionItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        viewHolder = createViewHolder();
        viewHolder.initView(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (abstractDiscussionDTO != null)
        {
            viewHolder.linkWith(abstractDiscussionDTO, true);
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        detachFetchDiscussionTask();
        super.onDetachedFromWindow();
    }

    protected AbstractDiscussionItemViewHolder createViewHolder()
    {
        return new AbstractDiscussionItemViewHolder();
    }

    @Override public void display(T discussionKey)
    {
        this.discussionKey = discussionKey;

        fetchDiscussionDetail(false);
    }

    public void refresh()
    {
        fetchDiscussionDetail(true);
    }

    private void fetchDiscussionDetail(boolean force)
    {
        detachFetchDiscussionTask();

        discussionFetchTask =
                discussionCache.getOrFetch(discussionKey, force, createDiscussionFetchListener());
        discussionFetchTask.execute();
    }

    private void detachFetchDiscussionTask()
    {
        if (discussionFetchTask != null)
        {
            discussionFetchTask.setListener(null);
        }
        discussionFetchTask = null;
    }

    public void display(AbstractDiscussionDTO abstractDiscussionDTO)
    {
        linkWith(abstractDiscussionDTO, true);
    }

    protected void linkWith(AbstractDiscussionDTO abstractDiscussionDTO, boolean andDisplay)
    {
        this.abstractDiscussionDTO = abstractDiscussionDTO;
        if (viewHolder != null)
        {
            viewHolder.linkWith(abstractDiscussionDTO, andDisplay);
        }
        if (andDisplay)
        {
        }
    }

    protected DTOCache.Listener<DiscussionKey, AbstractDiscussionDTO> createDiscussionFetchListener()
    {
        return new DiscussionFetchListener();
    }

    private class DiscussionFetchListener
            implements DTOCache.Listener<DiscussionKey, AbstractDiscussionDTO>
    {
        @Override
        public void onDTOReceived(DiscussionKey key, AbstractDiscussionDTO value, boolean fromCache)
        {
            display(value);
        }

        @Override public void onErrorThrown(DiscussionKey key, Throwable error)
        {
            THToast.show(new THException(error));
        }
    }

    //<editor-fold desc="Navigation">
    protected DashboardNavigator getNavigator()
    {
        return ((DashboardNavigatorActivity) getContext()).getDashboardNavigator();
    }
    //</editor-fold>
}
