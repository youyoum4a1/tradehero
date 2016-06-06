package com.androidth.general.fragments.settings;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import com.androidth.general.persistence.prefs.ShowAskForReviewDialog;
import com.androidth.general.persistence.timing.TimingIntervalPreference;
import com.androidth.general.utils.broadcast.BroadcastUtils;
import javax.inject.Inject;

public class AskForReviewSuggestedDialogFragment extends AskForReviewDialogFragment
{
    private static final long DELAY_WHEN_IGNORE = TimingIntervalPreference.DAY;

    @Inject @ShowAskForReviewDialog TimingIntervalPreference mShowAskForReviewDialogPreference;
    @Inject BroadcastUtils broadcastUtils;

    @NonNull public static AskForReviewSuggestedDialogFragment showReviewDialog(@NonNull FragmentManager fragmentManager)
    {
        AskForReviewSuggestedDialogFragment dialogFragment = new AskForReviewSuggestedDialogFragment();
        dialogFragment.show(fragmentManager, AskForReviewSuggestedDialogFragment.class.getName());
        return dialogFragment;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (!mShowAskForReviewDialogPreference.isItTime())
        {
            dismiss();
        }
    }

    @Override public void onLater()
    {
        mShowAskForReviewDialogPreference.pushInFuture(DELAY_WHEN_IGNORE);
        super.onLater();
    }

    @Override public void onRate()
    {
        super.onRate();
        mShowAskForReviewDialogPreference.justHandled();
    }

    @Override public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
        broadcastUtils.nextPlease();
    }
}
