package com.tradehero.th.fragments.achievement;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.fragments.base.BaseDialogFragment;
import com.tradehero.th.models.level.LevelDTO;
import com.tradehero.th.widget.UserLevelProgressBar;

public class AchievementDialogFragment extends BaseDialogFragment
{
    @InjectView(R.id.achievement_content_container) ViewGroup contentContainer;

    @InjectView(R.id.achievement_header) TextView header;
    @InjectView(R.id.achievement_title) TextView title;
    @InjectView(R.id.achievement_description) TextView description;
    @InjectView(R.id.achievement_more_description) TextView moreDescription;

    @InjectView(R.id.achievement_badge) ImageView badge;
    @InjectView(R.id.achievement_pulse) ImageView pulseEffect;

    @InjectView(R.id.user_level_progress_bar) UserLevelProgressBar userLevelProgressBar;

    @InjectView(R.id.btn_achievement_dismiss) Button btnDismiss;
    @InjectView(R.id.btn_achievement_share) Button btnShare;

    @InjectView(R.id.user_level_progress_level_up) TextView levelUp;
    @InjectView(R.id.user_level_progress_xp_earned) TextView xpEarned;
    @InjectView(R.id.user_level_progress_virtual_dollar_earned) TextView dollarEarned;

    private boolean mShouldDismissOnOutsideClicked;

    @Override public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog d = super.onCreateDialog(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.TH_Achievement_Dialog);
        d.getWindow().setWindowAnimations(R.style.TH_Achievement_Dialog_Animation);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        d.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        d.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        mShouldDismissOnOutsideClicked = true; //TODO
        userLevelProgressBar.setUserLevelProgressBarListener(createUserLevelProgressBarListener());
    }

    @Override public void onResume()
    {
        super.onResume();
        contentContainer.setOnTouchListener(
                new AchievementDialogSwipeDismissTouchListener(contentContainer, null,
                        new AchievementDialogSwipeDismissTouchListener.DismissCallbacks()
                        {
                            @Override public boolean canDismiss(Object token)
                            {
                                return true;
                            }

                            @Override public void onDismiss(View view, Object token)
                            {
                                removeDialogAnimation();
                                getDialog().dismiss();
                            }
                        }));
    }

    private void removeDialogAnimation()
    {
        getDialog().getWindow().setWindowAnimations(R.style.TH_Achievement_Dialog_NoAnimation);
    }

    @Override public void onPause()
    {
        contentContainer.setOnTouchListener(null);
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        userLevelProgressBar.setUserLevelProgressBarListener(null);
        super.onDestroyView();
    }

    public void setShouldDismissOnOutsideClicked(boolean mShouldDismissOnOutsideClicked)
    {
        this.mShouldDismissOnOutsideClicked = mShouldDismissOnOutsideClicked;
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

    @OnClick(R.id.achievement_dummy_container)
    public void onOutsideContentClicked()
    {
        if (mShouldDismissOnOutsideClicked)
        {
            getDialog().dismiss();
        }
    }

    private UserLevelProgressBar.UserLevelProgressBarListener createUserLevelProgressBarListener()
    {
        return new AchievementUserLevelProgressBarListener();
    }

    protected class AchievementUserLevelProgressBarListener implements UserLevelProgressBar.UserLevelProgressBarListener
    {
        @Override public void onLevelUp(LevelDTO fromLevel, LevelDTO toLevel)
        {
            Animation a = AnimationUtils.loadAnimation(getActivity(), R.anim.achievement_level_up);
            levelUp.setVisibility(View.VISIBLE);
            a.setAnimationListener(new Animation.AnimationListener()
                                   {
                                       @Override public void onAnimationStart(Animation animation)
                                       {

                                       }

                                       @Override public void onAnimationEnd(Animation animation)
                                       {
                                            levelUp.setVisibility(View.GONE);
                                       }

                                       @Override public void onAnimationRepeat(Animation animation)
                                       {

                                       }
                                   }
            );
            levelUp.startAnimation(a);
        }
    }
}
