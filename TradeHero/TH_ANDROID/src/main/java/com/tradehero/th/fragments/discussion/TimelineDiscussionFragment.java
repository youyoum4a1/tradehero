package com.tradehero.th.fragments.discussion;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;
import javax.inject.Inject;

public class TimelineDiscussionFragment extends AbstractDiscussionFragment
{
    @SuppressWarnings("UnusedDeclaration") @Inject Context doNotRemoveOrItFails;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.timeline_discussion, container, false);
    }

    @Override protected void handleCommentPosted(DiscussionDTO discussionDTO)
    {
        // Nothing to do
    }

    @Override public <T extends Fragment> boolean allowNavigateTo(@NonNull Class<T> fragmentClass, Bundle args)
    {
        boolean basicCheck = !this.getClass().isAssignableFrom(fragmentClass) && super.allowNavigateTo(fragmentClass, args);
        boolean sameKeyCheck = getDiscussionKey() != TimelineDiscussionFragment.getDiscussionKey(args, discussionKeyFactory);
        return basicCheck && sameKeyCheck;
    }
}
