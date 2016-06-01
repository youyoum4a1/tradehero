package com.ayondo.academy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Menu;
import com.ayondo.academy.R;
import com.ayondo.academy.api.discussion.key.DiscussionKey;
import com.ayondo.academy.api.discussion.key.DiscussionKeyFactory;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.fragments.social.message.NewPrivateMessageFragment;
import com.ayondo.academy.fragments.social.message.ReplyPrivateMessageFragment;

public class PrivateDiscussionActivity extends OneFragmentActivity
{
    private static final String CORRESPONDENT_USER_BASE_BUNDLE_KEY = PrivateDiscussionActivity.class.getName() + ".correspondentUserBaseKey";
    private static final String DISCUSSION_KEY_BUNDLE_KEY = PrivateDiscussionActivity.class.getName() + ".discussionKey";

    //region Inflow bundling
    public static void putCorrespondentUserBaseKey(@NonNull Bundle args, @NonNull UserBaseKey correspondentBaseKey)
    {
        args.putBundle(CORRESPONDENT_USER_BASE_BUNDLE_KEY, correspondentBaseKey.getArgs());
    }

    @NonNull private static UserBaseKey collectCorrespondentId(@NonNull Intent startIntent)
    {
        return new UserBaseKey(startIntent.getExtras().getBundle(CORRESPONDENT_USER_BASE_BUNDLE_KEY));
    }

    public static void putDiscussionKey(@NonNull Bundle args, @NonNull DiscussionKey discussionKey)
    {
        args.putBundle(DISCUSSION_KEY_BUNDLE_KEY, discussionKey.getArgs());
    }

    @Nullable protected static DiscussionKey getDiscussionKey(@NonNull Intent startIntent)
    {
        if (startIntent.getExtras().containsKey(DISCUSSION_KEY_BUNDLE_KEY))
        {
            return DiscussionKeyFactory.fromBundle(startIntent.getExtras().getBundle(DISCUSSION_KEY_BUNDLE_KEY));
        }
        return null;
    }
    //endregion

    @NonNull @Override protected Class<? extends Fragment> getInitialFragment()
    {
        return getDiscussionKey(getIntent()) == null ? NewPrivateMessageFragment.class : ReplyPrivateMessageFragment.class;
    }

    @NonNull @Override protected Bundle getInitialBundle()
    {
        UserBaseKey correspondentId = collectCorrespondentId(getIntent());
        DiscussionKey discussionKey = getDiscussionKey(getIntent());
        Bundle args = super.getInitialBundle();
        if (discussionKey == null)
        {
            NewPrivateMessageFragment.putCorrespondentUserBaseKey(args, correspondentId);
        }
        else
        {
            ReplyPrivateMessageFragment.putCorrespondentUserBaseKey(args, correspondentId);
            ReplyPrivateMessageFragment.putDiscussionKey(args, discussionKey);
        }
        return args;
    }

    @Override public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.private_discussion_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
