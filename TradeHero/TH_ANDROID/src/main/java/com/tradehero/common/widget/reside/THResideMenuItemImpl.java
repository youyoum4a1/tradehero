package com.tradehero.common.widget.reside;

import android.content.Context;
import com.special.residemenu.ResideMenuItem;

public class THResideMenuItemImpl extends ResideMenuItem
    implements THResideMenuItem
{
    //<editor-fold desc="Constructors">
    public THResideMenuItemImpl(Context context)
    {
        super(context);
    }

    public THResideMenuItemImpl(Context context, int icon, int title)
    {
        super(context, icon, title);
    }

    public THResideMenuItemImpl(Context context, int icon, String title)
    {
        super(context, icon, title);
    }
    //</editor-fold>
}
