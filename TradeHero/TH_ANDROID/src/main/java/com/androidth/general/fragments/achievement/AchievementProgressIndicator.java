package com.androidth.general.fragments.achievement;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.BindView;
import com.androidth.general.R;
import com.androidth.general.api.achievement.AchievementDefDTO;
import java.util.List;

public class AchievementProgressIndicator extends LinearLayout
{
    @BindView(R.id.achievement_indicator_image_container) ViewGroup imageContainer;
    @BindView(R.id.achievement_indicator_text_container) ViewGroup textContainer;

    SparseArray<ViewHolder> indicatorLists = new SparseArray<>();
    private int mCurrentLevel;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public AchievementProgressIndicator(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public AchievementProgressIndicator(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public AchievementProgressIndicator(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.bind(this);
        findIndicators();
    }

    private void findIndicators()
    {
        String indicatorTag = getContext().getString(R.string.tag_achievement_indicator);
        int index = 0;
        for (int i = 0; i < imageContainer.getChildCount(); i++)
        {
            View v = imageContainer.getChildAt(i);
            if (v.getTag() != null && v.getTag().equals(indicatorTag) && v instanceof ImageView)
            {
                addImageIndicator(index, (ImageView) v);
                index++;
            }
        }
        index = 0;
        for (int i = 0; i < textContainer.getChildCount(); i++)
        {
            View v = textContainer.getChildAt(i);
            if (v.getTag() != null && v.getTag().equals(indicatorTag) && v instanceof TextView)
            {
                addTextIndicator(index, (TextView) v);
                index++;
            }
        }
    }

    private void addImageIndicator(int index, ImageView view)
    {
        ViewHolder viewHolder = getViewHolder(index);
        viewHolder.indicatorImageView = view;
        setViewHolder(index, viewHolder);
    }

    private void addTextIndicator(int index, TextView textView)
    {
        ViewHolder viewHolder = getViewHolder(index);
        viewHolder.indicatorTextView = textView;
        setViewHolder(index, viewHolder);
    }

    private ViewHolder getViewHolder(int index)
    {
        ViewHolder viewHolder = indicatorLists.get(index);
        if (viewHolder == null)
        {
            viewHolder = new ViewHolder();
        }
        return viewHolder;
    }

    private void setViewHolder(int index, ViewHolder viewHolder)
    {
        indicatorLists.put(index, viewHolder);
    }

    public void setAchievementDef(List<AchievementDefDTO> achievementDefs, int currentUserLevel)
    {
        this.mCurrentLevel = currentUserLevel;
        updateDisplay(achievementDefs);
        hideUndefinedIndicators(achievementDefs);
    }

    private void updateDisplay(List<AchievementDefDTO> achievementDefs)
    {
        for (int i = 0; i < achievementDefs.size() && i < indicatorLists.size(); i++)
        {
            ViewHolder viewHolder = indicatorLists.get(i);
            AchievementDefDTO achievementDefDTO = achievementDefs.get(i);

            viewHolder.display(achievementDefDTO, mCurrentLevel);
        }
    }

    private void hideUndefinedIndicators(List<AchievementDefDTO> achievementDefs)
    {
        if (achievementDefs.size() < indicatorLists.size())
        {
            for (int i = achievementDefs.size(); i < indicatorLists.size(); i++)
            {
                ViewHolder viewHolder = indicatorLists.get(i);
                viewHolder.hide();
            }
        }
    }

    public void animateCurrentLevel()
    {
        if (mCurrentLevel > 0 && indicatorLists.size() >= mCurrentLevel)
        {
            ViewHolder holder = indicatorLists.get(mCurrentLevel - 1);
            if (holder != null)
            {
                holder.animateOn();
            }
        }
    }

    public void delayedColorUpdate(int updateColor)
    {
        for (int i = 0; i < indicatorLists.size(); i++)
        {
            ViewHolder indicator = indicatorLists.valueAt(i);
            indicator.shouldShowColor(updateColor);
        }
    }

    protected static class ViewHolder
    {
        TextView indicatorTextView;
        ImageView indicatorImageView;
        int mCurrentColor = Color.BLACK;
        int mLevel;
        AchievementDefDTO mAchievementDefDTO;
        private Animator animator;

        public ViewHolder()
        {
            super();
        }

        public void display(AchievementDefDTO achievementDefDTO, int currentLevel)
        {
            this.mAchievementDefDTO = achievementDefDTO;
            this.mLevel = currentLevel;
            display();
        }

        private void display()
        {
            indicatorTextView.setText(mAchievementDefDTO.triggerStr);
            if (mAchievementDefDTO.achievementLevel <= mLevel)
            {
                on();
            }
            else
            {
                off();
            }
        }

        private void on()
        {
            indicatorImageView.setImageResource(R.drawable.ic_achievement_star_on);
            show();
            unBoldText();
            colorText(Color.BLACK);
        }

        private void off()
        {
            indicatorImageView.setImageResource(R.drawable.ic_achievement_star_off);
            show();
            unBoldText();
            colorText(indicatorTextView.getContext().getResources().getColor(R.color.text_secondary));
        }

        private void show()
        {
            indicatorImageView.setVisibility(View.VISIBLE);
            indicatorTextView.setVisibility(View.VISIBLE);
        }

        public void hide()
        {
            indicatorImageView.setVisibility(View.GONE);
            indicatorTextView.setVisibility(View.GONE);
        }

        private void animateOn()
        {
            indicatorImageView.setImageResource(R.drawable.ic_achivement_star_animate);
            AnimationDrawable animationDrawable = (AnimationDrawable) indicatorImageView.getDrawable();
            animationDrawable.start();

            if (animator == null)
            {
                animator = AnimatorInflater.loadAnimator(indicatorImageView.getContext(), R.animator.quest_indicator_scale);
                Animator animatorCopy = animator.clone();
                animator.setTarget(indicatorImageView);
                animatorCopy.setTarget(indicatorTextView);

                animator.start();
                animatorCopy.start();
            }
            colorText();
            boldText();
        }

        private void colorText()
        {
            colorText(mCurrentColor);
        }

        private void colorText(int currentColor)
        {
            indicatorTextView.setTextColor(currentColor);
        }

        private void boldText()
        {
            indicatorTextView.setTypeface(indicatorTextView.getTypeface(), Typeface.BOLD);
        }

        private void unBoldText()
        {
            indicatorTextView.setTypeface(indicatorTextView.getTypeface(), Typeface.NORMAL);
        }

        public void shouldShowColor(int mCurrentColor)
        {
            this.mCurrentColor = mCurrentColor;
            if (mAchievementDefDTO != null && mLevel > 0 && mAchievementDefDTO.achievementLevel == mLevel)
            {
                animateOn();
            }
        }
    }
}
