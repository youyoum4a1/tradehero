package com.tradehero.th.fragments.achievement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.InjectView;
import com.tradehero.common.widget.QuestIndicatorGroupView;
import com.tradehero.th.R;

public class QuestDialogFragment extends AbstractAchievementDialogFragment
{
    @InjectView(R.id.quest_indicator_group) QuestIndicatorGroupView questIndicatorGroupView;

    protected QuestDialogFragment()
    {
        super();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.quest_dialog_fragment, container, false);
    }

    @Override protected void initView()
    {
        super.initView();
        questIndicatorGroupView.setCurrentCount(userAchievementDTO.contiguousCount);
    }
}
