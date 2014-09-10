package com.tradehero.th.fragments.achievement;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.th.R;
import com.tradehero.th.api.achievement.AchievementDefDTO;
import java.util.List;

public class AchievementProgressIndicator extends LinearLayout
{
    @InjectView(R.id.achievement_indicator_image_container) ViewGroup imageContainer;
    @InjectView(R.id.achievement_indicator_text_container) ViewGroup textContainer;

    SparseArray<ViewHolder> indicatorLists = new SparseArray<>();
    private int mCurrentLevel = 0;

    public AchievementProgressIndicator(Context context)
    {
        super(context);
    }

    public AchievementProgressIndicator(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AchievementProgressIndicator(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
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
        mCurrentLevel = currentUserLevel;
        updateDisplay(achievementDefs, currentUserLevel);
        hideUndefinedIndicators(achievementDefs);
    }

    private void updateDisplay(List<AchievementDefDTO> achievementDefs, int currentUserLevel)
    {
        for (int i = 0; i < achievementDefs.size() && i < indicatorLists.size(); i++)
        {
            ViewHolder viewHolder = indicatorLists.get(i);
            AchievementDefDTO achievementDefDTO = achievementDefs.get(i);

            viewHolder.display(achievementDefDTO);

            if (achievementDefDTO.achievementLevel <= currentUserLevel)
            {
                viewHolder.on();
            }
            else
            {
                viewHolder.off();
            }
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

    protected static class ViewHolder
    {
        TextView indicatorTextView;
        ImageView indicatorImageView;

        public ViewHolder()
        {
            super();
        }

        public void display(AchievementDefDTO achievementDefDTO)
        {
            indicatorTextView.setText(achievementDefDTO.triggerStr);
        }

        public void on()
        {
            indicatorImageView.setImageResource(R.drawable.ic_achievement_star_on);
            show();
        }

        public void off()
        {
            indicatorImageView.setImageResource(R.drawable.ic_achievement_star_off);
            show();
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

        public void animateOn()
        {
            indicatorImageView.setImageResource(R.drawable.ic_achivement_star_animate);
            AnimationDrawable animationDrawable = (AnimationDrawable) indicatorImageView.getDrawable();
            animationDrawable.start();

            Animator animator = AnimatorInflater.loadAnimator(indicatorImageView.getContext(), R.animator.quest_indicator_scale);
            Animator animatorCopy = animator.clone();

            animator.setTarget(indicatorImageView);
            animatorCopy.setTarget(indicatorTextView);

            animator.start();
            animatorCopy.start();
        }
    }
}
