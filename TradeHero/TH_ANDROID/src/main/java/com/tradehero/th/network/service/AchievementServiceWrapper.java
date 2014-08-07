package com.tradehero.th.network.service;

import com.tradehero.th.api.achievement.UserAchievementDTO;
import com.tradehero.th.api.achievement.UserAchievementId;
import com.tradehero.th.api.level.LevelDefDTOList;
import com.tradehero.th.api.level.key.LevelDefListId;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.retrofit.MiddleCallback;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;

public class AchievementServiceWrapper
{
    @NotNull private final AchievementServiceAsync achievementServiceAsync;
    @NotNull private final AchievementService achievementService;

    @Inject public AchievementServiceWrapper(
            @NotNull AchievementService achievementService,
            @NotNull AchievementServiceAsync achievementServiceAsync
    )
    {
        this.achievementService = achievementService;
        this.achievementServiceAsync = achievementServiceAsync;
    }

    public LevelDefDTOList getLevelDefs(
            @NotNull LevelDefListId levelDefListId)
    {
        return achievementService.getLevelDefs();
    }

    @NotNull public MiddleCallback<LevelDefDTOList> getLevelDefs(
            @NotNull LevelDefListId levelDefListId,
            @Nullable Callback<LevelDefDTOList> callback)
    {
        MiddleCallback<LevelDefDTOList> middleCallback = new BaseMiddleCallback<>(callback);
        achievementServiceAsync.getLevelDefs(middleCallback);
        return middleCallback;
    }

    public UserAchievementDTO getUserAchievementDetails(UserAchievementId userAchievementId)
    {
        return achievementService.getUserAchievementDetails(userAchievementId.key);
    }

    @NotNull public MiddleCallback<UserAchievementDTO> getUserAchievementDetails(
            @NotNull UserAchievementId userAchievementId,
            @Nullable Callback<UserAchievementDTO> callback)
    {
        MiddleCallback<UserAchievementDTO> middleCallback = new BaseMiddleCallback<>(callback);
        achievementServiceAsync.getUserAchievementDetails(userAchievementId.key, middleCallback);
        return middleCallback;
    }
}
