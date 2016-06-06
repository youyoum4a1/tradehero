package com.androidth.general.api.level;

import android.os.Bundle;
import com.androidth.general.common.persistence.DTO;

public class UserXPMultiplierDTO implements DTO
{
    private static final String KEY_TEXT = UserXPMultiplierDTO.class.getName() + ".text";
    private static final String KEY_XP_TOTAL = UserXPMultiplierDTO.class.getName() + ".xpTotal";
    private static final String KEY_MULTIPLIER = UserXPMultiplierDTO.class.getName() + ".multiplier";

    public String text;
    public int xpTotal;
    public int multiplier;

    //<editor-fold desc="Constructors">
    public UserXPMultiplierDTO()
    {
        super();
    }

    public UserXPMultiplierDTO(Bundle b)
    {
        if(b.containsKey(KEY_TEXT))
        {
            text = b.getString(KEY_TEXT);
        }

        if(b.containsKey(KEY_XP_TOTAL))
        {
            xpTotal = b.getInt(KEY_XP_TOTAL);
        }

        if(b.containsKey(KEY_MULTIPLIER))
        {
            multiplier = b.getInt(KEY_MULTIPLIER);
        }
    }
    //</editor-fold>

    public Bundle getArgs()
    {
        Bundle b = new Bundle();
        b.putString(KEY_TEXT, text);
        b.putInt(KEY_XP_TOTAL, xpTotal);
        b.putInt(KEY_MULTIPLIER, multiplier);
        return b;
    }
}