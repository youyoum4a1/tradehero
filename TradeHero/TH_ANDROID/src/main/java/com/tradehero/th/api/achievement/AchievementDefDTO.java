package com.tradehero.th.api.achievement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.utils.THJsonAdapter;
import java.io.IOException;
import org.jetbrains.annotations.Nullable;

public class AchievementDefDTO implements DTO
{
    int id;
    int trigger;
    String thName;
    double virtualDollars;
    String visual;
    String text;
    @Nullable String subText;
    int achievementLevel;
    String category;
    String hexColor;

    @JsonIgnore
    public AchievementDefDTOKey getAchievementDefDTOKey()
    {
        return new AchievementDefDTOKey(id);
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
