package com.tradehero.th.api.level;

import android.os.Bundle;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.utils.broadcast.BroadcastData;
import java.util.List;

public class UserXPAchievementDTO implements DTO, BroadcastData
{
    private static final String KEY_TEXT = UserXPAchievementDTO.class.getName()+".text";
    private static final String KEY_SUBTEXT = UserXPAchievementDTO.class.getName()+".subText";
    private static final String KEY_XP_FROM = UserXPAchievementDTO.class.getName()+".xpFrom";
    private static final String KEY_XP_EARNED = UserXPAchievementDTO.class.getName()+".xpEarned";

    public String text;
    public String subText;
    public int xpFrom;
    public int xpEarned;
    public List<UserXPMultiplierDTO> multipliers;

    public UserXPAchievementDTO()
    {
        super();
    }

    public UserXPAchievementDTO(Bundle b)
    {
        if(b.containsKey(KEY_TEXT))
        {
            text = b.getString(KEY_TEXT);
        }

        if(b.containsKey(KEY_SUBTEXT))
        {
            subText = b.getString(KEY_SUBTEXT);
        }

        if(b.containsKey(KEY_XP_EARNED))
        {
            xpEarned = b.getInt(KEY_XP_EARNED);
        }

        if(b.containsKey(KEY_XP_FROM))
        {
            xpFrom = b.getInt(KEY_XP_FROM);
        }
    }

    @Override public Bundle getArgs()
    {
        Bundle b = new Bundle();
        b.putString(KEY_TEXT, text);
        b.putString(KEY_SUBTEXT, subText);
        b.putInt(KEY_XP_EARNED, xpEarned);
        b.putInt(KEY_XP_FROM, xpFrom);
        return b;
    }
}