package com.tradehero.th.fragments.discussion;

import com.tradehero.th.fragments.discussion.stock.SecurityDiscussionCommentFragment;
import com.tradehero.th.fragments.discussion.stock.SecurityDiscussionFragment;
import com.tradehero.th.fragments.discussion.stock.SecurityDiscussionItemViewLinear;
import com.tradehero.th.fragments.discussion.stock.SecurityDiscussionView;
import dagger.Module;

/**
 * Created by tho on 9/9/2014.
 */
@Module(
        injects = {
                AbstractDiscussionCompactItemViewLinear.class,
                DiscussionItemViewLinear.class,
                AbstractDiscussionCompactItemViewHolder.class,
                AbstractDiscussionItemViewHolder.class,
                DiscussionItemViewHolder.class,
                TimelineItemViewHolder.class,
                SingleViewDiscussionSetAdapter.class,
                DiscussionSetAdapter.class,
                PrivateDiscussionSetAdapter.class,
                AbstractDiscussionFragment.class,

                SecurityDiscussionFragment.class,
                SecurityDiscussionView.class,
                SecurityDiscussionFragment.class,
                SecurityDiscussionItemViewLinear.class,
                SecurityDiscussionCommentFragment.class,

                DiscussionView.class,
                PostCommentView.class,
                NewsDiscussionView.class,

                DiscussionEditPostFragment.class,
                SecurityDiscussionEditPostFragment.class,
                DiscussionPostActionButtonsView.class,
                TransactionEditCommentFragment.class,
                MentionActionButtonsView.class,

                TimelineDiscussionFragment.class,
                NewsDiscussionFragment.class,
                CommentItemViewLinear.class
        },
        library = true,
        complete = false
)
public class FragmentDiscussionModule
{
}
