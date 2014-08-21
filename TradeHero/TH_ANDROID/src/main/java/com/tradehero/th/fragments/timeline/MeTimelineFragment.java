package com.tradehero.th.fragments.timeline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.special.ResideMenu.ResideMenu;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.api.achievement.UserAchievementId;
import com.tradehero.th.api.level.LevelDefDTO;
import com.tradehero.th.api.level.LevelDefDTOList;
import com.tradehero.th.api.level.key.LevelDefListId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.achievement.AbstractAchievementDialogFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.persistence.achievement.UserAchievementCache;
import com.tradehero.th.persistence.level.LevelDefListCache;
import com.tradehero.th.persistence.user.UserProfileRetrievedMilestone;
import com.tradehero.th.utils.achievement.UserAchievementDTOUtil;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import dagger.Lazy;
import javax.inject.Inject;
import timber.log.Timber;

@Routable({
        "user/me", "profiles/me"
})
public class MeTimelineFragment extends TimelineFragment
    implements WithTutorial
{
    @Inject protected CurrentUserId currentUserId;
    @Inject Analytics analytics;
    @Inject Lazy<ResideMenu> resideMenuLazy;
    @Inject Lazy<AbstractAchievementDialogFragment.Creator> achievementDialogFragmentCreatorLazy;

    @Inject LevelDefListCache levelDefListCache;
    @Inject UserAchievementCache userAchievementCache;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.save(getArguments(), currentUserId.toUserBaseKey());
        Timber.d("MeTimelineFragment onCreate");

        LevelDefDTOList cached = levelDefListCache.get(new LevelDefListId());
        Timber.d("cached %s", cached);
    }

    @Override public void onResume()
    {
        super.onResume();
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.TabBar_Me));
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.timeline_menu, menu);
        displayActionBarTitle();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_edit:
                AbstractAchievementDialogFragment a = achievementDialogFragmentCreatorLazy.get().newInstance(new UserAchievementId(1));
                if (a != null)
                {
                    a.show(getFragmentManager(), "test");
                }

                //getDashboardNavigator().pushFragment(SettingsProfileFragment.class);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected void createUserProfileRetrievedMilestone()
    {
        userProfileRetrievedMilestone = new UserProfileRetrievedMilestone(currentUserId.toUserBaseKey());
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_timeline;
    }
}
