package com.tradehero.th.fragments.base;

import android.view.MenuItem;
import android.widget.AbsListView;
import com.etiennelawlor.quickreturn.library.views.NotifyingScrollView;
import com.special.residemenu.ResideMenu;
import com.tradehero.th.BottomTabsQuickReturnListViewListener;
import com.tradehero.th.BottomTabsQuickReturnScrollViewListener;
import dagger.Lazy;
import javax.inject.Inject;

abstract public class DashboardFragment extends BaseFragment
{
    @Inject Lazy<ResideMenu> resideMenuLazy;

    @Inject @BottomTabsQuickReturnListViewListener protected Lazy<AbsListView.OnScrollListener> dashboardBottomTabsListViewScrollListener;
    @Inject @BottomTabsQuickReturnScrollViewListener protected Lazy<NotifyingScrollView.OnScrollChangedListener>
            dashboardBottomTabScrollViewScrollListener;

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
