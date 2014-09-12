package com.tradehero.th.api.achievement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.api.achievement.key.AchievementDefId;
import java.io.IOException;
import org.jetbrains.annotations.Nullable;

public class AchievementDefDTO implements DTO
{
    public int id;
    public int trigger;
    public String triggerStr;
    public String thName;
    @JsonProperty("virtualdollars")
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
    public int categoryId;

    @JsonIgnore
    public AchievementDefId getAchievementsId()
    {
        return new AchievementDefId(id);
    }

    @Override public String toString()
    {
        return "AchievementDefDTO{" +
                "thName='" + thName + '\'' +
                ", text='" + text + '\'' +
                ", achievementLevel=" + achievementLevel +
                '}';
    }
}
