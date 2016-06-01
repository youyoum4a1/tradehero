package com.ayondo.academy.models;

import com.ayondo.academy.models.leaderboard.ModelsLeaderboardTestModule;
import com.ayondo.academy.models.level.LevelTestModule;
import com.ayondo.academy.models.position.PositionDTOUtilsTest;
import com.ayondo.academy.models.push.PushTestModule;
import dagger.Module;

@Module(
        injects = {
            PositionDTOUtilsTest.class,
        },
        includes = {
                ModelsLeaderboardTestModule.class,
                PushTestModule.class,
                LevelTestModule.class,
        },
        complete = false,
        library = true
)
public class ModelsTestModule
{
}
