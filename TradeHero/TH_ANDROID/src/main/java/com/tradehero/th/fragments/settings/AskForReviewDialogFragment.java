package com.tradehero.th.fragments.settings;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.activities.MarketUtil;
import com.tradehero.th.fragments.base.BaseDialogFragment;
import javax.inject.Inject;
import android.support.annotation.NonNull;

public class AskForReviewDialogFragment extends BaseDialogFragment
{
    @Inject MarketUtil marketUtil;

    @NonNull public static AskForReviewDialogFragment showReviewDialog(FragmentManager fragmentManager)
    {
        AskForReviewDialogFragment dialogFragment = new AskForReviewDialogFragment();
        dialogFragment.show(fragmentManager, AskForReviewDialogFragment.class.getName());
        return dialogFragment;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.ask_for_review_dialog_layout, container, false);
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick({R.id.btn_later, R.id.btn_cancel})
    public void onLater()
    {
        dismiss();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.btn_rate)
    public void onRate()
    {
        marketUtil.sendToReviewAllOnMarket(getActivity());
        dismiss();
    }
}
