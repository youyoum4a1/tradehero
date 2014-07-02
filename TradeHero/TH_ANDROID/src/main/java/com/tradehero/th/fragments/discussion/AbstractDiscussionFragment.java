package com.tradehero.th.fragments.discussion;

import android.os.Bundle;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionKeyFactory;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.thm.R;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class AbstractDiscussionFragment extends BasePurchaseManagerFragment
{
    private static final String DISCUSSION_KEY_BUNDLE_KEY = AbstractDiscussionFragment.class.getName() + ".discussionKey";

    @InjectView(R.id.discussion_view) protected DiscussionView discussionView;

    @Inject @NotNull protected DiscussionKeyFactory discussionKeyFactory;

    private DiscussionKey discussionKey;

    public static void putDiscussionKey(@NotNull Bundle args, @NotNull DiscussionKey discussionKey)
    {
        args.putBundle(DISCUSSION_KEY_BUNDLE_KEY, discussionKey.getArgs());
    }

    @Nullable private static DiscussionKey getDiscussionKey(@NotNull Bundle args, @NotNull DiscussionKeyFactory discussionKeyFactory)
    {
        if (args.containsKey(DISCUSSION_KEY_BUNDLE_KEY))
        {
            return discussionKeyFactory.fromBundle(args.getBundle(DISCUSSION_KEY_BUNDLE_KEY));
        }
        return null;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.discussionKey = getDiscussionKey(getArguments(), discussionKeyFactory);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        ButterKnife.inject(this, view);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override protected void initViews(View view)
    {
        if (discussionView != null)
        {
            discussionView.setCommentPostedListener(createCommentPostedListener());
        }
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onResume()
    {
        super.onResume();

        if (discussionKey != null)
        {
            linkWith(discussionKey, true);
        }
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

    abstract protected void handleCommentPosted(DiscussionDTO discussionDTO);

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
}
