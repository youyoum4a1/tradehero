package com.tradehero.th.fragments.timeline;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.persistence.DTOCacheUtilImpl;
import javax.inject.Inject;

@Routable({
        "user/me", "profiles/me"
})
public class MeTimelineFragment extends TimelineFragment
        implements WithTutorial
{
    @Inject protected CurrentUserId currentUserId;
    @Inject DTOCacheUtilImpl dtoCacheUtil;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.save(getArguments(), currentUserId.toUserBaseKey());
    }

    @Override public void onResume()
    {
        super.onResume();
        dtoCacheUtil.anonymousPrefetches();
        dtoCacheUtil.initialPrefetches();
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        displayActionBarTitle();
        setActionBarSubtitle(null);
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_timeline;
    }

    @Override protected void fetchMessageThreadHeader()
    {
        // Nothing to do
    }
}
