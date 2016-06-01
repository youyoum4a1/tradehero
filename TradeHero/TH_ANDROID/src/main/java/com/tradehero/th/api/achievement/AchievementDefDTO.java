package com.ayondo.academy.api.achievement;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.common.persistence.DTO;
import com.ayondo.academy.api.achievement.key.AchievementDefId;

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
    @NonNull public String hexColor;
    @NonNull public String header;
    public int contiguousMax;
    public boolean isQuest;
    public int categoryId;

    @JsonIgnore
    @NonNull public AchievementDefId getAchievementsId()
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
