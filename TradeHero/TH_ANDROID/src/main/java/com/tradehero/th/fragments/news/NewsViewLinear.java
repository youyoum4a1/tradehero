package com.tradehero.th.fragments.news;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.key.NewsItemDTOKey;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import com.tradehero.th.fragments.discussion.DiscussionActionButtonsView;
import com.tradehero.th.fragments.discussion.DiscussionEditPostFragment;
import com.tradehero.th.fragments.trade.BuySellStockFragment;
import com.tradehero.th.fragments.web.WebViewFragment;
import rx.Observable;

public class NewsViewLinear extends AbstractDiscussionCompactItemViewLinear<NewsItemDTOKey>
{
    //<editor-fold desc="Constructors">
    public NewsViewLinear(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }
    //</editor-fold>

    @NonNull @Override protected NewsItemViewHolder createViewHolder()
    {
        return new NewsItemViewHolder<>(getContext());
    }

    @Override protected void fetchDiscussionDetail()
    {
        refresh(); // Just to make sure we get the complete NewsItemDTO if it was not fetched yet.
    }

    public void setTitleBackground(int resId)
    {
        if (viewHolder != null)
        {
            viewHolder.setBackgroundResource(resId);
        }
    }

    @NonNull @Override protected Observable<DiscussionActionButtonsView.UserAction> handleUserAction(
            DiscussionActionButtonsView.UserAction userAction)
    {
        if (userAction instanceof DiscussionActionButtonsView.CommentUserAction)
        {
            pushNewDiscussion();
            return Observable.empty();
        }
        if (userAction instanceof NewsItemViewHolder.OpenWebUserAction)
        {
            pushWebFragment();
            return Observable.empty();
        }
        if (userAction instanceof NewsItemViewHolder.SecurityUserAction)
        {
            pushBuySellFragment(((NewsItemViewHolder.SecurityUserAction) userAction).securityId);
            return Observable.empty();
        }
        return super.handleUserAction(userAction);
    }

    protected void pushWebFragment()
    {
        if (abstractDiscussionCompactDTO != null
                && ((NewsItemCompactDTO) abstractDiscussionCompactDTO).url != null)
        {
            Bundle bundle = new Bundle();
            WebViewFragment.putUrl(bundle, ((NewsItemCompactDTO) abstractDiscussionCompactDTO).url);
            getNavigator().pushFragment(WebViewFragment.class, bundle);
        }
    }

    protected void pushBuySellFragment(SecurityId securityId)
    {
        Bundle args = new Bundle();
        BuySellStockFragment.putSecurityId(args, securityId);
        getNavigator().pushFragment(BuySellStockFragment.class, args);
    }

    protected void pushNewDiscussion()
    {
        Bundle bundle = new Bundle();
        bundle.putBundle(DiscussionKey.BUNDLE_KEY_DISCUSSION_KEY_BUNDLE, discussionKey.getArgs());
        getNavigator().pushFragment(DiscussionEditPostFragment.class, bundle);
    }
}
