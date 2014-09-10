package com.tradehero.th.fragments.settings;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tradehero.common.persistence.prefs.LongPreference;
import com.tradehero.th.R;
import com.tradehero.th.activities.MarketUtil;
import com.tradehero.th.fragments.base.BaseDialogFragment;
import com.tradehero.th.persistence.prefs.ShowAskForReviewDialog;
import com.tradehero.th.utils.AlertDialogUtil;

import javax.inject.Inject;

import butterknife.OnClick;

public class AskForReviewDialogFragment extends BaseDialogFragment
{
    static public long ONE_MIN = 60*1000;
    static public long ONE_DAY = 24*60*60*1000;
    static public long ONE_YEAR = (long)365*24*60*60*1000;

    @Inject AlertDialogUtil alertDialogUtil;
    @Inject MarketUtil marketUtil;
    @Inject @ShowAskForReviewDialog LongPreference mShowAskForReviewDialogPreference;

    public static AskForReviewDialogFragment showReviewDialog(FragmentManager fragmentManager)
    {
        AskForReviewDialogFragment dialogFragment = new AskForReviewDialogFragment();
        dialogFragment.show(fragmentManager, AskForReviewDialogFragment.class.getName());
        return dialogFragment;
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setStyle(BaseDialogFragment.STYLE_NO_FRAME, getTheme());
        setCancelable(false);
        mShowAskForReviewDialogPreference.set(System.currentTimeMillis() + ONE_MIN);
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
        mShowAskForReviewDialogPreference.set(System.currentTimeMillis()+ONE_YEAR);
    }

    @OnClick(R.id.btn_later)
    public void onLater()
    {
        dismiss();
        mShowAskForReviewDialogPreference.set(System.currentTimeMillis()+ONE_DAY);
    }

    @OnClick(R.id.btn_rate)
    public void onRate()
    {
        rate();
        dismiss();
        mShowAskForReviewDialogPreference.set((long)System.currentTimeMillis() + ONE_YEAR);
    }

    private void rate()
    {
        marketUtil.sendToReviewAllOnMarket(getActivity());
    }
}
