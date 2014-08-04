package com.tradehero.th.fragments.achievement;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.BaseDialogFragment;
import com.tradehero.th.widget.UserLevelProgressBar;

public class AchievementDialogFragment extends BaseDialogFragment
{
    @InjectView(R.id.achievement_header) TextView header;
    @InjectView(R.id.achievement_title) TextView title;
    @InjectView(R.id.achievement_description) TextView description;
    @InjectView(R.id.achievement_more_description) TextView moreDescription;

    @InjectView(R.id.achievement_badge) ImageView badge;
    @InjectView(R.id.achievement_pulse) ImageView pulseEffect;

    @InjectView(R.id.user_level_progress_bar) UserLevelProgressBar userLevelProgressBar;

    @InjectView(R.id.btn_achievement_dismiss) Button btnDismiss;
    @InjectView(R.id.btn_achievement_share) Button btnShare;

    @Override public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog d = super.onCreateDialog(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, getTheme());
        //d.getWindow().setWindowAnimations(R.style.detailDialogAnimation); //TODO
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return d;
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.achievement_dialog_fragment, container, false);
        return v;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @OnClick(R.id.btn_achievement_share)
    public void onShareClicked()
    {
    }

    @OnClick(R.id.btn_achievement_dismiss)
    public void onDismissBtnClicked()
    {
        getDialog().dismiss();
    }
}
