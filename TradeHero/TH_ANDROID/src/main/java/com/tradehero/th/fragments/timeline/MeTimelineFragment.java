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
import com.tradehero.th.fragments.achievement.AchievementDialogFragment;
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
    @Inject Lazy<AchievementDialogFragment.Creator> achievementDialogFragmentCreatorLazy;

    @Inject LevelDefListCache levelDefListCache;
    @Inject UserAchievementCache userAchievementCache;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.save(getArguments(), currentUserId.toUserBaseKey());
        Timber.d("MeTimelineFragment onCreate");

        LevelDefDTO mockLevel1 = new LevelDefDTO();
        mockLevel1.id = 1;
        mockLevel1.level = 1;
        mockLevel1.xpFrom = 0;
        mockLevel1.xpTo = 1000;

        LevelDefDTO mockLevel2 = new LevelDefDTO();
        mockLevel2.id = 2;
        mockLevel2.level = 2;
        mockLevel2.xpFrom = 1001;
        mockLevel2.xpTo = 2000;

        LevelDefDTO mockLevel3 = new LevelDefDTO();
        mockLevel3.id = 3;
        mockLevel3.level = 3;
        mockLevel3.xpFrom = 2001;
        mockLevel3.xpTo = 3000;

        LevelDefDTOList levelDefDTOList = new LevelDefDTOList();
        levelDefDTOList.add(mockLevel1);
        levelDefDTOList.add(mockLevel3);
        levelDefDTOList.add(mockLevel2);

        //levelDefListCache.put(new LevelDefListId(), levelDefDTOList);
        userAchievementCache.put(new UserAchievementId(1), UserAchievementDTOUtil.dummy());
        LevelDefDTOList cached = levelDefListCache.get(new LevelDefListId());
        Timber.d("cached %s", cached);
    }

    @Override public void onResume()
    {
        super.onResume();

        analytics.addEvent(new SimpleEvent(AnalyticsConstants.TabBar_Me));
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
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
                AchievementDialogFragment a = achievementDialogFragmentCreatorLazy.get().newInstance(new UserAchievementId(1));
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
