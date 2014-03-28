package com.tradehero.th.ui;

import com.special.ResideMenu.ResideMenu;
import com.tradehero.common.utils.THToast;
import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA. User: tho Date: 3/5/14 Time: 4:12 PM Copyright (c) TradeHero
 */
public class ResideMenuListener implements ResideMenu.OnMenuListener
{
    @Inject public ResideMenuListener()
    {
        super();
    }

    @Override public void openMenu()
    {
        THToast.show("Menu is opened!");
    }

    @Override public void closeMenu()
    {
        THToast.show("Menu is closed!");
    }
}
