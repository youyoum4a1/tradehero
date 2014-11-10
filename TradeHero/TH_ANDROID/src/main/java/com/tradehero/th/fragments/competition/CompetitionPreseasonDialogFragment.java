package com.tradehero.th.fragments.competition;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.BaseShareableDialogFragment;
import com.tradehero.th.widget.MarkdownTextView;
import javax.inject.Inject;

public class CompetitionPreseasonDialogFragment extends BaseShareableDialogFragment
{
    public static final String TAG = CompetitionPreseasonDialogFragment.class.getName();

    @Inject Context doNotRemove;

    @InjectView(R.id.preseason_share) Button btnShare;
    @InjectView(R.id.preseason_title_image) ImageView imgTitle;
    @InjectView(R.id.preseason_prize_image) ImageView imgPrize;
    @InjectView(R.id.preseason_prize_description) MarkdownTextView textDescription;
    @InjectView(R.id.preseason_prize_tncs) Button btnTncs;

    public static CompetitionPreseasonDialogFragment newInstance()
    {
        return new CompetitionPreseasonDialogFragment();
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.competition_preseason_dialog, container, false);
    }

    @OnClick(R.id.close)
    public void onCloseClicked()
    {
        getDialog().dismiss();
    }

}
