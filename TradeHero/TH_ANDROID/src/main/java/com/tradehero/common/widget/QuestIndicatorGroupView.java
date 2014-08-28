package com.tradehero.common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import com.tradehero.th.R;
import com.tradehero.th.api.achievement.QuestBonusDTO;
import java.util.ArrayList;
import java.util.List;

public class QuestIndicatorGroupView extends LinearLayout
{
    List<QuestIndicatorView> questIndicatorViews = new ArrayList<>();

    public QuestIndicatorGroupView(Context context)
    {
        super(context);
    }

    public QuestIndicatorGroupView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public QuestIndicatorGroupView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

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

    public void setQuestBonusDef(List<QuestBonusDTO> questBonusDef, int currentCount)
    {
        updateDisplay(questBonusDef, currentCount);
        hideUndefinedIndicators(questBonusDef);
    }

    private void updateDisplay(List<QuestBonusDTO> questBonusDTOs, int currentContigousCount)
    {
        for (int i = 0; i < questBonusDTOs.size() && i < questIndicatorViews.size(); i++)
        {
            QuestIndicatorView questIndicatorView = questIndicatorViews.get(i);
            QuestBonusDTO questBonusDTO = questBonusDTOs.get(i);

            questIndicatorView.display(questBonusDTO);

            if (questBonusDTO.level < currentContigousCount)
            {
                questIndicatorView.on();
            }
            else if (questBonusDTO.level == currentContigousCount)
            {
                questIndicatorView.animateOn();
            }
            else
            {
                questIndicatorView.off();
            }
        }
    }

    private void hideUndefinedIndicators(List<QuestBonusDTO> questBonusDef)
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
}
