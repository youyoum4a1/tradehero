package com.tradehero.th.fragments.achievement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.R;
import com.tradehero.th.api.achievement.AchievementCategoryDTO;
import com.tradehero.th.api.achievement.AchievementCategoryId;

public class AchievementDialogFragment extends AbstractAchievementDialogFragment
{
    @InjectView(R.id.btn_achievement_share) Button btnShare;

    @InjectView(R.id.achievement_progress_indicator) AchievementProgressIndicator achievementProgressIndicator;

    private DTOCacheNew.Listener<AchievementCategoryId, AchievementCategoryDTO> mCategoryListener;

    protected AchievementDialogFragment()
    {
        super();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.achievement_dialog_fragment, container, false);
    }

    @Override protected void init()
    {
        super.init();
    }

    @Override protected void initView()
    {
        super.initView();
        getView().post(
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        achievementProgressIndicator.animateCurrentLevel();
                    }
                }
        );
    }

    @Override public void onDestroyView()
    {
        super.onDestroyView();
    }

    @OnClick(R.id.btn_achievement_share)
    public void onShareClicked()
    {

    }
}
