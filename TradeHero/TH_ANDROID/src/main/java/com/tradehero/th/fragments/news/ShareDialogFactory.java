package com.tradehero.th.fragments.news;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import com.tradehero.common.persistence.DTO;
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

    /**
     * You can access the view from the dialog with
     * dialog.getWindow().getDecorView().findViewById(android.R.id.content);
     * @param context
     * @param whatToShare
     * @param menuClickedListener
     * @return
     */
    @NonNull public Dialog createShareDialog(
            @NonNull Context context,
            @NonNull DTO whatToShare,
            @Nullable ShareDialogLayout.OnShareMenuClickedListener menuClickedListener)
    {
        ShareDialogLayout contentView = (ShareDialogLayout) LayoutInflater.from(context)
                .inflate(R.layout.sharing_dialog_layout, null);
        contentView.setWhatToShare(whatToShare);
        contentView.setMenuClickedListener(
                menuClickedListener);
        return THDialog.showUpDialog(context, contentView);
    }
}
