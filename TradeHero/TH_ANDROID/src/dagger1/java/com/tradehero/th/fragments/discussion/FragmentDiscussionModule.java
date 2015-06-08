package com.tradehero.th.fragments.discussion;

import com.tradehero.th.fragments.discussion.stock.SecurityDiscussionCommentFragment;
import com.tradehero.th.fragments.discussion.stock.SecurityDiscussionFragment;
import com.tradehero.th.fragments.discussion.stock.SecurityDiscussionItemViewLinear;
import dagger.Module;

@Module(
        injects = {
                AbstractDiscussionCompactItemViewLinear.class,
                DiscussionItemViewLinear.class,
                AbstractDiscussionCompactItemViewHolder.class,
                AbstractDiscussionItemViewHolder.class,
                DiscussionItemViewHolder.class,
                TimelineItemViewHolder.class,
                AbstractDiscussionFragment.class,

                SecurityDiscussionFragment.class,
                SecurityDiscussionFragment.class,
                SecurityDiscussionItemViewLinear.class,
                SecurityDiscussionCommentFragment.class,

                PostCommentView.class,

                DiscussionEditPostFragment.class,
                SecurityDiscussionEditPostFragment.class,
                DiscussionPostActionButtonsView.class,
                TransactionEditCommentFragment.class,
                MentionActionButtonsView.class,

                TimelineDiscussionFragment.class,
                NewsDiscussionFragment.class,
        },
        library = true,
        complete = false
)
public class FragmentDiscussionModule
{
}
