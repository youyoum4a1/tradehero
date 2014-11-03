package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.widget.LinearLayout;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.SocialShareResultDTO;
import com.tradehero.th.api.translation.TranslationResult;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.models.share.SocialShareTranslationHelper;
import com.tradehero.th.persistence.discussion.DiscussionCacheRx;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

abstract public class AbstractDiscussionCompactItemViewLinear<T extends DiscussionKey>
        extends LinearLayout
        implements DTOView<T>
{
    @Inject protected DiscussionCacheRx discussionCache;
    @Inject protected SocialShareTranslationHelper socialShareHelper;
    @Inject DashboardNavigator dashboardNavigator;

    protected AbstractDiscussionCompactItemViewHolder viewHolder;
    protected T discussionKey;
    protected AbstractDiscussionCompactDTO abstractDiscussionCompactDTO;

    private Subscription discussionFetchSubscription;

    //<editor-fold desc="Constructors">
    public AbstractDiscussionCompactItemViewLinear(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        if (!isInEditMode())
        {
            viewHolder = createViewHolder();
            viewHolder.onFinishInflate(this);
            socialShareHelper.setMenuClickedListener(createSocialShareMenuClickedListener());
        }
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        if (!isInEditMode())
        {
            viewHolder.onAttachedToWindow(this);
            viewHolder.linkWith(abstractDiscussionCompactDTO, true);
            viewHolder.setMenuClickedListener(createViewHolderMenuClickedListener());
            socialShareHelper.setMenuClickedListener(createSocialShareMenuClickedListener());
        }
    }

    @Override protected void onDetachedFromWindow()
    {
        detachFetchDiscussionTask();
        socialShareHelper.onDetach();
        viewHolder.setMenuClickedListener(null);
        viewHolder.onDetachedFromWindow();
        discussionFetchSubscription = null;
        super.onDetachedFromWindow();
    }

    protected AbstractDiscussionCompactItemViewHolder createViewHolder()
    {
        return new AbstractDiscussionCompactItemViewHolder<AbstractDiscussionCompactDTO>(getContext());
    }

    @Override public void display(T discussionKey)
    {
        this.discussionKey = discussionKey;

        fetchDiscussionDetail();
    }

    public void refresh()
    {
        fetchDiscussionDetail();
    }

    private void fetchDiscussionDetail()
    {
        detachFetchDiscussionTask();
        discussionFetchSubscription = discussionCache.get(discussionKey)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createDiscussionFetchObserver());
    }

    private void detachFetchDiscussionTask()
    {
        Subscription copy = discussionFetchSubscription;
        if (copy != null)
        {
            copy.unsubscribe();
        }
        discussionFetchSubscription = null;
    }

    protected void linkWith(AbstractDiscussionCompactDTO abstractDiscussionDTO, boolean andDisplay)
    {
        this.abstractDiscussionCompactDTO = abstractDiscussionDTO;
        viewHolder.linkWith(abstractDiscussionDTO, andDisplay);
        if (andDisplay)
        {
        }
    }

    protected DashboardNavigator getNavigator()
    {
        return dashboardNavigator;
    }

    @NotNull protected Observer<Pair<DiscussionKey, AbstractDiscussionCompactDTO>> createDiscussionFetchObserver()
    {
        return new DiscussionFetchObserver();
    }

    private class DiscussionFetchObserver
            implements Observer<Pair<DiscussionKey, AbstractDiscussionCompactDTO>>
    {
        @Override public void onNext(Pair<DiscussionKey, AbstractDiscussionCompactDTO> pair)
        {
            linkWith(pair.second, true);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(new THException(e));
        }
    }

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

            @Override public void onTranslationRequested()
            {
                // Nothing to do
            }
        };
    }

    abstract protected class AbstractDiscussionViewHolderClickedListener implements AbstractDiscussionItemViewHolder.OnMenuClickedListener
    {
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
        }

        @Override public void onTranslatedAllAtributes(AbstractDiscussionCompactDTO toTranslate,
                AbstractDiscussionCompactDTO translated)
        {
        }

        @Override public void onTranslateFailed(AbstractDiscussionCompactDTO toTranslate,
                Throwable error)
        {
        }
    }
}
