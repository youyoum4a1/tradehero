package com.tradehero.th.models.social;

import android.app.AlertDialog;
import com.tradehero.th.fragments.social.FollowDialogView;

public class FollowDialogCombo
{
    public final AlertDialog alertDialog;
    public final FollowDialogView followDialogView;

    //<editor-fold desc="Constructors">
    public FollowDialogCombo(AlertDialog alertDialog, FollowDialogView followDialogView)
    {
        this.alertDialog = alertDialog;
        this.followDialogView = followDialogView;
    }
    //</editor-fold>
}
