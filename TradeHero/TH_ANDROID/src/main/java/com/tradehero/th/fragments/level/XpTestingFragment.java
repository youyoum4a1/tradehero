package com.tradehero.th.fragments.level;

import android.os.Bundle;
import android.text.TextUtils;
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
import com.tradehero.th.api.level.UserXPMultiplierDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.utils.broadcast.BroadcastUtils;
import java.util.ArrayList;
import java.util.List;
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

    @Inject BroadcastUtils broadcastUtils;

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
            int xp = Integer.parseInt(xpEarned.getText().toString());

            UserXPAchievementDTO userXPAchievementDTO = new UserXPAchievementDTO();
            userXPAchievementDTO.text = xpReason.getText().toString();
            userXPAchievementDTO.xp = xp;
            userXPAchievementDTO.originalXP = Integer.parseInt(xpFrom.getText().toString());
            userXPAchievementDTO.multipliers = new ArrayList<>();
            xp = parseMultipliers(userXPAchievementDTO.multipliers, xp, xpM1Reason, xpM1Value);
            xp = parseMultipliers(userXPAchievementDTO.multipliers, xp, xpM2Reason, xpM2Value);
            xp = parseMultipliers(userXPAchievementDTO.multipliers, xp, xpM3Reason, xpM3Value);

            broadcastUtils.enqueue(userXPAchievementDTO);
        } catch (NumberFormatException e)
        {
            THToast.show(e.getMessage());
        }
    }

    private int parseMultipliers(List<UserXPMultiplierDTO> list, int baseXP, EditText reason, EditText value)
    {
        if (!TextUtils.isEmpty(reason.getText()) && !TextUtils.isEmpty(value.getText()))
        {
            UserXPMultiplierDTO m = new UserXPMultiplierDTO();
            m.text = reason.getText().toString();
            m.multiplier = Integer.parseInt(value.getText().toString());
            m.xpTotal = baseXP * m.multiplier;
            list.add(m);
            return m.xpTotal;
        }
        return baseXP;
    }
}
