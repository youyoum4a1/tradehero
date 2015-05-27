package com.tradehero.th.fragments.timeline;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.activities.UpdateCenterActivity;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.models.number.THSignedNumber;
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

    TextView unreadCountView;

    @Nullable @Override protected UserBaseKey getShownUserBaseKey()
    {
        return currentUserId.toUserBaseKey();
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
        setActionBarSubtitle(null);
        inflater.inflate(R.menu.me_timeline_menu, menu);

        View unreadActionView = menu.findItem(R.id.btn_notification).getActionView();
        unreadCountView = (TextView) unreadActionView.findViewById(R.id.unread_count);
        displayUnreadCount();
        unreadActionView.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                navigator.get().launchActivity(UpdateCenterActivity.class);
            }
        });
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.btn_notification)
        {
            thRouter.open("notifications");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_timeline;
    }

    @Override protected void fetchMessageThreadHeader()
    {
        // Nothing to do
    }

    @Override protected void linkWith(@NonNull UserProfileDTO userProfileDTO)
    {
        super.linkWith(userProfileDTO);
        displayUnreadCount();
    }

    protected void displayUnreadCount()
    {
        if (shownProfile != null && unreadCountView != null)
        {
            unreadCountView.setVisibility(shownProfile.unreadNotificationsCount > 0 ? View.VISIBLE : View.GONE);
            unreadCountView.setText(THSignedNumber.builder(shownProfile.unreadNotificationsCount)
                    .build().toString());
        }
    }
}
