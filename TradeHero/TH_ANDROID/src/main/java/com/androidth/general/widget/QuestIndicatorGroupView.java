package com.androidth.general.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import com.androidth.general.R;
import com.androidth.general.api.achievement.QuestBonusDTO;
import java.util.ArrayList;
import java.util.List;

public class QuestIndicatorGroupView extends LinearLayout
{
    @NonNull List<QuestIndicatorView> questIndicatorViews = new ArrayList<>();
    private ValueAnimator revealNextAnimator;
    @Nullable private QuestBonusDTO mNextBonusDTO;
    private int mCurrentCount;
    private int mCurrentColor;

    //<editor-fold desc="Constructors">
    @SuppressWarnings("UnusedDeclaration")
    public QuestIndicatorGroupView(Context context)
    {
        super(context);
    }

    @SuppressWarnings("UnusedDeclaration")
    public QuestIndicatorGroupView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @SuppressWarnings("UnusedDeclaration")
    public QuestIndicatorGroupView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        findIndicators();
    }

    private void findIndicators()
    {
        String tag = getContext().getString(R.string.tag_quest_indicator);
        for (int i = 0; i < getChildCount(); i++)
        {
            View v = getChildAt(i);
            if (v.getTag() != null && v.getTag().equals(tag) && v instanceof QuestIndicatorView)
            {
                questIndicatorViews.add((QuestIndicatorView) v);
            }
        }
    }

    public void setQuestBonusDef(@NonNull List<QuestBonusDTO> questBonusDef, int currentCount)
    {
        this.mCurrentCount = currentCount;
        updateDisplay(questBonusDef);
        hideUndefinedIndicators(questBonusDef);
        detectNextIndicator(questBonusDef);
    }

    public void revealNext()
    {
        post(new Runnable()
        {
            @Override public void run()
            {
                if (mNextBonusDTO != null &&
                        !questIndicatorViews.isEmpty())
                {
                    QuestIndicatorView questIndicatorView = questIndicatorViews.get(0);
                    QuestIndicatorView nextQuestIndicatorView = createNextIndicatorView(questIndicatorView);

                    final int originalWidth = questIndicatorView.getMeasuredWidth();

                    revealNextAnimator = ValueAnimator.ofInt(originalWidth, 0);
                    revealNextAnimator.addUpdateListener(createAnimUpdateListener(questIndicatorView, nextQuestIndicatorView, originalWidth));
                    revealNextAnimator.addListener(createAnimListener(questIndicatorView, nextQuestIndicatorView));
                    revealNextAnimator.setInterpolator(new AccelerateInterpolator(3));
                    revealNextAnimator.setStartDelay(getResources().getInteger(R.integer.quest_indicator_animation_start_delay));
                    revealNextAnimator.setDuration(getResources().getInteger(R.integer.quest_indicator_animation_duration));
                    revealNextAnimator.start();
                }
            }

            private QuestIndicatorView createNextIndicatorView(@NonNull View source)
            {
                QuestIndicatorView indicatorView =
                        new QuestIndicatorView(getContext());
                indicatorView.setLayoutParams(new LayoutParams(source.getLayoutParams()));
                indicatorView.display(mNextBonusDTO, mCurrentCount);
                return indicatorView;
            }

            private ValueAnimator.AnimatorUpdateListener createAnimUpdateListener(
                    final QuestIndicatorView questIndicatorView,
                    final QuestIndicatorView nextQuestIndicatorView,
                    final int originalWidth)
            {
                return new ValueAnimator.AnimatorUpdateListener()
                {
                    @Override public void onAnimationUpdate(ValueAnimator valueAnimator)
                    {
                        int width = (Integer) valueAnimator.getAnimatedValue();
                        float alpha = (float) width / (float) originalWidth;

                        questIndicatorView.getLayoutParams().width = width;
                        questIndicatorView.setAlpha(alpha);

                        int targetWidth = originalWidth - width;
                        float targetAlpha = 1f - alpha;

                        nextQuestIndicatorView.getLayoutParams().width = targetWidth;
                        nextQuestIndicatorView.setAlpha(targetAlpha);

                        questIndicatorView.requestLayout();
                        nextQuestIndicatorView.requestLayout();
                    }
                };
            }

            private ValueAnimator.AnimatorListener createAnimListener(
                    @NonNull final QuestIndicatorView questIndicatorView,
                    @NonNull final QuestIndicatorView nextQuestIndicatorView)
            {
                return new AnimatorListenerAdapter()
                {
                    @Override public void onAnimationStart(Animator animation)
                    {
                        super.onAnimationStart(animation);
                        ((LayoutParams) questIndicatorView.getLayoutParams()).weight = 0;

                        nextQuestIndicatorView.getLayoutParams().width = 0;
                        ((LayoutParams) nextQuestIndicatorView.getLayoutParams()).weight = 0;

                        addView(nextQuestIndicatorView, nextQuestIndicatorView.getLayoutParams());
                        questIndicatorViews.add(nextQuestIndicatorView);
                    }

                    @Override public void onAnimationEnd(Animator animation)
                    {
                        super.onAnimationEnd(animation);
                        removeView(questIndicatorView);
                        questIndicatorViews.remove(questIndicatorView);
                    }
                };
            }
        });
    }

    @Override protected void onDetachedFromWindow()
    {
        getHandler().removeCallbacks(null);

        if (revealNextAnimator != null)
        {
            revealNextAnimator.removeAllUpdateListeners();
            revealNextAnimator.removeAllListeners();
            revealNextAnimator.cancel();
            revealNextAnimator = null;
        }

        questIndicatorViews.clear();

        mNextBonusDTO = null;

        super.onDetachedFromWindow();
    }

    public void delayedColorUpdate(int updateColor)
    {
        this.mCurrentColor = updateColor;
        for (QuestIndicatorView questIndicatorView : questIndicatorViews)
        {
            questIndicatorView.shouldShowColor(mCurrentColor);
        }
    }

    private void updateDisplay(@NonNull List<QuestBonusDTO> questBonusDTOs)
    {
        for (int i = 0; i < questBonusDTOs.size() && i < questIndicatorViews.size(); i++)
        {
            QuestIndicatorView questIndicatorView = questIndicatorViews.get(i);
            QuestBonusDTO questBonusDTO = questBonusDTOs.get(i);

            questIndicatorView.display(questBonusDTO, mCurrentCount);
        }
    }

    private void hideUndefinedIndicators(@NonNull List<QuestBonusDTO> questBonusDef)
    {
        if (questBonusDef.size() < questIndicatorViews.size())
        {
            for (int i = questBonusDef.size(); i < questIndicatorViews.size(); i++)
            {
                QuestIndicatorView viewHolder = questIndicatorViews.get(i);
                viewHolder.setVisibility(View.GONE);
            }
        }
    }

    private void detectNextIndicator(@NonNull List<QuestBonusDTO> questBonusDef)
    {
        if (questBonusDef.size() > questIndicatorViews.size())
        {
            mNextBonusDTO = questBonusDef.get(questBonusDef.size() - 1);
        }
    }

    public int getNumberOfIndicators()
    {
        return questIndicatorViews.size();
    }
}
