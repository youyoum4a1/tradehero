package com.tradehero.th.fragments.settings;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tradehero.th.R;
import com.tradehero.th.activities.MarketUtil;
import com.tradehero.th.fragments.base.BaseDialogFragment;
import com.tradehero.th.persistence.prefs.ShowAskForReviewDialog;
import com.tradehero.th.persistence.timing.TimingIntervalPreference;

import javax.inject.Inject;

import butterknife.OnClick;

public class AskForReviewDialogFragment extends BaseDialogFragment
{
    @Inject MarketUtil marketUtil;
    @Inject @ShowAskForReviewDialog TimingIntervalPreference mShowAskForReviewDialogPreference;

    public static AskForReviewDialogFragment showReviewDialog(FragmentManager fragmentManager)
    {
        AskForReviewDialogFragment dialogFragment = new AskForReviewDialogFragment();
        dialogFragment.show(fragmentManager, AskForReviewDialogFragment.class.getName());
        return dialogFragment;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(BaseDialogFragment.STYLE_NO_TITLE, R.style.TH_Dialog);
        setCancelable(false);
        mShowAskForReviewDialogPreference.pushInFuture(TimingIntervalPreference.MINUTE);
    }

    @Override public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog d = super.onCreateDialog(savedInstanceState);
        d.getWindow().setWindowAnimations(R.style.TH_BuySellDialogAnimation);
        return d;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.ask_for_review_dialog_layout, container, false);
    }

    @OnClick(R.id.btn_cancel)
    public void onCancel()
    {
        dismiss();
        mShowAskForReviewDialogPreference.justHandled();
    }

    @OnClick(R.id.btn_later)
    public void onLater()
    {
        dismiss();
        mShowAskForReviewDialogPreference.pushInFuture(TimingIntervalPreference.DAY);
    }

    @OnClick(R.id.btn_rate)
    public void onRate()
    {
        rate();
        dismiss();
        mShowAskForReviewDialogPreference.justHandled();
    }

    private void rate()
    {
        marketUtil.sendToReviewAllOnMarket(getActivity());
    }
}
