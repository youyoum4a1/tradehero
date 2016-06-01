package com.ayondo.academy.fragments.base;

import android.view.MenuItem;
import dagger.Lazy;
import javax.inject.Inject;

abstract public class DashboardFragment extends BaseFragment
{
    @Inject protected Lazy<FragmentOuterElements> fragmentElements;

    public boolean shouldShowLiveTradingToggle()
    {
        return false;
    }

    public void onLiveTradingChanged(boolean isLive)
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
