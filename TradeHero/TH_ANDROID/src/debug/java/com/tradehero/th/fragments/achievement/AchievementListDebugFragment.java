package com.tradehero.th.fragments.achievement;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.tradehero.th.R;

public class AchievementListDebugFragment extends AchievementListFragment
{
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.achievement_testing, menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_test_achievement:
                navigator.pushFragment(AchievementListTestingFragment.class, null);
                break;
            case R.id.menu_test_achievement_daily:
                navigator.pushFragment(QuestListTestingFragment.class, null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
