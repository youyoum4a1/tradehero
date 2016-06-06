package com.androidth.general.api.achievement;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.androidth.general.common.persistence.DTO;
import com.androidth.general.api.achievement.key.AchievementCategoryId;
import com.androidth.general.api.users.UserBaseKey;
import java.util.List;

public class AchievementCategoryDTO implements DTO
{
    public int id;
    public String category;
    public String displayName;
    public String badge;
    public String description;
    public int currentUserLevel;

    @JsonProperty("AchievementDefs")
    public List<AchievementDefDTO> achievementDefs;

    @JsonIgnore
    @NonNull public AchievementCategoryId getCategoryId(@NonNull UserBaseKey userBaseKey)
    {
        return new AchievementCategoryId(userBaseKey, id);
    }
}
