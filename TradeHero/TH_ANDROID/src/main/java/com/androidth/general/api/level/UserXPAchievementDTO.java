package com.androidth.general.api.level;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.utils.broadcast.BroadcastData;
import java.util.ArrayList;
import timber.log.Timber;

import static com.androidth.general.utils.broadcast.BroadcastConstants.KEY_XP_BROADCAST;
import static com.androidth.general.utils.broadcast.BroadcastConstants.XP_INTENT_ACTION_NAME;

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
    @Nullable public UserXPMultiplierDTOList multiplier;

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
            multiplier = new UserXPMultiplierDTOList();
            for (Bundle multi : bundles)
            {
                multiplier.add(new UserXPMultiplierDTO(multi));
            }
        }
    }
    //</editor-fold>

    @NonNull @Override public Bundle getArgs()
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

    @NonNull @Override public String getBroadcastBundleKey()
    {
        return KEY_XP_BROADCAST;
    }

    @NonNull @Override public String getBroadcastIntentActionName()
    {
        return XP_INTENT_ACTION_NAME;
    }

    public int getBaseXp()
    {
        int baseXp;
        if(multiplier != null && !multiplier.isEmpty())
        {
            baseXp = xpTotal;
            for (UserXPMultiplierDTO multiplierDTO : multiplier)
            {
                baseXp -= xpEarned * multiplierDTO.multiplier;
            }
            return baseXp;
        }
        else
        {
            baseXp = xpTotal - xpEarned;
        }
        if (baseXp < 0)
        {
            Timber.e(new Exception(), "BaseXp=0 for %s", this);
        }
        return baseXp;
    }

    @Override public String toString()
    {
        return "UserXPAchievementDTO{" +
                "text='" + text + '\'' +
                ", subText='" + subText + '\'' +
                ", xpEarned=" + xpEarned +
                ", xpTotal=" + xpTotal +
                ", multiplier=" + multiplier +
                '}';
    }
}