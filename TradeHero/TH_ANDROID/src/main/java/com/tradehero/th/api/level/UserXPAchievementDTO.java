package com.tradehero.th.api.level;

import android.os.Bundle;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.utils.broadcast.BroadcastData;
import com.tradehero.th.utils.level.XpModule;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UserXPAchievementDTO implements DTO, BroadcastData
{
    private static final String KEY_TEXT = UserXPAchievementDTO.class.getName() + ".text";
    private static final String KEY_SUBTEXT = UserXPAchievementDTO.class.getName() + ".subText";
    private static final String KEY_XP_EARNED = UserXPAchievementDTO.class.getName() + ".xpEarned";
    private static final String KEY_XP_TOTAL = UserXPAchievementDTO.class.getName() + ".xpTotal";
    private static final String KEY_MULTIPLIERS = UserXPAchievementDTO.class.getName() + ".multiplier";

    public String text;
    public String subText;
    public int xpEarned;
    public int xpTotal;
    @Nullable public List<UserXPMultiplierDTO> multiplier;

    //<editor-fold desc="Constructors">
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
            multiplier = new ArrayList<>();
            for (Bundle multi : bundles)
            {
                multiplier.add(new UserXPMultiplierDTO(multi));
            }
        }
    }
    //</editor-fold>

    @NotNull @Override public Bundle getArgs()
    {
        Bundle b = new Bundle();
        b.putString(KEY_TEXT, text);
        b.putString(KEY_SUBTEXT, subText);
        b.putInt(KEY_XP_TOTAL, xpTotal);
        b.putInt(KEY_XP_EARNED, xpEarned);

        if (multiplier != null && !multiplier.isEmpty())
        {
            ArrayList<Bundle> multis = new ArrayList<>();
            for (UserXPMultiplierDTO multiplierDTO : multiplier)
            {
                multis.add(multiplierDTO.getArgs());
            }
            b.putParcelableArrayList(KEY_MULTIPLIERS, multis);
        }
        return b;
    }

    @NotNull @Override public String getBroadcastBundleKey()
    {
        return XpModule.KEY_XP_BROADCAST;
    }

    @NotNull @Override public String getBroadcastIntentActionName()
    {
        return XpModule.XP_INTENT_ACTION_NAME;
    }

    public int getBaseXp()
    {
        if(multiplier != null && !multiplier.isEmpty())
        {
            int baseXp = xpTotal;
            for (UserXPMultiplierDTO multiplierDTO : multiplier)
            {
                baseXp -= xpEarned * multiplierDTO.multiplier;
            }
            return baseXp;
        }
        else
        {
            return xpTotal - xpEarned;
        }
    }
}