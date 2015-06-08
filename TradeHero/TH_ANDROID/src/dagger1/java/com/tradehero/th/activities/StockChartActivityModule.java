package com.tradehero.th.activities;

import com.etiennelawlor.quickreturn.library.views.NotifyingScrollView;
import com.tradehero.th.BottomTabsQuickReturnScrollViewListener;
import com.tradehero.th.UIModule;
import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
                UIModule.class
        },
        library = true,
        complete = false
) class StockChartActivityModule
{
    @Provides @BottomTabsQuickReturnScrollViewListener NotifyingScrollView.OnScrollChangedListener provideQuickReturnListViewOnScrollListener()
    {
        return null;
    }
}
