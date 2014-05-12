package com.tradehero.th.ui;

import com.special.ResideMenu.ResideMenu;
import com.tradehero.common.utils.THToast;
import javax.inject.Inject;


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
