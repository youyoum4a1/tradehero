package com.androidth.general.fragments.news;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.LayoutInflater;
import com.androidth.general.common.widget.dialog.THDialog;
import com.androidth.general.R;

public class NewsDialogFactory extends ShareDialogFactory
{
    @NonNull public static Pair<Dialog, NewsDialogLayout> createNewsDialogRx(@NonNull Context context)
    {
        NewsDialogLayout contentView = (NewsDialogLayout) LayoutInflater.from(context)
                .inflate(R.layout.sharing_translation_dialog_layout, null);
        return Pair.create(
                THDialog.showUpDialog(context, contentView),
                contentView);
    }
}
