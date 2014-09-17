package com.tradehero.th.fragments.social.message;

import dagger.Module;

/**
 * Created by tho on 9/9/2014.
 */
@Module(
        injects = {
                NewPrivateMessageFragment.class,
                ReplyPrivateMessageFragment.class,
                PrivateDiscussionView.class,
                PrivateDiscussionView.PrivateDiscussionViewDiscussionSetAdapter.class,
                PrivateMessageBubbleViewLinear.class,
                AbstractPrivateMessageFragment.class,
                PrivatePostCommentView.class,
        },
        library = true,
        complete = false
)
public class FragmentSocialMessageModule
{
}
