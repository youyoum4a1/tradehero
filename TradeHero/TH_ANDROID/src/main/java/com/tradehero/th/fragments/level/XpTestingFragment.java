package com.tradehero.th.fragments.level;

import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.level.UserXPAchievementDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.utils.broadcast.BroadcastUtils;
import com.tradehero.th.utils.level.XpModule;
import javax.inject.Inject;

public class XpTestingFragment extends DashboardFragment
{
    @InjectView(R.id.xp_test_reason) EditText xpReason;
    @InjectView(R.id.xp_test_from) EditText xpFrom;
    @InjectView(R.id.xp_test_earned) EditText xpEarned;
    @InjectView(R.id.xp_test_multiplier_1_reason) EditText xpM1Reason;
    @InjectView(R.id.xp_test_multiplier_1_value) EditText xpM1Value;
    @InjectView(R.id.xp_test_multiplier_2_reason) EditText xpM2Reason;
    @InjectView(R.id.xp_test_multiplier_2_value) EditText xpM2Value;
    @InjectView(R.id.xp_test_multiplier_3_reason) EditText xpM3Reason;
    @InjectView(R.id.xp_test_multiplier_3_value) EditText xpM3Value;
    @InjectView(R.id.xp_test_launch) Button launch;

    @Inject LocalBroadcastManager localBroadcastManager;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_xp_testing, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    @OnClick(R.id.xp_test_launch)
    public void onLaunch()
    {
        try
        {
            UserXPAchievementDTO userXPAchievementDTO = new UserXPAchievementDTO();
            userXPAchievementDTO.text = xpReason.getText().toString();
            userXPAchievementDTO.xpEarned = Integer.parseInt(xpEarned.getText().toString());
            userXPAchievementDTO.xpFrom = Integer.parseInt(xpFrom.getText().toString());
            BroadcastUtils utils = new BroadcastUtils(userXPAchievementDTO, localBroadcastManager, XpModule.XP_INTENT_ACTION_NAME, XpModule.XP_BROADCAST_KEY);
            utils.start();
        }catch (NumberFormatException e)
        {
            THToast.show(e.getMessage());
        }
    }
}
