package com.androidth.general.fragments.social.message;

import dagger.Module;

@Module(
        injects = {
                NewPrivateMessageFragment.class,
                ReplyPrivateMessageFragment.class,
                AbstractPrivateMessageFragment.class,
                PrivatePostCommentView.class,
        },
        library = true,
        complete = false
)
public class FragmentSocialMessageModule
{
}
