package com.tradehero.th.api.achievement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.utils.THJsonAdapter;
import java.io.IOException;
import org.jetbrains.annotations.Nullable;

public class AchievementsDTO implements DTO
{
    public int id;
    public int trigger;
    public String thName;
    public double virtualDollars;
    public String visual;
    public String text;
    @Nullable public String subText;
    public int achievementLevel;
    public String category;
    public String hexColor;
    public String header;
    public int contiguousMax;
    public boolean isQuest;

    @JsonIgnore
    public AchievementsId getAchievementsId()
    {
        return new AchievementsId(id);
    }

    @Override
    public String toString()
    {
        try
        {
            return THJsonAdapter.getInstance().toStringBody(this);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "Failed to json";
        }
    }
}
