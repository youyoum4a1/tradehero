package com.tradehero.th.fragments.base;

import android.view.MenuItem;
import com.tradehero.th.widget.OffOnViewSwitcherEvent;
import dagger.Lazy;
import javax.inject.Inject;

abstract public class DashboardFragment extends BaseFragment
{
    @Inject protected Lazy<FragmentOuterElements> fragmentElements;

    public boolean shouldShowLiveTradingToggle()
    {
        return false;
    }

    public boolean shouldHandleLiveColor()
    {
        return false;
    }

    public void onLiveTradingChanged(OffOnViewSwitcherEvent event)
    {
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                if (!actionBarOwnerMixin.shouldShowHomeAsUp())
                {
                    fragmentElements.get().onOptionItemsSelected(item);
                    return true;
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
