package com.ayondo.academy.api.achievement;

import android.support.annotation.NonNull;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.common.persistence.DTO;
import com.ayondo.academy.api.achievement.key.AchievementCategoryId;
import com.ayondo.academy.api.users.UserBaseKey;
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
