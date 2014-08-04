package com.tradehero.th.fragments.discussion;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;
import org.jetbrains.annotations.NotNull;

public class TimelineDiscussionFragment extends AbstractDiscussionFragment
{
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.timeline_discussion, container, false);
        return view;
    }

    @Override public void onDestroyView()
    {
        super.onDestroyView();
    }

    @Override protected void handleCommentPosted(DiscussionDTO discussionDTO)
    {
        // Nothing to do
    }

    @Override public <T extends Fragment> boolean allowNavigateTo(@NotNull Class<T> fragmentClass, Bundle args)
    {
        boolean basicCheck = !this.getClass().isAssignableFrom(fragmentClass) && super.allowNavigateTo(fragmentClass, args);
        boolean sameKeyCheck = getDiscussionKey() != TimelineDiscussionFragment.getDiscussionKey(args, discussionKeyFactory);
        return basicCheck && sameKeyCheck;
    }
}
