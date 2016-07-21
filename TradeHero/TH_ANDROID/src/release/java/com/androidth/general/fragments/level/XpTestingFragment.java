package com.androidth.general.fragments.level;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.BindView;
import butterknife.OnClick;
import com.androidth.general.common.utils.THToast;
import com.androidth.general.R;
import com.androidth.general.api.level.UserXPAchievementDTO;
import com.androidth.general.api.level.UserXPMultiplierDTO;
import com.androidth.general.api.level.UserXPMultiplierDTOList;
import com.androidth.general.fragments.base.BaseFragment;
import com.androidth.general.utils.broadcast.BroadcastUtils;
import java.util.List;
import javax.inject.Inject;

public class XpTestingFragment extends BaseFragment
{
    @BindView(R.id.xp_test_reason) EditText xpReason;
    @BindView(R.id.xp_test_from) EditText xpFrom;
    @BindView(R.id.xp_test_earned) EditText xpEarned;
    @BindView(R.id.xp_test_multiplier_1_reason) EditText xpM1Reason;
    @BindView(R.id.xp_test_multiplier_1_value) EditText xpM1Value;
    @BindView(R.id.xp_test_multiplier_2_reason) EditText xpM2Reason;
    @BindView(R.id.xp_test_multiplier_2_value) EditText xpM2Value;
    @BindView(R.id.xp_test_multiplier_3_reason) EditText xpM3Reason;
    @BindView(R.id.xp_test_multiplier_3_value) EditText xpM3Value;
    @BindView(R.id.xp_test_launch) Button launch;

    @Inject BroadcastUtils broadcastUtils;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_xp_testing, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.xp_test_launch)
    public void onLaunch()
    {
        try
        {
            int xp = Integer.parseInt(xpEarned.getText().toString());

            UserXPAchievementDTO userXPAchievementDTO = new UserXPAchievementDTO();
            userXPAchievementDTO.text = xpReason.getText().toString();
            userXPAchievementDTO.xpEarned = xp;
            userXPAchievementDTO.xpTotal = Integer.parseInt(xpFrom.getText().toString()) + xp;
            userXPAchievementDTO.multiplier = new UserXPMultiplierDTOList();
            xp = parseMultipliers(userXPAchievementDTO.multiplier, xp, xpM1Reason, xpM1Value);
            xp = parseMultipliers(userXPAchievementDTO.multiplier, xp, xpM2Reason, xpM2Value);
            xp = parseMultipliers(userXPAchievementDTO.multiplier, xp, xpM3Reason, xpM3Value);

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
            return baseXP;
        }
        return baseXP;
    }
}
