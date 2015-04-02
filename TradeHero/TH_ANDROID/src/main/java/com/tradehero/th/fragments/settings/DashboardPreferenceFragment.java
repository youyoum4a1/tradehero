package com.tradehero.th.fragments.settings;

import android.view.MenuItem;
import android.widget.AbsListView;
import com.special.residemenu.ResideMenu;
import com.tradehero.th.BottomTabsQuickReturnListViewListener;
import dagger.Lazy;
import javax.inject.Inject;

public class DashboardPreferenceFragment extends BasePreferenceFragment
{
    @Inject Lazy<ResideMenu> resideMenuLazy;
    @Inject @BottomTabsQuickReturnListViewListener protected Lazy<AbsListView.OnScrollListener> dashboardBottomTabsScrollListener;

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                if (!actionBarOwnerMixin.shouldShowHomeAsUp())
                {
                    resideMenuLazy.get().openMenu();
                    return true;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
