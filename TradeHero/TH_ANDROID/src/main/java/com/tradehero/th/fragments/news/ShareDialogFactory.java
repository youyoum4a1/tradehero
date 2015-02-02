package com.tradehero.th.fragments.news;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.LayoutInflater;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import javax.inject.Inject;

public class ShareDialogFactory
{
    //<editor-fold desc="Constructors">
    @Inject public ShareDialogFactory()
    {
        super();
    }
    //</editor-fold>

    @NonNull public Pair<Dialog, ShareDialogLayout> createShareDialog(@NonNull Context context)
    {
        ShareDialogLayout contentView = (ShareDialogLayout) LayoutInflater.from(context)
                .inflate(R.layout.sharing_dialog_layout, null);
        return Pair.create(
                THDialog.showUpDialog(context, contentView),
                contentView);
    }
}
