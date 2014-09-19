package com.tradehero.th.api.level;

import android.os.Bundle;
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.utils.broadcast.BroadcastData;

public class UserXPMultiplierDTO implements DTO
{
    public String text;
    public int xpTotal;
    public int multiplier;
}