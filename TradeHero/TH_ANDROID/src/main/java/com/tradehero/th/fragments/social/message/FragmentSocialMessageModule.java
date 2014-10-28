package com.tradehero.th.fragments.social.message;

import dagger.Module;

@Module(
        injects = {
                NewPrivateMessageFragment.class,
                ReplyPrivateMessageFragment.class,
                PrivateDiscussionView.class,
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
