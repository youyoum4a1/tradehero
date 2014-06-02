package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.SocialShareResultDTO;
import com.tradehero.th.api.translation.TranslationResult;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.share.SocialShareTranslationHelper;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;

abstract public class AbstractDiscussionCompactItemView<T extends DiscussionKey>
        extends LinearLayout
        implements DTOView<T>
{
    @Inject protected DiscussionCache discussionCache;
    @Inject protected SocialShareTranslationHelper socialShareHelper;
    protected AbstractDiscussionCompactItemViewHolder viewHolder;
    protected T discussionKey;
    protected AbstractDiscussionCompactDTO abstractDiscussionCompactDTO;

    private DTOCache.GetOrFetchTask<DiscussionKey, AbstractDiscussionCompactDTO> discussionFetchTask;

    //<editor-fold desc="Constructors">
    public AbstractDiscussionCompactItemView(Context context)
    {
        super(context);
    }

    public AbstractDiscussionCompactItemView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AbstractDiscussionCompactItemView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        viewHolder = createViewHolder();
        ButterKnife.inject(viewHolder, this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(viewHolder, this);
        viewHolder.linkWith(abstractDiscussionCompactDTO, true);
        viewHolder.setMenuClickedListener(createViewHolderMenuClickedListener());
    }

    @Override protected void onDetachedFromWindow()
    {
        detachFetchDiscussionTask();
        viewHolder.setMenuClickedListener(null);
        ButterKnife.reset(viewHolder);
        super.onDetachedFromWindow();
    }

    protected AbstractDiscussionCompactItemViewHolder createViewHolder()
    {
        return new AbstractDiscussionCompactItemViewHolder<AbstractDiscussionCompactDTO>();
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

    protected void linkWith(AbstractDiscussionCompactDTO abstractDiscussionDTO, boolean andDisplay)
    {
        this.abstractDiscussionCompactDTO = abstractDiscussionDTO;
        viewHolder.linkWith(abstractDiscussionDTO, andDisplay);
        if (andDisplay)
        {
        }
    }

    protected DTOCache.Listener<DiscussionKey, AbstractDiscussionCompactDTO> createDiscussionFetchListener()
    {
        return new DiscussionFetchListener();
    }

    private class DiscussionFetchListener
            implements DTOCache.Listener<DiscussionKey, AbstractDiscussionCompactDTO>
    {
        @Override
        public void onDTOReceived(DiscussionKey key, AbstractDiscussionCompactDTO value, boolean fromCache)
        {
            linkWith(value, true);
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

    protected AbstractDiscussionCompactItemViewHolder.OnMenuClickedListener createViewHolderMenuClickedListener()
    {
        return new AbstractDiscussionViewHolderClickedListener()
        {
            @Override public void onShareButtonClicked()
            {
                // Nothing to do
            }

            @Override public void onCommentButtonClicked()
            {
                // Nothing to do
            }

            @Override public void onUserClicked(UserBaseKey userClicked)
            {
                // Nothing to do
            }
        };
    }

    abstract protected class AbstractDiscussionViewHolderClickedListener implements AbstractDiscussionItemViewHolder.OnMenuClickedListener
    {
        @Override public void onTranslationRequested()
        {
            socialShareHelper.translate(abstractDiscussionCompactDTO);
        }

        @Override public void onMoreButtonClicked()
        {
            socialShareHelper.shareOrTranslate(abstractDiscussionCompactDTO);
        }
    }

    protected SocialShareTranslationHelper.OnMenuClickedListener createSocialShareMenuClickedListener()
    {
        return new AbstractDiscussionItemViewShareTranslationMenuClickListener();
    }

    protected class AbstractDiscussionItemViewShareTranslationMenuClickListener implements SocialShareTranslationHelper.OnMenuClickedListener
    {
        @Override public void onCancelClicked()
        {
        }

        @Override public void onShareRequestedClicked(SocialShareFormDTO socialShareFormDTO)
        {
        }

        @Override public void onConnectRequired(SocialShareFormDTO shareFormDTO)
        {
        }

        @Override public void onShared(SocialShareFormDTO shareFormDTO,
                SocialShareResultDTO socialShareResultDTO)
        {
        }

        @Override public void onShareFailed(SocialShareFormDTO shareFormDTO, Throwable throwable)
        {
        }

        @Override public void onTranslationClicked(AbstractDiscussionCompactDTO toTranslate)
        {
        }

        @Override public void onTranslatedOneAttribute(AbstractDiscussionCompactDTO toTranslate,
                TranslationResult translationResult)
        {
            viewHolder.setLatestTranslationResult(translationResult);
        }

        @Override public void onTranslatedAllAtributes(AbstractDiscussionCompactDTO toTranslate,
                AbstractDiscussionCompactDTO translated)
        {
            viewHolder.linkWithTranslated(translated, true);
        }

        @Override public void onTranslateFailed(AbstractDiscussionCompactDTO toTranslate,
                Throwable error)
        {
        }
    }
}
