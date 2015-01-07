package com.tradehero.th.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.th.R;
import javax.inject.Inject;

public class AlertDialogRxUtil
{
    @NonNull protected final VersionUtils versionUtils;

    //<editor-fold desc="Constructors">
    @Inject public AlertDialogRxUtil(@NonNull VersionUtils versionUtils)
    {
        super();
        this.versionUtils = versionUtils;
    }
    //</editor-fold>

    @NonNull public AlertDialog.Builder createDefaultDialogBuilder(@NonNull Context activityContext)
    {
        return new AlertDialog.Builder(activityContext)
                .setIcon(R.drawable.th_app_logo)
                .setCancelable(true);
    }
}
