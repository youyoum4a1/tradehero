package com.tradehero.th.fragments.news;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.LayoutInflater;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import javax.inject.Inject;

public class NewsDialogFactory extends ShareDialogFactory
{
    //<editor-fold desc="Constructors">
    @Inject public NewsDialogFactory()
    {
    }
    //</editor-fold>

    @NonNull public Pair<Dialog, NewsDialogLayout> createNewsDialogRx(@NonNull Context context)
    {
        NewsDialogLayout contentView = (NewsDialogLayout) LayoutInflater.from(context)
                .inflate(R.layout.sharing_translation_dialog_layout, null);
        return Pair.create(
                THDialog.showUpDialog(context, contentView),
                contentView);
    }
}
