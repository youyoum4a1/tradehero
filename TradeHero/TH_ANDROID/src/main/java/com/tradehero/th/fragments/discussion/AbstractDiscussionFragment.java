package com.tradehero.th.fragments.discussion;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;
import com.tradehero.common.fragment.HasSelectedItem;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.BottomTabs;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionKeyFactory;
import com.tradehero.th.fragments.DashboardTabHost;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.misc.exception.THException;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;

abstract public class AbstractDiscussionFragment extends BasePurchaseManagerFragment
{
    private static final String DISCUSSION_KEY_BUNDLE_KEY = AbstractDiscussionFragment.class.getName() + ".discussionKey";

    @InjectView(R.id.discussion_view) protected DiscussionView discussionView;
    @InjectView(R.id.post_comment_text) @Optional protected EditText postCommentText;
    @InjectView(R.id.mention_widget) @Optional protected MentionActionButtonsView mentionActionButtonsView;

    @Inject @BottomTabs protected Lazy<DashboardTabHost> dashboardTabHost;
    @Inject protected MentionTaggedStockHandler mentionTaggedStockHandler;

    private DiscussionKey discussionKey;
    private Subscription hasSelectedSubscription;

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
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        discussionView.discussionList.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());
        mentionTaggedStockHandler.setDiscussionPostContent(postCommentText);
        subscribeHasSelected();
        if (discussionView != null)
        {
            discussionView.setCommentPostedListener(createCommentPostedListener());
        }
    }

    @Override public void onResume()
    {
        super.onResume();

        if (discussionKey != null)
        {
            linkWith(discussionKey, true);
        }

        dashboardTabHost.get().setOnTranslate(new DashboardTabHost.OnTranslateListener()
        {
            @Override public void onTranslate(float x, float y)
            {
                if (discussionView.postCommentView != null)
                {
                    discussionView.postCommentView.setTranslationY(y);
                }
            }
        });
        mentionTaggedStockHandler.collectSelection();
    }

    @Override public void onPause()
    {
        dashboardTabHost.get().setOnTranslate(null);
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        detachSelectedSubscription();
        if (discussionView != null)
        {
            discussionView.discussionList.setOnScrollListener(null);
        }
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

    public DiscussionKey getDiscussionKey()
    {
        return discussionKey;
    }

    protected void linkWith(DiscussionKey discussionKey, boolean andDisplay)
    {
        this.discussionKey = discussionKey;
        if (andDisplay && discussionView != null)
        {
            discussionView.display(discussionKey);
        }
    }

    private void subscribeHasSelected()
    {
        detachSelectedSubscription();
        if (mentionActionButtonsView != null)
        {
            hasSelectedSubscription = mentionActionButtonsView.getSelectedItemObservable()
                    .subscribe(createSelectedItemObserver());
        }
    }

    private void detachSelectedSubscription()
    {
        Subscription hasSelectedSubscriptionCopy = hasSelectedSubscription;
        if (hasSelectedSubscriptionCopy != null)
        {
            hasSelectedSubscriptionCopy.unsubscribe();
        }
        hasSelectedSubscription = null;
    }

    private Observer<HasSelectedItem> createSelectedItemObserver()
    {
        return new Observer<HasSelectedItem>()
        {
            @Override public void onCompleted()
            {
                // do nothing
            }

            @Override public void onError(Throwable e)
            {
                THToast.show(new THException(e));
            }

            @Override public void onNext(HasSelectedItem hasSelectedItem)
            {
                mentionTaggedStockHandler.setHasSelectedItemFragment(hasSelectedItem);
            }
        };
    }

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
}
