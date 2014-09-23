package com.tradehero.th.api.level;

import android.os.Bundle;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.utils.broadcast.BroadcastData;
import com.tradehero.th.utils.level.XpModule;
import java.util.ArrayList;
import java.util.List;

public class UserXPAchievementDTO implements DTO, BroadcastData
{
    private static final String KEY_TEXT = UserXPAchievementDTO.class.getName() + ".text";
    private static final String KEY_SUBTEXT = UserXPAchievementDTO.class.getName() + ".subText";
    private static final String KEY_XP_EARNED = UserXPAchievementDTO.class.getName() + ".xpEarned";
    private static final String KEY_XP_TOTAL = UserXPAchievementDTO.class.getName() + ".xpTotal";
    private static final String KEY_MULTIPLIERS = UserXPAchievementDTO.class.getName() + ".multipliers";

    public String text;
    public String subText;
    public int xpEarned;
    public int xpTotal;
    public List<UserXPMultiplierDTO> multipliers;

    public UserXPAchievementDTO()
    {
        super();
    }

    public UserXPAchievementDTO(Bundle b)
    {
        if (b.containsKey(KEY_TEXT))
        {
            text = b.getString(KEY_TEXT);
        }

        if (b.containsKey(KEY_SUBTEXT))
        {
            subText = b.getString(KEY_SUBTEXT);
        }

        if (b.containsKey(KEY_XP_TOTAL))
        {
            xpTotal = b.getInt(KEY_XP_TOTAL);
        }

        if (b.containsKey(KEY_XP_EARNED))
        {
            xpEarned = b.getInt(KEY_XP_EARNED);
        }

        if (b.containsKey(KEY_MULTIPLIERS))
        {
            ArrayList<Bundle> bundles = b.getParcelableArrayList(KEY_MULTIPLIERS);
            multipliers = new ArrayList<>();
            for (Bundle multi : bundles)
            {
                multipliers.add(new UserXPMultiplierDTO(multi));
            }
        }
    }

    @Override public Bundle getArgs()
    {
        Bundle b = new Bundle();
        b.putString(KEY_TEXT, text);
        b.putString(KEY_SUBTEXT, subText);
        b.putInt(KEY_XP_TOTAL, xpTotal);
        b.putInt(KEY_XP_EARNED, xpEarned);

        if (multipliers != null && !multipliers.isEmpty())
        {
            ArrayList<Bundle> multis = new ArrayList<>();
            for (UserXPMultiplierDTO multiplierDTO : multipliers)
            {
                multis.add(multiplierDTO.getArgs());
            }
            b.putParcelableArrayList(KEY_MULTIPLIERS, multis);
        }
        return b;
    }

    @Override public String getBroadcastBundleKey()
    {
        return XpModule.KEY_XP_BROADCAST;
    }

    @Override public String getBroadcastIntentActionName()
    {
        return XpModule.XP_INTENT_ACTION_NAME;
    }

    public int getBaseXp()
    {
        return xpTotal - xpEarned;
    }
}