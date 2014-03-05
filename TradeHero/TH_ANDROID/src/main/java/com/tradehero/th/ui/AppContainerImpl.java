package com.tradehero.th.ui;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.fragments.dashboard.DashboardTabType;
import javax.inject.Inject;

import static butterknife.ButterKnife.findById;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/5/14 Time: 4:07 PM Copyright (c) TradeHero
 */
public class AppContainerImpl implements AppContainer
{
    private ResideMenu.OnMenuListener menuListener;

    @Inject public AppContainerImpl(ResideMenuListener menuListener)
    {
        this.menuListener = menuListener;
    }

    @Override public ViewGroup get(Activity activity)
    {
        return findById(activity, android.R.id.content);
    }
}
