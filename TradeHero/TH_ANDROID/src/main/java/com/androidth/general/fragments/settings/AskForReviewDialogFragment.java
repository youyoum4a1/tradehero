package com.androidth.general.fragments.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.androidth.general.R;
import com.androidth.general.activities.MarketUtil;
import com.androidth.general.fragments.base.BaseDialogFragment;

public class AskForReviewDialogFragment extends BaseDialogFragment
{
    @NonNull public static AskForReviewDialogFragment showReviewDialog(@NonNull FragmentManager fragmentManager)
    {
        AskForReviewDialogFragment dialogFragment = new AskForReviewDialogFragment();
        dialogFragment.show(fragmentManager, AskForReviewDialogFragment.class.getName());
        return dialogFragment;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.ask_for_review_dialog_layout, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
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
        MarketUtil.sendToReviewAllOnMarket(getActivity());
        dismiss();
    }
}
