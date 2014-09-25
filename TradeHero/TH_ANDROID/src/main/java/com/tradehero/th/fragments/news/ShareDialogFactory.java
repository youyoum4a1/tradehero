package com.tradehero.th.fragments.news;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * @param abstractDiscussionCompactDTO
     * @param menuClickedListener
     * @return
     */
    public Dialog createShareDialog(@NotNull Context context,
            @NotNull AbstractDiscussionCompactDTO abstractDiscussionCompactDTO,
            @Nullable ShareDialogLayout.OnShareMenuClickedListener menuClickedListener)
    {
        ShareDialogLayout contentView = (ShareDialogLayout) LayoutInflater.from(context)
                .inflate(R.layout.sharing_dialog_layout, null);
        contentView.setDiscussionToShare(abstractDiscussionCompactDTO);
        contentView.setMenuClickedListener(
                menuClickedListener);
        return THDialog.showUpDialog(context, contentView);
    }
}
