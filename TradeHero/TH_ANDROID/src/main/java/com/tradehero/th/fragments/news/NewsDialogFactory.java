package com.tradehero.th.fragments.news;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import com.tradehero.thm.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import javax.inject.Inject;

public class NewsDialogFactory extends ShareDialogFactory
{
    @Inject public NewsDialogFactory()
    {
    }

    public Dialog createNewsDialog(Context context,
            AbstractDiscussionCompactDTO abstractDiscussionCompactDTO,
            NewsDialogLayout.OnMenuClickedListener menuClickedListener)
    {
        NewsDialogLayout contentView = (NewsDialogLayout) LayoutInflater.from(context)
                .inflate(R.layout.sharing_translation_dialog_layout, null);
        contentView.setDiscussionToShare(abstractDiscussionCompactDTO);
        contentView.setMenuClickedListener(
                menuClickedListener);
        return createShareDialog(context, contentView);
    }
}
