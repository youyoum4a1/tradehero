package com.tradehero.th.fragments.news;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import javax.inject.Inject;

public class ShareDialogFactory
{
    @Inject public ShareDialogFactory()
    {
        super();
    }

    public Dialog createShareDialog(Context context,
            AbstractDiscussionCompactDTO abstractDiscussionCompactDTO,
            ShareDialogLayout.OnShareMenuClickedListener menuClickedListener)
    {
        ShareDialogLayout contentView = (ShareDialogLayout) LayoutInflater.from(context)
                .inflate(R.layout.sharing_dialog_layout, null);
        contentView.setDiscussionToShare(abstractDiscussionCompactDTO);
        contentView.setMenuClickedListener(
                menuClickedListener);
        return createShareDialog(context, contentView);
    }

    public Dialog createShareDialog(Context context, ShareDialogLayout shareDialogLayout)
    {
        return THDialog.showUpDialog(context, shareDialogLayout);
    }
}
